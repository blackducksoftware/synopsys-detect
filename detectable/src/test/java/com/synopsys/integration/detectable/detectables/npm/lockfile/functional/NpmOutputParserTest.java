package com.synopsys.integration.detectable.detectables.npm.lockfile.functional;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.npm.cli.parse.NpmCliParser;
import com.synopsys.integration.detectable.detectables.npm.cli.parse.NpmDependencyTypeFilter;
import com.synopsys.integration.detectable.detectables.npm.lockfile.result.NpmPackagerResult;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class NpmOutputParserTest {
    @Test
    public void npmCliDependencyFinder() {
        NpmCliParser parser = new NpmCliParser(new ExternalIdFactory(), EnumListFilter.excludeNone());
        String testIn = String.join(System.lineSeparator(), Arrays.asList(
            "{",
            "   \"name\": \"node-js\",",
            "   \"version\": \"0.2.0\",",
            "   \"dependencies\": {",
            "       \"upper-case\": {",
            "       \"version\": \"1.1.3\",",
            "       \"from\": \"upper-case@latest\",",
            "       \"resolved\": \"https://registry.npmjs.org/upper-case/-/upper-case-1.1.3.tgz\"",
            "       },",
            "       \"xml2js\": {",
            "           \"version\": \"0.4.17\",",
            "           \"from\": \"xml2js@latest\",",
            "           \"resolved\": \"https://registry.npmjs.org/xml2js/-/xml2js-0.4.17.tgz\",",
            "            \"dependencies\": {",
            "               \"sax\": {",
            "                   \"version\": \"1.2.2\",",
            "                   \"from\": \"sax@>=0.6.0\",",
            "                   \"resolved\": \"https://registry.npmjs.org/sax/-/sax-1.2.2.tgz\"",
            "               },",
            "               \"xmlbuilder\": {",
            "                   \"version\": \"4.2.1\",",
            "                   \"from\": \"xmlbuilder@>=4.1.0 <5.0.0\",",
            "                   \"resolved\": \"https://registry.npmjs.org/xmlbuilder/-/xmlbuilder-4.2.1.tgz\",",
            "                   \"dependencies\": {",
            "                       \"lodash\": {",
            "                           \"version\": \"4.17.4\",",
            "                           \"from\": \"lodash@>=4.0.0 <5.0.0\",",
            "                           \"resolved\": \"https://registry.npmjs.org/lodash/-/lodash-4.17.4.tgz\"",
            "                       }",
            "                   }",
            "               }",
            "           }",
            "       }",
            "   }",
            "}"
        ));
        NpmDependencyTypeFilter npmDependencyTypeFilter = new NpmDependencyTypeFilter(Collections.emptySet(), Collections.emptySet(), true, true);
        PackageJson packageJson = new PackageJson();
        NpmPackagerResult result = parser.convertNpmJsonFileToCodeLocation(testIn, packageJson);

        Assertions.assertEquals("node-js", result.getProjectName());
        Assertions.assertEquals("0.2.0", result.getProjectVersion());

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.NPMJS, result.getCodeLocation().getDependencyGraph());

        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("xml2js", "0.4.17");
        graphAssert.hasRootDependency("upper-case", "1.1.3");
        graphAssert.hasParentChildRelationship("xml2js", "0.4.17", "xmlbuilder", "4.2.1");
        graphAssert.hasParentChildRelationship("xml2js", "0.4.17", "sax", "1.2.2");
        graphAssert.hasParentChildRelationship("xmlbuilder", "4.2.1", "lodash", "4.17.4");
    }
}
