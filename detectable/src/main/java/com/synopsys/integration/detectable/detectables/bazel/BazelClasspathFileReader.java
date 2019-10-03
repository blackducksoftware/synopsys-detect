package com.synopsys.integration.detectable.detectables.bazel;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

public class BazelClasspathFileReader {

    public String readFileFromClasspathToString(final String filePath) throws IOException {
        final String fileContent;
        try (final InputStream in = this.getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (in == null) {
                throw new IOException("Unable to read file from classpath");
            }
            fileContent = IOUtils.toString(in, StandardCharsets.UTF_8);
        }
        return fileContent;
    }
}
