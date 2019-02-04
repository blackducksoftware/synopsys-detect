package com.synopsys.integration.detectable.detectable.executable.impl;

import java.io.File;

import com.synopsys.integration.detectable.detectable.executable.ExecutableType;
import com.synopsys.integration.detectable.detectable.executable.LocalExecutableFinder;
import com.synopsys.integration.detectable.detectable.executable.LocalOrSytemExecutableFinder;
import com.synopsys.integration.detectable.detectable.executable.SystemExecutableFinder;

public class SimpleLocalOrSystemExecutableFinder implements LocalOrSytemExecutableFinder {

    private final LocalExecutableFinder localExecutableFinder;
    private final SystemExecutableFinder systemExecutableFinder;

    public SimpleLocalOrSystemExecutableFinder(LocalExecutableFinder localExecutableFinder, SystemExecutableFinder systemExecutableFinder) {
        this.localExecutableFinder = localExecutableFinder;
        this.systemExecutableFinder = systemExecutableFinder;
    }

    @Override
    public File findExecutable(final String executable, final File localLocation) {
        File local = localExecutableFinder.findExecutable(executable, localLocation);
        if (local != null){
            return local;
        }
        return systemExecutableFinder.findExecutable(executable);
    }
}
