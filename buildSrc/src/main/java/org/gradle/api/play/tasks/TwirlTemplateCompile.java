package org.gradle.api.play.tasks;


import groovy.lang.Closure;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.FileTreeElement;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.util.PatternFilterable;
import org.gradle.api.tasks.util.PatternSet;
import play.twirl.compiler.TwirlCompiler$;
import scala.io.Codec$;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class TwirlTemplateCompile extends DefaultTask { //TODO: SourceTask did not work - which it should?
    private File sourceDirectory;
    private File generatedDirectory;
    private String formatterType;
    private String additionalImports = "";
    private String codec;
    private boolean inclusiveDots = false;
    private boolean useOldParser = false;

    private File asRelativeTo(File root, File file) {
        String path = file.getAbsolutePath().replace(root.getAbsolutePath(), "./" + root.getName());
        return new File(path);
    }

    @TaskAction
    protected void compile() throws IOException {
        PatternFilterable pattern = new PatternSet()
                .include("**/*.scala.*")
                .exclude("*.java", "*.scala");
        FileTree inputTree = getProject().fileTree(sourceDirectory).matching(pattern);
        for (File file: inputTree.getFiles()) {
            TwirlCompiler$.MODULE$.compile(
                    file.getCanonicalFile(),
                    sourceDirectory.getCanonicalFile(),
                    generatedDirectory,
                    formatterType,
                    additionalImports,
                    Codec$.MODULE$.apply(codec),
                    inclusiveDots,
                    useOldParser
            );
        }
    }

    @Input @Optional
    public boolean isUseOldParser() {
        return useOldParser;
    }

    public void setUseOldParser(boolean useOldParser) {
        this.useOldParser = useOldParser;
    }

    @InputDirectory
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

    @Input @Optional
    public String getAdditionalImports() {
        return additionalImports;
    }

    public void setAdditionalImports(String additionalImports) {
        this.additionalImports = additionalImports;
    }

    @Input
    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    @Input @Optional
    public boolean isInclusiveDots() {
        return inclusiveDots;
    }

    public void setInclusiveDots(boolean inclusiveDots) {
        this.inclusiveDots = inclusiveDots;
    }
}
