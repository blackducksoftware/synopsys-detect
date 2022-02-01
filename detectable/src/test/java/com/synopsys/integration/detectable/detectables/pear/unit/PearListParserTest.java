package com.synopsys.integration.detectable.detectables.pear.unit;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.pear.parse.PearListParser;
import com.synopsys.integration.exception.IntegrationException;

@UnitTest
class PearListParserTest {
    private static PearListParser pearListParser;

    @BeforeEach
    void setUp() {
        pearListParser = new PearListParser();
    }

    @Test
    void parse() throws IntegrationException {
        List<String> validListLines = Arrays.asList(
            "Installed packages, channel pear.php.net:",
            "=========================================",
            "Package              Version State",
            "Archive_Tar          1.4.3   stable",
            "Console_Getopt       1.4.1   stable"
        );

        Map<String, String> dependenciesMap = pearListParser.parse(validListLines);
        Assertions.assertEquals(2, dependenciesMap.size());
        Assertions.assertEquals("1.4.3", dependenciesMap.get("Archive_Tar"));
        Assertions.assertEquals("1.4.1", dependenciesMap.get("Console_Getopt"));
    }

    @Test
    void parseNoStart() throws IntegrationException {
        List<String> notStartLines = Arrays.asList(
            "Package              Version State",
            "Archive_Tar          1.4.3   stable",
            "Console_Getopt       1.4.1   stable"
        );

        Map<String, String> dependenciesMap = pearListParser.parse(notStartLines);
        Assertions.assertEquals(0, dependenciesMap.size());
    }

    @Test
    void parseMissingInfo() {
        List<String> missingInfoLines = Arrays.asList(
            "Installed packages, channel pear.php.net:",
            "=========================================",
            "Package              Version State",
            "Archive_Tar          1.4.3   stable",
            "Console_Getopt       "
        );

        assertThrows(IntegrationException.class, () -> pearListParser.parse(missingInfoLines));
    }
}