package com.synopsys.integration.detect.workflow.codelocation;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileNameUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileNameUtils.class);

    public static String relativize(String from, String to) {
        String relative = to;
        try {
            Path toPath = new File(to).toPath();
            Path fromPath = new File(from).toPath();
            Path relativePath = fromPath.relativize(toPath);
            List<String> relativePieces = new ArrayList<>();
            for (int i = 0; i < relativePath.getNameCount(); i++) {
                relativePieces.add(relativePath.getName(i).toFile().getName());
            }
            relative = StringUtils.join(relativePieces, "/");
        } catch (Exception e) {
            logger.info(String.format("Unable to relativize path, full source path will be used: %s", to));
            logger.debug("The reason relativize failed: ", e);
        }

        return relative;
    }

    public static String relativizeParent(String from, String to) {
        String relative = to;
        try {
            Path toPath = new File(to).toPath();
            Path fromPath = new File(from).toPath();
            Path relativePath = fromPath.getParent().relativize(toPath);
            List<String> relativePieces = new ArrayList<>();
            for (int i = 0; i < relativePath.getNameCount(); i++) {
                relativePieces.add(relativePath.getName(i).toFile().getName());
            }
            relative = StringUtils.join(relativePieces, "/");
        } catch (Exception e) {
            logger.info(String.format("Unable to relativize path, full source path will be used: %s", to));
            logger.debug("The reason relativize failed: ", e);
        }

        return relative;
    }

}
