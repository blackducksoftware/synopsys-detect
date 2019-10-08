package com.synopsys.integration.detect.battery;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.detect.configuration.DetectProperty;

public class ResourceTypingExecutable extends TypingExecutable {
    private final List<String> toType;

    protected ResourceTypingExecutable(final DetectProperty detectProperty, final List<String> toType) {
        super(detectProperty);
        this.toType = toType;
    }

    @Override
    public List<String> getFilePaths(final File mockDirectory, final AtomicInteger commandCount) throws IOException {
        final List<String> filePaths = new ArrayList<>();
        for (final String resource : toType) {
            final InputStream commandText = BatteryFiles.asInputStream(resource);
            Assertions.assertNotNull(commandText, "Unable to find resource: " + resource);
            final File commandTextFile = new File(mockDirectory, "cmd-" + commandCount.getAndIncrement() + ".txt");
            Files.copy(commandText, commandTextFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            filePaths.add(commandTextFile.getCanonicalPath());
        }
        return filePaths;
    }
}
