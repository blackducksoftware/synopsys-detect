package com.synopsys.integration.detectable.detectable.factory;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectable;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectableOptions;

public class DetectableFactory {
    private final UtilityFactory utilityFactory;
    private final ExtractorFactory extractorFactory;

    public DetectableFactory(final UtilityFactory utilityFactory, final ExtractorFactory extractorFactory) {
        this.utilityFactory = utilityFactory;
        this.extractorFactory = extractorFactory;
    }

    public BitbakeDetectable bitbakeDetectable(DetectableEnvironment environment, BitbakeDetectableOptions options) {
        return new BitbakeDetectable(environment, this.utilityFactory.simpleFileFinder(), options, extractorFactory.bitbakeExtractor(), this.utilityFactory.simpleSystemExecutableFinder());
    }
}
