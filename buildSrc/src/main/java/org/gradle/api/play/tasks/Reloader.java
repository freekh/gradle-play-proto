package org.gradle.api.play.tasks;

import org.gradle.BuildResult;
import org.gradle.StartParameter;
import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.initialization.ClassLoaderScope;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.cache.CacheRepository;
import org.gradle.cache.PersistentCache;
import org.gradle.cache.internal.FileLockManager;
import org.gradle.initialization.GradleLauncher;
import org.gradle.initialization.GradleLauncherFactory;
import org.gradle.initialization.buildsrc.BuildSourceBuilder;
import org.gradle.initialization.buildsrc.BuildSrcBuildListenerFactory;
import org.gradle.initialization.buildsrc.BuildSrcUpdateFactory;
import org.gradle.internal.classpath.DefaultClassPath;
import org.gradle.util.GradleVersion;
import play.api.PlayException;
import play.core.BuildDocHandler;
import play.core.BuildLink;
import play.core.server.NettyServer;
import play.docs.BuildDocHandlerFactory;
import scala.collection.script.Start;
import scala.util.Right$;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

import static org.gradle.cache.internal.filelock.LockOptionsBuilder.mode;

public class Reloader extends DefaultTask {

    @Input
    public void setGradleLauncherFactory(GradleLauncherFactory gradleLauncherFactory) {
        this.gradleLauncherFactory = gradleLauncherFactory;
    }

    private GradleLauncherFactory gradleLauncherFactory;

    @Input
    public void setCacheRepository(CacheRepository cacheRepository) {
        this.cacheRepository = cacheRepository;
    }

    private CacheRepository cacheRepository;

    private final Logger logger = Logging.getLogger(this.getClass());

    PersistentCache createCache(StartParameter startParameter) {
        return cacheRepository
                .cache(new File(startParameter.getCurrentDir(), ".gradle/noVersion/buildSrc"))
                .withCrossVersionCache()
                .withDisplayName("buildSrc state cache")
                .withLockOptions(mode(FileLockManager.LockMode.None).useCrossVersionImplementation())
                .withProperties(Collections.singletonMap("gradle.version", GradleVersion.current().getVersion()))
                .open();
    }

    private DefaultClassPath build() {
        StartParameter startParameter = getProject().getGradle().getStartParameter();
        GradleLauncher gradleLauncher = gradleLauncherFactory.newInstance(startParameter);
        boolean rebuild = false;
        BuildSrcBuildListenerFactory.Listener listener = new BuildSrcBuildListenerFactory.Listener(rebuild);
        gradleLauncher.addListener(listener);
        BuildResult result = gradleLauncher.run();
        Throwable ex = result.getFailure();
        if (ex != null) {
            throw new PlayException("Buhu!", "bla bla bla", ex);
        }

        return new DefaultClassPath(listener.getRuntimeClasspath());
    }

//    StartParameter startParameter = getProject().getGradle().getStartParameter();
//    GradleLauncher gradleLauncher = gradleLauncherFactory.newInstance(startParameter);
//    final PersistentCache buildSrcCache = createCache(startParameter);

//    buildSrcCache.useCache("rebuild buildSrc", new BuildSrcUpdateFactory(buildSrcCache, gradleLauncher, new BuildSrcBuildListenerFactory()));
//    URLClassLoader classLoader = new URLClassLoader(defaultClassPath.getAsURLs().toArray(new URL[]{}), this.getClass().getClassLoader());
//    try {
//        Enumeration<URL> res = classLoader.getResources("play.plugins");
////                    while (res.hasMoreElements()) {
////                        System.out.println(res.nextElement());
////                    }
//    } catch (IOException e) {
//        e.printStackTrace();
//    }
//    System.out.println(defaultClassPath);

    @TaskAction
    protected void run() throws InterruptedException, IOException { //TODO: why is convetion to be protected?
        BuildLink buildLink = new BuildLink(){
            @Override
            public Object reload() {
                DefaultClassPath defaultClassPath = build();
                URLClassLoader classLoader = new URLClassLoader(defaultClassPath.getAsURLs().toArray(new URL[]{}), this.getClass().getClassLoader());
                return classLoader;
            }

            @Override
            public Object[] findSource(String className, Integer line) {
                logger.info("findSource: " + className + " line: " + line);
                return null;
            }

            @Override
            public File projectPath() {
                logger.info("projectPath");
                return getProject().getRootDir(); //TODO: is this right?
            }

            @Override
            public void forceReload() {
                logger.info("forceReload");
            }

            @Override
            public Map<String, String> settings() {
                logger.info("settings");
                return new HashMap<String, String>();
            }

            @Override
            public Object runTask(String task) {
                logger.info("runTask " + task);
                return null;
            }
        };
        BuildDocHandlerFactory buildDocHandlerFactory = new BuildDocHandlerFactory();
        BuildDocHandler buildDocHandler = buildDocHandlerFactory.fromJar(new JarFile("/Users/freekh/.ivy2/cache/com.typesafe.play/play-docs_2.10/jars/play-docs_2.10-2.3.2.jar"), "play/docs/content");

        play.core.server.NettyServer.mainDevHttpMode(buildLink, buildDocHandler, 9000);

        while (true) {
            Thread.sleep(100);
        }

    }
}
