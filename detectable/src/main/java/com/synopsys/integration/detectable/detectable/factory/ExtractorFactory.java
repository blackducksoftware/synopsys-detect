package com.synopsys.integration.detectable.detectable.factory;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleExecutableRunner;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeExtractor;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeArchitectureParser;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeGraphTransformer;
import com.synopsys.integration.detectable.detectables.bitbake.parse.GraphParserTransformer;

public class ExtractorFactory {
    private final UtilityFactory utilityFactory;

    public ExtractorFactory(UtilityFactory utilityFactory) {
        this.utilityFactory = utilityFactory;
    }

    public BitbakeExtractor bitbakeExtractor() {
        GraphParserTransformer graphParserTransformer = new GraphParserTransformer();
        BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new ExternalIdFactory());
        BitbakeArchitectureParser bitbakeArchitectureParser = new BitbakeArchitectureParser();
        BitbakeExtractor bitbakeExtractor = new BitbakeExtractor(new SimpleExecutableRunner(), this.utilityFactory.simpleFileFinder(), graphParserTransformer, bitbakeGraphTransformer, bitbakeArchitectureParser);
        return bitbakeExtractor;
    }
}
