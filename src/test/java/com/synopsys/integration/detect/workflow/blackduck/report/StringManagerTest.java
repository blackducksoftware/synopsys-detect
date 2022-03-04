package com.synopsys.integration.detect.workflow.blackduck.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.synopsys.integration.detect.workflow.blackduck.report.pdf.StringManager;

public class StringManagerTest {
    @Test
    public void testStringWidth() throws IOException {
        assertTrue(0 < StringManager.getStringWidth(PDType1Font.HELVETICA, 10.0f, "some text"));
    }

    @ParameterizedTest
    @MethodSource("provideStringWrappingDataStream")
    public void testStringWrapping(StringWrappingData stringWrappingData) throws IOException {
        List<String> actualResults = getActualResults(stringWrappingData);
        assertEquals(stringWrappingData.expectedResults, actualResults);
    }

    private List<String> getActualResults(StringWrappingData stringWrappingData) throws IOException {
        return StringManager.wrapToCombinedList(PDType1Font.HELVETICA, stringWrappingData.fontSize, stringWrappingData.text, stringWrappingData.widthLimit);
    }

    static Stream<StringWrappingData> provideStringWrappingDataStream() {
        String longText = "reallyreallylonglong reallyreallylonglong reallyreallylonglonglong reallyreallylonglong reallyreallylonglong reallyreally longlonglong pants";
        List<String> longResults = Arrays.asList(
            "reallyreallylo",
            "nglong",
            "reallyreallylo",
            "nglonglong",
            "reallyreallylonglong",
            "reallyreallylonglong",
            "reallyreallylo",
            "nglong",
            "reallyreally",
            "longlonglong",
            "pants"
        );
        List<String> shortResults = Arrays.asList(
            "rea",
            "llyr",
            "ea",
            "llyl",
            "on",
            "gl",
            "on",
            "rea",
            "llyr",
            "ea",
            "llyl",
            "on",
            "gl",
            "on",
            "gl",
            "on",
            "reallyreallylonglong",
            "reallyreallylonglong",
            "rea",
            "llyr",
            "ea",
            "llyl",
            "on",
            "gl",
            "on",
            "reallyreally",
            "lon",
            "gl",
            "on",
            "gl",
            "on",
            "pants"
        );

        return Stream.of(
            new StringWrappingData(1.0f, "happymonkeyday", 5, Arrays.asList("happymonke", "yday"))
            , new StringWrappingData(10.0f, "happymonkeyday", 5, Arrays.asList("ha", "pp", "ym", "on", "ke", "yd"))
            , new StringWrappingData(1.0f, "happymonkeyday", 50, Arrays.asList("happymonkeyday"))
            , new StringWrappingData(10.0f, "happymonkeyday", 50, Arrays.asList("happymonke", "yday"))
            , new StringWrappingData(1.0f, "happy monkey day", 5, Arrays.asList("happy", "monkey", "day"))
            , new StringWrappingData(1.0f, "happy monkey day", 50, Arrays.asList("happy monkey day"))
            , new StringWrappingData(10.0f, "happy monkey day", 50, Arrays.asList("happy", "monkey", "day"))
            , new StringWrappingData(10.0f, "happymonkey day", 50, Arrays.asList("happymonke", "day"))
            , new StringWrappingData(10.0f, "happy monkeyday", 50, Arrays.asList("happy", "monkeyday"))
            , new StringWrappingData(10.0f, longText, 50, longResults)
            , new StringWrappingData(10.0f, longText, 5, shortResults)
        );
    }

    private static class StringWrappingData {
        public float fontSize;
        public String text;
        int widthLimit;
        List<String> expectedResults;

        public StringWrappingData(float fontSize, String text, int widthLimit, List<String> expectedResults) {
            this.fontSize = fontSize;
            this.text = text;
            this.widthLimit = widthLimit;
            this.expectedResults = expectedResults;
        }
    }

}
