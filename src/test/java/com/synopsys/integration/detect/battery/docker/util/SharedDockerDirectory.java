package com.synopsys.integration.detect.battery.docker.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SharedDockerDirectory {
    private static File dockerRoot = null;
    private static File sharedTools = null;

    public static File getRoot() throws IOException {
        if (dockerRoot == null) {
            dockerRoot = Files.createTempDirectory("docker").toFile();
        }
        return dockerRoot;
    }

    public static File getSharedTools() throws IOException {
        if (sharedTools == null) {
            sharedTools = new File(getRoot(), "tools");
        }
        return sharedTools;
    }
}
