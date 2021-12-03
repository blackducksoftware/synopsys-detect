package com.synopsys.integration.detectable.detectables.pear.unit;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.pear.model.PackageDependency;
import com.synopsys.integration.detectable.detectables.pear.parse.PearPackageDependenciesParser;
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
        List<String> validLines = Arrays.asList(
            "=====================",
            "Required? Type           Name                    Versioning                   Group",
            "Yes       Php                                     (version >= 5.4.0)",
            "Yes       Pear Installer                          (version >= 1.9.0)",
            "Yes       Package        pear/Archive_Tar         (recommended version 1.4.3)",
            "No       Package        pear/Structures_Graph    (recommended version 1.1.1)"
        );

        List<PackageDependency> packageDependencies = pearPackageDependenciesParser.parse(validLines);
        Assertions.assertEquals(2, packageDependencies.size());
        Assertions.assertEquals("Archive_Tar", packageDependencies.get(0).getName());
        Assertions.assertTrue(packageDependencies.get(0).isRequired());
        Assertions.assertEquals("Structures_Graph", packageDependencies.get(1).getName());
        Assertions.assertFalse(packageDependencies.get(1).isRequired());
    }

    @Test
    void parseNoStart() throws IntegrationException {
        List<String> noStartLines = Arrays.asList(
            "Required? Type           Name                    Versioning                   Group",
            "Yes       Php                                     (version >= 5.4.0)",
            "Yes       Pear Installer                          (version >= 1.9.0)",
            "Yes       Package        pear/Archive_Tar         (recommended version 1.4.3)",
            "No       Package        pear/Structures_Graph    (recommended version 1.1.1)"
        );

        List<PackageDependency> packageDependencies = pearPackageDependenciesParser.parse(noStartLines);
        Assertions.assertEquals(0, packageDependencies.size());
    }

    @Test
    void parseMissingInfo() {
        List<String> missingInfoLines = Arrays.asList(
            "=====================",
            "Required? Type           Name                    Versioning                   Group",
            "Yes       Php                                     (version >= 5.4.0)",
            "Yes       Pear Installer                          (version >= 1.9.0)",
            "Yes       Package        pear/Archive_Tar         (recommended version 1.4.3)",
            "No       Package"
        );

        Assertions.assertThrows(IntegrationException.class, () -> pearPackageDependenciesParser.parse(missingInfoLines));
    }
}