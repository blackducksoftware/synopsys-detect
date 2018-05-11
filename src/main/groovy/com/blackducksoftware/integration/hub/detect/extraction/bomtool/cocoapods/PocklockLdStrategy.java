package com.blackducksoftware.integration.hub.detect.extraction.bomtool.cocoapods;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class PocklockLdStrategy extends Strategy<PodlockContext, PodlockExtractor> {
    public static final String PODFILE_LOCK_FILENAME = "Podfile.lock";

    public PocklockLdStrategy() {
        super("Podlock", BomToolType.COCOAPODS, PodlockContext.class, PodlockExtractor.class);

        needsFile(PODFILE_LOCK_FILENAME, (context, file) -> context.podlock = file);
    }

}
