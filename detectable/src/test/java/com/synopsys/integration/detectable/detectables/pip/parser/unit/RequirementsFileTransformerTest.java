package com.synopsys.integration.detectable.detectables.pip.parser.unit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.pip.parser.RequirementsFileTransformer;

import net.bytebuddy.build.ToStringPlugin;

public class RequirementsFileTransformerTest {
    private static final String EXPECTED_DEPENDENCY_NAME = "requests";
    private static final String EXPECTED_DEPENDENCY_VERSION = "12.3.3";

    private static RequirementsFileTransformer requirementsFileTransformer;

    @BeforeAll
    static void setUp() {
        requirementsFileTransformer = new RequirementsFileTransformer();
    }


    @Test
    void testExtractTokensForValidLineWithSpaces() {
        String validDependencyLine = "requests == 12.3.3";
        List<String> expectedTokensBeforeOperator = Collections.singletonList(EXPECTED_DEPENDENCY_NAME);
        List<String> expectedTokensAfterOperator = Collections.singletonList(EXPECTED_DEPENDENCY_VERSION);
        List<List<String>> expectedTokens = Arrays.asList(expectedTokensBeforeOperator, expectedTokensAfterOperator);

        List<List<String>> extractedTokens = requirementsFileTransformer.extractTokens(validDependencyLine);
        Assertions.assertEquals(expectedTokens, extractedTokens);
    }

    @Test
    void testExtractTokensForValidLineWithoutSpaces() {
        String validDependencyLine = "requests==12.3.3";
        List<String> expectedTokensBeforeOperator = Collections.singletonList(EXPECTED_DEPENDENCY_NAME);
        List<String> expectedTokensAfterOperator = Collections.singletonList(EXPECTED_DEPENDENCY_VERSION);
        List<List<String>> expectedTokens = Arrays.asList(expectedTokensBeforeOperator, expectedTokensAfterOperator);

        List<List<String>> extractedTokens = requirementsFileTransformer.extractTokens(validDependencyLine);
        Assertions.assertEquals(expectedTokens, extractedTokens);
    }

    @Test
    void testExtractTokensForValidLineWithExtras() {
        String validDependencyLine = "requests [security] >= 12.3.3";
        List<String> expectedTokensBeforeOperator = Arrays.asList(EXPECTED_DEPENDENCY_NAME, "[security]");
        List<String> expectedTokensAfterOperator = Collections.singletonList(EXPECTED_DEPENDENCY_VERSION);
        List<List<String>> expectedTokens = Arrays.asList(expectedTokensBeforeOperator, expectedTokensAfterOperator);

        List<List<String>> extractedTokens = requirementsFileTransformer.extractTokens(validDependencyLine);
        Assertions.assertEquals(expectedTokens, extractedTokens);
    }

    @Test
    void testFormatLineForRawLineWithComments() {
        String rawInputLine = "requests [security] >= 12.3.3    # sample comment";
        String expectedLine = "requests [security] >= 12.3.3";

        String formattedLine = requirementsFileTransformer.formatLine(rawInputLine);
        Assertions.assertEquals(expectedLine, formattedLine);
    }

    @Test
    void testFormatLineForRawLineWithUnderscore() {
        String rawInputLine = "requests [security] >= 12.3.3; sys_platform == win32";
        String expectedLine = "requests [security] >= 12.3.3";

        String formattedLine = requirementsFileTransformer.formatLine(rawInputLine);
        Assertions.assertEquals(expectedLine, formattedLine);
    }

    @Test
    void testFormatLineForRawLineWithCommas() {
        String rawInputLine = "requests [security] >= 12.3.3, < 14.0";
        String expectedLine = "requests [security] >= 12.3.3";

        String formattedLine = requirementsFileTransformer.formatLine(rawInputLine);
        Assertions.assertEquals(expectedLine, formattedLine);
    }

    @Test
    void testFormatLineForRawLineWithFlags() {
        String rawInputLine = "-r /path/to/other/requirements.txt";
        String expectedLine = "";

        String formattedLine = requirementsFileTransformer.formatLine(rawInputLine);
        Assertions.assertEquals(expectedLine, formattedLine);
    }

    @Test
    void testFormatLineForRawLineWithEmptyWhiteSpace() {
        String rawInputLine = "          ";
        String expectedLine = "";

        String formattedLine = requirementsFileTransformer.formatLine(rawInputLine);
        Assertions.assertEquals(expectedLine, formattedLine);
    }

    @Test
    void testFormatTokenForDependencyTokenWithExtraAttached() {
        String rawToken = "requests[security]";
        String expectedToken = "requests";

        String formattedToken = requirementsFileTransformer.formatToken(rawToken);
        Assertions.assertEquals(expectedToken, formattedToken);
    }

    @Test
    void testFormatTokenForVersionTokenWithComma() {
        String rawToken = "1.2.4,";
        String expectedToken = "1.2.4";

        String formattedToken = requirementsFileTransformer.formatToken(rawToken);
        Assertions.assertEquals(expectedToken, formattedToken);
    }

    @Test
    void testFormatTokenForVersionTokenWithDoubleQuotes() {
        String rawToken = "\"1.2.4\"";
        String expectedToken = "1.2.4";

        String formattedToken = requirementsFileTransformer.formatToken(rawToken);
        Assertions.assertEquals(expectedToken, formattedToken);
    }

    @Test
    void testFormatTokenForVersionTokenWithSingleQuotes() {
        String rawToken = "'1.2.4'";
        String expectedToken = "1.2.4";

        String formattedToken = requirementsFileTransformer.formatToken(rawToken);
        Assertions.assertEquals(expectedToken, formattedToken);
    }

}
