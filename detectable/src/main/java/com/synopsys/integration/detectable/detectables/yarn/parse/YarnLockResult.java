package com.synopsys.integration.detectable.detectables.yarn.parse;

import java.io.File;

public class YarnLockResult {
    private final File file;
    private final YarnLock yarnLock;

    public YarnLockResult(final File file, final YarnLock yarnLock) {
        this.file = file;
        this.yarnLock = yarnLock;
    }

    public File getFile() {
        return file;
    }

    public YarnLock getYarnLock() {
        return yarnLock;
    }
}
