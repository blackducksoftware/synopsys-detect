package com.synopsys.integration.detectable.detectables.go.unit;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.gson.GsonBuilder;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.go.gomod.ReplacementDataExtractor;

public class ReplacementDataExtractorTest {

    private ReplacementDataExtractor replacementDataExtractor = new ReplacementDataExtractor(new GsonBuilder().setPrettyPrinting().create());

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
            replacementDataExtractor.extractReplacementData(input);
        } catch (DetectableException e) {
            threwException = true;
        }

        Assertions.assertTrue(threwException);
    }

}
