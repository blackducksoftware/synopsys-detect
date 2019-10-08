package com.synopsys.integration.detect.battery;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;

import com.synopsys.integration.detect.configuration.DetectProperty;

public class StringTypingExecutable extends TypingExecutable {
    private final List<String> toType;

    protected StringTypingExecutable(final DetectProperty detectProperty, final List<String> toType) {
        super(detectProperty);
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
