package com.synopsys.integration.detectable.detectables.carthage.parse;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detectable.detectables.carthage.model.CarthageDeclaration;

public class CartfileResolvedParser {
    public List<CarthageDeclaration> parseDependencies(List<String> dependencyDeclarations) {
        // As of Carthage 0.38.0 the dependencies in Cartfile.resolved are produced as a flat list
        // Each line in a Cartfile.resolved file is a dependency declaration: <origin> <name/resource> <version>
        // eg. github "realm/realm-cocoa" "v10.7.2"
        List<CarthageDeclaration> carthageDeclarations = new LinkedList<>();
        for (String dependencyDeclaration : dependencyDeclarations) {
            String[] dependencyDeclarationPieces = dependencyDeclaration.split("\\s+");
            String origin = dependencyDeclarationPieces[0].trim();
            String name = StringUtils.strip(dependencyDeclarationPieces[1], "\"").trim();
            String version = StringUtils.strip(dependencyDeclarationPieces[2], "\"").trim();
            CarthageDeclaration carthageDeclaration = new CarthageDeclaration(origin, name, version);
            carthageDeclarations.add(carthageDeclaration);
        }
        return carthageDeclarations;
    }
}
