package com.blackducksoftware.integration.hub.detect.extraction.bomtool.conda;

import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class CondaCliLdStrategy extends Strategy<CondaCliContext, CondaCliExtractor> {
    public static final String ENVIRONEMNT_YML = "environment.yml";

    public CondaCliLdStrategy() {
        super("Conda Cli", BomToolType.COCOAPODS, CondaCliContext.class, CondaCliExtractor.class);

        needsFile(ENVIRONEMNT_YML);

        demandsStandardExecutable(StandardExecutableType.CONDA, (context, file) -> context.condaExe = file);
    }

}
