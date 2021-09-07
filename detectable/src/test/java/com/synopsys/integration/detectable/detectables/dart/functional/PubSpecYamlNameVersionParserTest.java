package com.synopsys.integration.detectable.detectables.dart.functional;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.dart.PubSpecYamlNameVersionParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.util.NameVersion;

public class PubSpecYamlNameVersionParserTest {
    @Test
    public void testParseNameVersion() throws IOException {
        File pubspecYaml = FunctionalTestFiles.asFile("/dart/pubspec.yaml");
        PubSpecYamlNameVersionParser nameVersionParser = new PubSpecYamlNameVersionParser();
        NameVersion nameVersion = nameVersionParser.parseNameVersion(pubspecYaml).get();
        Assertions.assertEquals("contacts_app", nameVersion.getName());
        Assertions.assertEquals("1.0.0+1", nameVersion.getVersion());
    }
}
