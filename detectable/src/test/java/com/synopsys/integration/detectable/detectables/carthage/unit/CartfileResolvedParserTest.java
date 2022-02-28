package com.synopsys.integration.detectable.detectables.carthage.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.carthage.model.CarthageDeclaration;
import com.synopsys.integration.detectable.detectables.carthage.parse.CartfileResolvedParser;

public class CartfileResolvedParserTest {
    private final CartfileResolvedParser parser = new CartfileResolvedParser();

    @Test
    public void testGithubDependencies() {
        List<String> dependencyDeclarations = Arrays.asList(
            "github \"GEOSwift/GEOSwift\" \"8.0.2\"",
            "github \"GEOSwift/geos\" \"6.0.2\"",
            "github \"MobileNativeFoundation/Kronos\" \"4.2.1\""
        );
        List<CarthageDeclaration> carthageDeclarations = parser.parseDependencies(dependencyDeclarations);

        assertTrue(containsDependency(carthageDeclarations, "github", "GEOSwift/GEOSwift", "8.0.2"));
        assertTrue(containsDependency(carthageDeclarations, "github", "GEOSwift/geos", "6.0.2"));
        assertTrue(containsDependency(carthageDeclarations, "github", "MobileNativeFoundation/Kronos", "4.2.1"));
        assertEquals(3, carthageDeclarations.size());
    }

    @Test
    public void testNotParseBinaryDependencies() {
        List<String> dependencyDeclarations = Arrays.asList(
            "binary \"https://assets.braintreegateway.com/mobile/ios/carthage-frameworks/cardinal-mobile/CardinalMobile.json\" \"2.2.5\"",
            "binary \"https://assets.braintreegateway.com/mobile/ios/carthage-frameworks/pp-risk-magnes/PPRiskMagnes.json\" \"5.0.1\"",
            "github \"GEOSwift/GEOSwift\" \"8.0.2\""
        );
        List<CarthageDeclaration> carthageDeclarations = parser.parseDependencies(dependencyDeclarations);

        assertTrue(containsDependency(
            carthageDeclarations,
            "binary",
            "https://assets.braintreegateway.com/mobile/ios/carthage-frameworks/cardinal-mobile/CardinalMobile.json",
            "2.2.5"
        ));
        assertTrue(containsDependency(
            carthageDeclarations,
            "binary",
            "https://assets.braintreegateway.com/mobile/ios/carthage-frameworks/pp-risk-magnes/PPRiskMagnes.json",
            "5.0.1"
        ));
        assertTrue(containsDependency(carthageDeclarations, "github", "GEOSwift/GEOSwift", "8.0.2"));
        assertEquals(3, carthageDeclarations.size());
    }

    @Test
    public void testParseFileWithExtraSpaces() {
        List<String> dependencyDeclarations = Arrays.asList(
            "github  \"adjust/ios_sdk\"    \"v4.26.1\"",
            "github         \"amplitude/Amplitude-iOS\"   \"v8.0.0\"",
            "github  \"braintree/braintree_ios\"             \"5.2.0\""
        );
        List<CarthageDeclaration> carthageDeclarations = parser.parseDependencies(dependencyDeclarations);

        assertTrue(containsDependency(carthageDeclarations, "github", "adjust/ios_sdk", "v4.26.1"));
        assertTrue(containsDependency(carthageDeclarations, "github", "amplitude/Amplitude-iOS", "v8.0.0"));
        assertTrue(containsDependency(carthageDeclarations, "github", "braintree/braintree_ios", "5.2.0"));
        assertEquals(3, carthageDeclarations.size());
    }

    private boolean containsDependency(List<CarthageDeclaration> carthageDeclarations, String origin, String name, String version) {
        return carthageDeclarations.stream()
            .anyMatch(declaration ->
                origin.equals(declaration.getOrigin())
                    && name.equals(declaration.getName())
                    && version.equals(declaration.getVersion())
            );
    }
}
