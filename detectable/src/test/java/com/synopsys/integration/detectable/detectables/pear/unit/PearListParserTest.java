package com.synopsys.integration.detectable.detectables.pear.unit;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
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
        final List<String> validListLines = Arrays.asList(
            "Installed packages, channel pear.php.net:",
            "=========================================",
            "Package              Version State",
            "Archive_Tar          1.4.3   stable",
            "Console_Getopt       1.4.1   stable"
        );

        final Map<String, String> dependenciesMap = pearListParser.parse(validListLines);
        Assert.assertEquals(2, dependenciesMap.size());
        Assert.assertEquals("1.4.3", dependenciesMap.get("Archive_Tar"));
        Assert.assertEquals("1.4.1", dependenciesMap.get("Console_Getopt"));
    }

    @Test
    void parseNoStart() throws IntegrationException {
        final List<String> notStartLines = Arrays.asList(
            "Package              Version State",
            "Archive_Tar          1.4.3   stable",
            "Console_Getopt       1.4.1   stable"
        );

        final Map<String, String> dependenciesMap = pearListParser.parse(notStartLines);
        Assert.assertEquals(0, dependenciesMap.size());
    }

    @Test
    void parseMissingInfo() {
        final List<String> missingInfoLines = Arrays.asList(
            "Installed packages, channel pear.php.net:",
            "=========================================",
            "Package              Version State",
            "Archive_Tar          1.4.3   stable",
            "Console_Getopt       "
        );

        try {
            pearListParser.parse(missingInfoLines);
            Assert.fail("Should have thrown an exception");
        } catch (final IntegrationException ignore) {

        }
    }
}