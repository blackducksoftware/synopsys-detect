package com.synopsys.integration.detectable.detectables.pip.inspector.unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.pipenv.build.model.PipFreeze;
import com.synopsys.integration.detectable.detectables.pipenv.build.model.PipFreezeEntry;
import com.synopsys.integration.detectable.detectables.pipenv.build.parser.PipenvFreezeParser;

@UnitTest
public class PipenvFreezeParserTests {
    @Test
    void findsThreeNamesAndVersions() {
        List<String> pipFreezeText = new ArrayList<>();
        pipFreezeText.add("simple==1");
        pipFreezeText.add("with-dashes==2.0");
        pipFreezeText.add("dots.and-dashes==3.1.2");

        PipenvFreezeParser pipenvFreezeParser = new PipenvFreezeParser();
        PipFreeze pipFreeze = pipenvFreezeParser.parse(pipFreezeText);

        Assertions.assertEquals(3, pipFreeze.getEntries().size(), "Pip freeze should have created three entries.");
        assertContains("simple", "1", pipFreeze);
        assertContains("with-dashes", "2.0", pipFreeze);
        assertContains("dots.and-dashes", "3.1.2", pipFreeze);
    }

    private void assertContains(String name, String version, PipFreeze pipFreeze) {
        Optional<PipFreezeEntry> found = pipFreeze.getEntries().stream()
            .filter(it -> it.getName().equals(name))
            .filter(it -> it.getVersion().equals(version))
            .findFirst();

        Assertions.assertTrue(found.isPresent(), String.format("Could not find pip freeze entry with name '%s' and version '%s'", name, version));
    }
}
