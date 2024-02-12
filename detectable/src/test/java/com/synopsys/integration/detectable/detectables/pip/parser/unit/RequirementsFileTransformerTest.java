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
    private static final String VALID_DEPENDENCY_LINE_WITH_SPACES = "requests == 12.3.3";
    private static final String VALID_DEPENDENCY_LINE_WITHOUT_SPACES = "requests==12.3.3";
    private static final String VALID_DEPENDENCY_LINE_WITH_EXTRAS = "requests [security] <= 12.3.3";

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
        String validDependencyLine = "requests [security] <= 12.3.3";
        List<String> expectedTokensBeforeOperator = Arrays.asList(EXPECTED_DEPENDENCY_NAME, "[security]");
        List<String> expectedTokensAfterOperator = Collections.singletonList(EXPECTED_DEPENDENCY_VERSION);
        List<List<String>> expectedTokens = Arrays.asList(expectedTokensBeforeOperator, expectedTokensAfterOperator);

        List<List<String>> extractedTokens = requirementsFileTransformer.extractTokens(validDependencyLine);
        Assertions.assertEquals(expectedTokens, extractedTokens);
    }


}
