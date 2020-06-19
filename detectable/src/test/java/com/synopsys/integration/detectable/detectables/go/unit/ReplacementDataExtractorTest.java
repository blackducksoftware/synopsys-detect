package com.synopsys.integration.detectable.detectables.go.unit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.go.gomod.ReplacementDataExtractorB;

public class ReplacementDataExtractorTest {

    private Map<String, String> replaceMentData = new HashMap<>();

    private ReplacementDataExtractorB replacementDataExtractorB = new ReplacementDataExtractorB(replaceMentData);

    @Test
    public void extractorThrowsExceptionIfVersionFormatIsOdd() {
        List<String> input = Arrays.asList(
            "{\n",
            "\t\"Path\": \"github.com/sirupsen/logrus\",\n",
            "\t\"Version\": \"v1.1.1\",\n",
            "\t\"Replace\": {\n",
            "\t\t\"Path\": \"github.com/sirupsen/logrus\",\n",
            "\t\t\"Version\": \"v1.1.1\n",
            "\t}\n",
            "}\n"
        );

        boolean threwException = false;

        try {
            replacementDataExtractorB.extractReplacementData(input);
        } catch (DetectableException e) {
            threwException = true;
        }

        Assertions.assertTrue(threwException);
    }

}
