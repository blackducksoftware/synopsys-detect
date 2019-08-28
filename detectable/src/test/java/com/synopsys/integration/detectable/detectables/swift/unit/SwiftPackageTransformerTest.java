package com.synopsys.integration.detectable.detectables.swift.unit;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.externalid.ExternalId;
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

        final ExternalId fisherYates = externalIdFactory.createNameVersionExternalId(SwiftPackageTransformer.SWIFT_FORGE, "FisherYates", "2.0.5");
        final ExternalId playingCard = externalIdFactory.createNameVersionExternalId(SwiftPackageTransformer.SWIFT_FORGE, "PlayingCard", "3.0.5");
        final ExternalId playingCard2 = externalIdFactory.createModuleNamesExternalId(SwiftPackageTransformer.SWIFT_FORGE, "PlayingCard2");
        final ExternalId deckOfPlayingCards = externalIdFactory.createNameVersionExternalId(SwiftPackageTransformer.SWIFT_FORGE, "DeckOfPlayingCards", "unspecified");
        graphAssert.hasRootDependency(fisherYates);
        graphAssert.hasRootDependency(playingCard);
        graphAssert.hasParentChildRelationship(playingCard, playingCard2);
        graphAssert.hasNoDependency(deckOfPlayingCards);
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

        final SwiftPackage playingCard2 = new SwiftPackage();
        playingCard2.setName("PlayingCard2");
        playingCard2.setVersion("unspecified");
        playingCard.getDependencies().add(playingCard2);

        final List<SwiftPackage> dependencies = new ArrayList<>();
        dependencies.add(fisherYates);
        dependencies.add(playingCard);
        rootPackage.setDependencies(dependencies);

        return rootPackage;
    }

}