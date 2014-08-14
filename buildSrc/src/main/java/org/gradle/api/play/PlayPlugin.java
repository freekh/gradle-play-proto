package org.gradle.api.play; //TODO: change? does not feel right to have api? remember properties file

import org.gradle.StartParameter;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.internal.initialization.ClassLoaderScope;
import org.gradle.api.internal.initialization.RootClassLoaderScope;
import org.gradle.api.play.tasks.Reloader;
import org.gradle.api.play.tasks.RoutesCompile;
import org.gradle.api.play.tasks.TwirlTemplateCompile;
import org.gradle.api.plugins.scala.ScalaPlugin;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.scala.ScalaCompile;
import org.gradle.cache.CacheRepository;
import org.gradle.cache.PersistentCache;
import org.gradle.cache.internal.FileLockManager;
import org.gradle.initialization.GradleLauncher;
import org.gradle.initialization.GradleLauncherFactory;
import org.gradle.initialization.buildsrc.BuildSourceBuilder;
import org.gradle.initialization.buildsrc.BuildSrcBuildListenerFactory;
import org.gradle.initialization.buildsrc.BuildSrcUpdateFactory;
import org.gradle.internal.classpath.ClassPath;
import org.gradle.internal.classpath.DefaultClassPath;
import org.gradle.util.GradleVersion;
import play.api.mvc.Codec$;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.gradle.cache.internal.filelock.LockOptionsBuilder.mode;

public class PlayPlugin implements Plugin<Project> {

    private final CacheRepository cacheRepository;
    private final GradleLauncherFactory gradleLauncherFactory;

    @Inject
    public PlayPlugin(GradleLauncherFactory gradleLauncherFactory, CacheRepository cacheRepository) {
        //ClassLoaderScope classLoaderScope, CacheRepository cacheRepository
        this.gradleLauncherFactory = gradleLauncherFactory;
        this.cacheRepository = cacheRepository;
    }

    @Override
    public void apply(final Project project) {
        File defaultGeneratedDir = new File("src-generated"); //TODO: should be under build/src-generated

        configureTwirlTemplateCompile(project, defaultGeneratedDir);
        configureRoutesCompile(project, defaultGeneratedDir);
        configureReloader(project);
    }


    private void configureReloader(Project project) {
        TaskContainer tasks = project.getTasks();
        Task reloader = tasks.create("play-run", Reloader.class);
        reloader.setProperty("gradleLauncherFactory", gradleLauncherFactory);
        reloader.setProperty("cacheRepository", cacheRepository);
    }

    private void configureTwirlTemplateCompile(final Project project, File defaultGeneratedDir) {
        project.getPlugins().apply(ScalaPlugin.class);
        TaskContainer tasks = project.getTasks();
        Task twirlTemplateCompile = tasks.create("twirlTemplateCompile", TwirlTemplateCompile.class);

        //Defaults: TODO: use Spec instead like language-jvm, or sourceSets?
        twirlTemplateCompile.setProperty("sourceDirectory", new File("./app/"));
        twirlTemplateCompile.setProperty("codec", "UTF-8");
        twirlTemplateCompile.setProperty("generatedDirectory", defaultGeneratedDir);
        twirlTemplateCompile.setProperty("formatterType", "play.twirl.api.HtmlFormat");
        twirlTemplateCompile.setProperty("additionalImports", "import controllers._");

        linkToScalaCompiler(defaultGeneratedDir, tasks, twirlTemplateCompile);
    }

    private void configureRoutesCompile(final Project project, File defaultGeneratedDir) {
        project.getPlugins().apply(ScalaPlugin.class);
        TaskContainer tasks = project.getTasks();
        Task routesCompile = tasks.create("playRoutesCompile", RoutesCompile.class);

        List<File> inputFiles = new ArrayList<>();
        inputFiles.add(new File(project.getRootDir(), "conf/routes")); //TODO: not the right way?

        List<File> additionalImports = new ArrayList<>();

        //Defaults: TODO: use Spec instead like language-jvm, or sourceSets?
        routesCompile.setProperty("inputFiles", inputFiles);
        routesCompile.setProperty("generatedDirectory", defaultGeneratedDir);
        routesCompile.setProperty("additionalImports", additionalImports);

        linkToScalaCompiler(defaultGeneratedDir, tasks, routesCompile);
    }

    private void linkToScalaCompiler(File defaultGeneratedDir, TaskContainer tasks, Task routesCompile) {
        TaskCollection<ScalaCompile> scalaCompilers = tasks.withType(ScalaCompile.class);
        ScalaCompile scalaCompile = scalaCompilers.getAt("compileScala");

        scalaCompile.source(defaultGeneratedDir); //TODO: this is wrong, we want to link scala compile source to generated directory not set it like this
        scalaCompile.dependsOn(routesCompile);
    }
}
