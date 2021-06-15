package com.synopsys.integration.detect.battery.util;

import java.io.File;

public class TestPaths {
    public static File build() {
        return new File(projectRootDirectory(), "build");
    }

    public static File libs() {
        return new File(build(), "libs");
    }

    public static File projectRootDirectory() {
        return new File(".");
    }
}
