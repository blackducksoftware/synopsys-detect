package com.blackducksoftware.integration.hub.detect.workflow;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DetectFileUtils {
    private final static Logger logger = LoggerFactory.getLogger(DetectFileUtils.class);

    public static File writeToFile(final File file, final String contents) throws IOException {
        return writeToFile(file, contents, true);
    }

    private static File writeToFile(final File file, final String contents, final boolean overwrite) throws IOException {
        if (file == null) {
            return null;
        }
        if (overwrite && file.exists()) {
            file.delete();
        }
        if (file.exists()) {
            logger.info(String.format("%s exists and not being overwritten", file.getAbsolutePath()));
        } else {
            FileUtils.write(file, contents, StandardCharsets.UTF_8);
        }
        return file;
    }
}
