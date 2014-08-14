package org.gradle.api.play.tasks;


import org.gradle.api.tasks.*;
import play.twirl.compiler.TwirlCompiler$;
import scala.io.Codec;
import scala.io.Codec$;

import java.io.File;
import java.util.List;

public class TwirlTemplateCompile extends SourceTask {
    private List<File> inputFiles;
    private File sourceDirectory;
    private File generatedDirectory;
    private String formatterType;
    private String additionalImports;
    private Codec codec;
    private boolean inclusiveDots;
    private boolean useOldParser;

    @TaskAction
    protected void compile() {
        System.out.println("JIPPI:");
        for (File file: inputFiles) {
            TwirlCompiler$.MODULE$.compile(
                    file,
                    sourceDirectory,
                    generatedDirectory,
                    formatterType,
                    additionalImports,
                    codec,
                    inclusiveDots,
                    useOldParser
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


    @Input
    public boolean isUseOldParser() {
        return useOldParser;
    }

    public void setUseOldParser(boolean useOldParser) {
        this.useOldParser = useOldParser;
    }

    @Input
    public File getSourceDirectory() {
        return sourceDirectory;
    }

    public void setSourceDirectory(File sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    @OutputDirectory
    public File getGeneratedDirectory() {
        return generatedDirectory;
    }

    public void setGeneratedDirectory(File generatedDirectory) {
        this.generatedDirectory = generatedDirectory;
    }

    @Input
    public String getFormatterType() {
        return formatterType;
    }

    public void setFormatterType(String formatterType) {
        this.formatterType = formatterType;
    }

    @Input
    public String getAdditionalImports() {
        return additionalImports;
    }

    public void setAdditionalImports(String additionalImports) {
        this.additionalImports = additionalImports;
    }

    @Input
    public String getCodec() {
        return codec.encoder().toString();
    }

    public void setCodec(String codec) {
        this.codec = Codec$.MODULE$.apply(codec);
    }

    @Input
    public boolean isInclusiveDots() {
        return inclusiveDots;
    }

    public void setInclusiveDots(boolean inclusiveDots) {
        this.inclusiveDots = inclusiveDots;
    }
}
