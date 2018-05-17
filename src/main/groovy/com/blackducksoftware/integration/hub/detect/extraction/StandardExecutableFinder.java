package com.blackducksoftware.integration.hub.detect.extraction;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;

@Component
public class StandardExecutableFinder {
    public enum StandardExecutableType {
        CONDA,
        CPAN,
        CPANM,
        DOCKER,
        BASH,
        GO,
        REBAR3,
        PEAR
    }

    @Autowired
    public DetectFileManager detectFileManager;

    @Autowired
    public ExecutableManager executableManager;

    @Autowired
    protected DetectConfiguration detectConfiguration;

    private final Map<StandardExecutableType, File> alreadyFound = new HashMap<>();

    public File getExecutable(final StandardExecutableType executableType) {
        if (alreadyFound.containsKey(executableType)) {
            return alreadyFound.get(executableType);
        }
        final StandardExecutableInfo info = createInfo(executableType);
        if (info == null) {
            throw new RuntimeException("Unknown executable type: " + executableType.toString());
        }

        final String exe = executableManager.getExecutablePathOrOverride(info.detectExecutableType, true, detectConfiguration.getSourceDirectory(), info.override);
        File exeFile = null;
        if (exe != null) {
            exeFile = new File(exe);
        }
        alreadyFound.put(executableType, exeFile);
        return exeFile;
    }

    public StandardExecutableInfo createInfo(final StandardExecutableType type) {
        switch (type) {
        case CONDA:
            return new StandardExecutableInfo(ExecutableType.CONDA, detectConfiguration.getCondaPath());
        case CPAN:
            return new StandardExecutableInfo(ExecutableType.CPAN, detectConfiguration.getCpanPath());
        case CPANM:
            return new StandardExecutableInfo(ExecutableType.CPANM, detectConfiguration.getCpanmPath());
        case DOCKER:
            return new StandardExecutableInfo(ExecutableType.DOCKER, detectConfiguration.getDockerPath());
        case BASH:
            return new StandardExecutableInfo(ExecutableType.BASH, detectConfiguration.getBashPath());
        case GO:
            return new StandardExecutableInfo(ExecutableType.GO, null);
        case REBAR3:
            return new StandardExecutableInfo(ExecutableType.REBAR3, detectConfiguration.getHexRebar3Path());
        case PEAR:
            return new StandardExecutableInfo(ExecutableType.PEAR, detectConfiguration.getPearPath());

        }
        return null;
    }

    private class StandardExecutableInfo {
        public ExecutableType detectExecutableType;
        public String override;

        public StandardExecutableInfo(final ExecutableType detectExecutableType, final String override) {
            this.detectExecutableType = detectExecutableType;
            this.override = override;
        }
    }
}
