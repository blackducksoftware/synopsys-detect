package com.synopsys.integration.detectable.detectables.pear.unit;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.pear.PackageDependency;
import com.synopsys.integration.detectable.detectables.pear.PearPackageDependenciesParser;
import com.synopsys.integration.exception.IntegrationException;

@UnitTest
class PearPackageDependenciesParserTest {
    private static PearPackageDependenciesParser pearPackageDependenciesParser;

    @BeforeEach
    void setUp() {
        pearPackageDependenciesParser = new PearPackageDependenciesParser();
    }

    @Test
    void parseValid() throws IntegrationException {
        final List<String> validLines = Arrays.asList(
            "=====================",
            "Required? Type           Name                    Versioning                   Group",
            "Yes       Php                                     (version >= 5.4.0)",
            "Yes       Pear Installer                          (version >= 1.9.0)",
            "Yes       Package        pear/Archive_Tar         (recommended version 1.4.3)",
            "No       Package        pear/Structures_Graph    (recommended version 1.1.1)"
        );

        final List<PackageDependency> packageDependencies = pearPackageDependenciesParser.parse(validLines);
        Assert.assertEquals(2, packageDependencies.size());
        Assert.assertEquals("Archive_Tar", packageDependencies.get(0).getName());
        Assert.assertTrue(packageDependencies.get(0).isRequired());
        Assert.assertEquals("Structures_Graph", packageDependencies.get(1).getName());
        Assert.assertFalse(packageDependencies.get(1).isRequired());
    }

    @Test
    void parseNoStart() throws IntegrationException {
        final List<String> noStartLines = Arrays.asList(
            "Required? Type           Name                    Versioning                   Group",
            "Yes       Php                                     (version >= 5.4.0)",
            "Yes       Pear Installer                          (version >= 1.9.0)",
            "Yes       Package        pear/Archive_Tar         (recommended version 1.4.3)",
            "No       Package        pear/Structures_Graph    (recommended version 1.1.1)"
        );

        final List<PackageDependency> packageDependencies = pearPackageDependenciesParser.parse(noStartLines);
        Assert.assertEquals(0, packageDependencies.size());
    }

    @Test
    void parseMissingInfo() {
        final List<String> missingInfoLines = Arrays.asList(
            "=====================",
            "Required? Type           Name                    Versioning                   Group",
            "Yes       Php                                     (version >= 5.4.0)",
            "Yes       Pear Installer                          (version >= 1.9.0)",
            "Yes       Package        pear/Archive_Tar         (recommended version 1.4.3)",
            "No       Package"
        );

        try {
            pearPackageDependenciesParser.parse(missingInfoLines);
            Assert.fail("Should have thrown an exception");
        } catch (final IntegrationException ignore) {

        }
    }
}