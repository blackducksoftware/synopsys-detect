package com.synopsys.integration.detectable.detectables.swift.unit;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.swift.SwiftPackageTransformer;
import com.synopsys.integration.detectable.detectables.swift.model.SwiftPackage;
import com.synopsys.integration.detectable.util.graph.GraphAssert;

class SwiftPackageTransformerTest {
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
    private final SwiftPackageTransformer swiftPackageTransformer = new SwiftPackageTransformer(externalIdFactory);

    @Test
    void transform() {
        final SwiftPackage swiftPackage = createSwiftPackage();
        final CodeLocation codeLocation = swiftPackageTransformer.transform(swiftPackage);

        final GraphAssert graphAssert = new GraphAssert(SwiftPackageTransformer.SWIFT_FORGE, codeLocation.getDependencyGraph());
        graphAssert.hasDependency(externalIdFactory.createNameVersionExternalId(SwiftPackageTransformer.SWIFT_FORGE, "FisherYates", "2.0.5"));
        graphAssert.hasDependency(externalIdFactory.createNameVersionExternalId(SwiftPackageTransformer.SWIFT_FORGE, "PlayingCard", "3.0.5"));
        graphAssert.hasNoDependency(externalIdFactory.createNameVersionExternalId(SwiftPackageTransformer.SWIFT_FORGE, "DeckOfPlayingCards", "unspecified"));
    }

    private SwiftPackage createSwiftPackage() {
        final SwiftPackage rootPackage = new SwiftPackage();
        rootPackage.setName("DeckOfPlayingCards");
        rootPackage.setVersion("unspecified");

        final SwiftPackage fisherYates = new SwiftPackage();
        fisherYates.setName("FisherYates");
        fisherYates.setVersion("2.0.5");

        final SwiftPackage playingCard = new SwiftPackage();
        playingCard.setName("PlayingCard");
        playingCard.setVersion("3.0.5");

        final List<SwiftPackage> dependencies = new ArrayList<>();
        dependencies.add(fisherYates);
        dependencies.add(playingCard);
        rootPackage.setDependencies(dependencies);

        return rootPackage;
    }

}