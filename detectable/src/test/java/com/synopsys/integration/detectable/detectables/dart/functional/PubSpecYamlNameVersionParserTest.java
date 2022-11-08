package com.synopsys.integration.detectable.detectables.dart.functional;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.dart.PubSpecYamlNameVersionParser;
import com.synopsys.integration.util.NameVersion;

public class PubSpecYamlNameVersionParserTest {
    @Test
    public void testParseNameVersion() throws IOException {
        List<String> pubspecYaml = Arrays.asList(
            "name: contacts_app",
            "description: A new Flutter project.",
            "",
            "# Test file for PubSpecYamlNameVersionParserTest",
            "version: 1.0.0+1"
        );
        PubSpecYamlNameVersionParser nameVersionParser = new PubSpecYamlNameVersionParser();
        NameVersion nameVersion = nameVersionParser.parseNameVersion(pubspecYaml).get();
        Assertions.assertEquals("contacts_app", nameVersion.getName());
        Assertions.assertEquals("1.0.0+1", nameVersion.getVersion());
    }
}
