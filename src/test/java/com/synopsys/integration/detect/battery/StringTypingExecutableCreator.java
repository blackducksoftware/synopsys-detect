package com.synopsys.integration.detect.battery;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;

public class StringTypingExecutableCreator extends TypingExecutableCreator {
    private final List<String> toType;

    protected StringTypingExecutableCreator(final List<String> toType) {
        this.toType = toType;
    }

    @Override
    public List<String> getFilePaths(final File mockDirectory, final AtomicInteger commandCount) throws IOException {
        final List<String> filePaths = new ArrayList<>();
        for (final String text : toType) {
            final File commandTextFile = new File(mockDirectory, "cmd-" + commandCount.getAndIncrement() + ".txt");
            FileUtils.writeStringToFile(commandTextFile, text, Charset.defaultCharset());
            filePaths.add(commandTextFile.getCanonicalPath());
        }
        return filePaths;
    }
}
