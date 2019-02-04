package com.synopsys.integration.detectable.detectable.factory;

import com.synopsys.integration.detectable.detectable.executable.SystemExecutableFinder;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleExecutableFinder;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleSystemExecutableFinder;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.file.impl.SimpleFileFinder;

public class UtilityFactory {
    public FileFinder simpleFileFinder(){
        return new SimpleFileFinder();
    }

    public SimpleExecutableFinder simpleExecutableFinder(){
        return SimpleExecutableFinder.forCurrentOperatingSystem(simpleFileFinder());
    }

    public SystemExecutableFinder simpleSystemExecutableFinder() {
        return new SimpleSystemExecutableFinder(simpleExecutableFinder());
    }
}
