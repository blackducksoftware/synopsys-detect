package com.blackducksoftware.integration.hub.detect.extraction.bomtool.rubygems;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.extraction.model.ExtractionContext;

public class GemlockContext {
    private final static String GEMFILE_KEY = "gemfile";
    ExtractionContext context;


    public GemlockContext(final ExtractionContext context) {
        this.context = context;
    }

    public File getGemlock() {
        return context.getFileKey(GEMFILE_KEY);
    }

    public void setGemlock(final File gemlock) {
        context.addFileKey(GEMFILE_KEY, gemlock);
    }
}
