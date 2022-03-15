package com.synopsys.integration.detectable.detectable;

import java.io.File;
import java.util.function.Consumer;

public class SearchPattern {
    private final File searchDirectory;
    private final String filePattern;
    private final Consumer<File> fileConsumer;

    public SearchPattern(File searchDirectory, String filePattern, Consumer<File> fileConsumer) {
        this.searchDirectory = searchDirectory;
        this.filePattern = filePattern;
        this.fileConsumer = fileConsumer;
    }

    public File getSearchDirectory() {
        return searchDirectory;
    }

    public String getFilePattern() {
        return filePattern;
    }

    public Consumer<File> getFileConsumer() {
        return fileConsumer;
    }
}
