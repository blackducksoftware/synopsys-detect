package com.blackducksoftware.integration.hub.detect.extraction.bomtool.conda;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class CondaCliLdStrategy extends Strategy<CondaCliContext, CondaCliExtractor> {
    public static final String ENVIRONEMNT_YML = "environment.yml";

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public StandardExecutableFinder standardExecutableFinder;

    @Autowired
    public DetectConfiguration detectConfiguration;

    public CondaCliLdStrategy() {
        super("Conda Cli", BomToolType.COCOAPODS, CondaCliContext.class, CondaCliExtractor.class);

        needsFile(ENVIRONEMNT_YML);

        demandsStandardExecutable(StandardExecutableType.CONDA, (context, file) -> context.condaExe = file);
    }

}
