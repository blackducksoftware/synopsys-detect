import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.synopsys.detect.doctor.logparser.DetectExtractionParser;
import com.synopsys.detect.doctor.logparser.LoggedDetectExtraction;

public class ExtractionParserTest {

    @Test
    public void testHeaderParsing() {
        List<String> lines = Arrays.asList("2018-08-21 10:51:48 INFO  [main] --- Starting extraction: GRADLE - Gradle Inspector",
                                           "2018-08-21 10:51:48 INFO  [main] --- Identifier: 1",
                                           "2018-08-21 10:51:48 INFO  [main] --- gradleExe : D:\\BlackDuck\\ScanTarget\\Pilot_AVMSmartPhoneApplication\\gradlew.bat");

        DetectExtractionParser parser = new DetectExtractionParser();
        LoggedDetectExtraction extraction = new LoggedDetectExtraction();

        for(String line : lines){
            parser.parseExtractionHeader(extraction, line);
        }

        Assert.assertEquals("GRADLE - Gradle Inspector", extraction.bomToolDescription);
        Assert.assertEquals("Gradle Inspector", extraction.bomToolName);
        Assert.assertEquals("GRADLE", extraction.bomToolGroup);

        Assert.assertEquals("1", extraction.extractionIdentifier);

        Assert.assertEquals(1, extraction.parameters.size());
        Assert.assertEquals("D:\\BlackDuck\\ScanTarget\\Pilot_AVMSmartPhoneApplication\\gradlew.bat", extraction.parameters.get("gradleExe"));

        Assert.assertEquals(3, extraction.rawHeader.size());
    }
}
