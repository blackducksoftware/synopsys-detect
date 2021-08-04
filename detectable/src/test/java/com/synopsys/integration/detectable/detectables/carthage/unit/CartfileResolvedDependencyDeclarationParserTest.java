package com.synopsys.integration.detectable.detectables.carthage.unit;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.detectables.carthage.CartfileResolvedDependencyDeclarationParser;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class CartfileResolvedDependencyDeclarationParserTest {
    private CartfileResolvedDependencyDeclarationParser parser = new CartfileResolvedDependencyDeclarationParser();

    @Test
    public void testGithubDependencies() {
        List<String> dependencyDeclarations = Arrays.asList(
            "github \"GEOSwift/GEOSwift\" \"8.0.2\"",
            "github \"GEOSwift/geos\" \"6.0.2\"",
            "github \"MobileNativeFoundation/Kronos\" \"4.2.1\""
        );
        DependencyGraph dependencyGraph = parser.parseDependencies(dependencyDeclarations);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.GITHUB, dependencyGraph);
        graphAssert.hasRootSize(3);
        graphAssert.hasRootDependency("GEOSwift/GEOSwift", "8.0.2");
        graphAssert.hasRootDependency("GEOSwift/geos", "6.0.2");
        graphAssert.hasRootDependency("MobileNativeFoundation/Kronos", "4.2.1");
    }

    @Test
    public void testNotParseBinaryDependencies() {
        List<String> dependencyDeclarations = Arrays.asList(
            "binary \"https://assets.braintreegateway.com/mobile/ios/carthage-frameworks/cardinal-mobile/CardinalMobile.json\" \"2.2.5\"",
            "binary \"https://assets.braintreegateway.com/mobile/ios/carthage-frameworks/pp-risk-magnes/PPRiskMagnes.json\" \"5.0.1\"",
            "github \"GEOSwift/GEOSwift\" \"8.0.2\""
        );
        DependencyGraph dependencyGraph = parser.parseDependencies(dependencyDeclarations);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.GITHUB, dependencyGraph);
        graphAssert.hasRootSize(1);
        graphAssert.hasNoDependency("https://assets.braintreegateway.com/mobile/ios/carthage-frameworks/cardinal-mobile/CardinalMobile.json", "2.2.5");
        graphAssert.hasNoDependency("https://assets.braintreegateway.com/mobile/ios/carthage-frameworks/pp-risk-magnes/PPRiskMagnes.json", "5.0.1");
        graphAssert.hasRootDependency("GEOSwift/GEOSwift", "8.0.2");
    }

    @Test
    public void testParseFileWithExtraSpaces() {
        List<String> dependencyDeclarations = Arrays.asList(
            "github  \"adjust/ios_sdk\"    \"v4.26.1\"",
            "github         \"amplitude/Amplitude-iOS\"   \"v8.0.0\"",
            "github  \"braintree/braintree_ios\"             \"5.2.0\""
        );
        DependencyGraph dependencyGraph = parser.parseDependencies(dependencyDeclarations);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.GITHUB, dependencyGraph);
        graphAssert.hasRootSize(3);
        graphAssert.hasRootDependency("adjust/ios_sdk", "v4.26.1");
        graphAssert.hasRootDependency("amplitude/Amplitude-iOS", "v8.0.0");
        graphAssert.hasRootDependency("braintree/braintree_ios", "5.2.0");
    }

}
