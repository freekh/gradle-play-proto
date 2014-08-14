package org.gradle.api.play.tasks;

import org.gradle.api.tasks.*;
import play.router.RoutesCompiler$;
import scala.collection.Seq;
import scala.collection.mutable.ListBuffer;
import scala.io.Codec;

import java.io.File;
import java.util.List;

public class RoutesCompile extends SourceTask {
    private List<File> inputFiles;
    private File generatedDirectory;
    private List<String> additionalImports;
    private Boolean generateReverseRoute= true;
    private boolean generateRefReverseRouter = true;
    private boolean namespaceReverseRouter = false;

    @TaskAction
    protected void compile() {
        //conversion to Scala Seq:
        ListBuffer<String> buffer = new ListBuffer<>();
        if (additionalImports != null) {
            for (String additionalImport : additionalImports) {
                buffer.$plus$eq(additionalImport);
            }
        }

        for (File file: inputFiles) {
            RoutesCompiler$.MODULE$.compile(
                    file,
                    generatedDirectory,
                    buffer.toSeq(),
                    generateReverseRoute,
                    generateRefReverseRouter,
                    namespaceReverseRouter
            );
        }
    }


    @InputFiles
    public List<File> getInputFiles() {
        return inputFiles;
    }

    public void setInputFiles(List<File> inputFiles) {
        this.inputFiles = inputFiles;
    }

    @OutputDirectory
    public File getGeneratedDirectory() {
        return generatedDirectory;
    }

    public void setGeneratedDirectory(File generatedDirectory) {
        this.generatedDirectory = generatedDirectory;
    }

    @Input
    public List<String> getAdditionalImports() {
        return additionalImports;
    }

    public void setAdditionalImports(List<String> additionalImports) {
        this.additionalImports = additionalImports;
    }

    @Input @Optional
    public Boolean getGenerateReverseRoute() {
        return generateReverseRoute;
    }

    public void setGenerateReverseRoute(Boolean generateReverseRoute) {
        this.generateReverseRoute = generateReverseRoute;
    }

    @Input @Optional
    public boolean isGenerateRefReverseRouter() {
        return generateRefReverseRouter;
    }

    public void setGenerateRefReverseRouter(boolean generateRefReverseRouter) {
        this.generateRefReverseRouter = generateRefReverseRouter;
    }

    @Input @Optional
    public boolean isNamespaceReverseRouter() {
        return namespaceReverseRouter;
    }

    public void setNamespaceReverseRouter(boolean namespaceReverseRouter) {
        this.namespaceReverseRouter = namespaceReverseRouter;
    }

}