package com.synopsys.integration.detectable.detectables.pip.unit;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.pip.model.PipFreeze;
import com.synopsys.integration.detectable.detectables.pip.model.PipFreezeEntry;
import com.synopsys.integration.detectable.detectables.pip.model.PipenvGraphDependency;
import com.synopsys.integration.detectable.detectables.pip.parser.PipenvFreezeParser;
import com.synopsys.integration.detectable.detectables.pip.parser.PipenvGraphParser;

@UnitTest
public class PipenvFreezeParserTests {
    @Test
    void findsThreeNamesAndVersions() {
        List<String> pipFreezeText = new ArrayList<>();
        pipFreezeText.add("simple==1");
        pipFreezeText.add("with-dashes==2.0");
        pipFreezeText.add("dots.and-dashes==3.1.2");

        final PipenvFreezeParser pipenvFreezeParser = new PipenvFreezeParser(new ExternalIdFactory());
        final PipFreeze pipFreeze = pipenvFreezeParser.parse(pipFreezeText);

        Assertions.assertEquals(3, pipFreeze.getEntries().size(), "Pip freeze should have created three entries.");
        assertContains("simple", "1", pipFreeze);
        assertContains("with-dashes", "2.0", pipFreeze);
        assertContains("dots.and-dashes", "3.1.2", pipFreeze);
    }

    private void assertContains(String name, String version, PipFreeze pipFreeze) {
        Optional<PipFreezeEntry> found = pipFreeze.getEntries().stream()
            .filter(it->it.getName().equals(name))
            .filter(it->it.getVersion().equals(version))
            .findFirst();

        Assertions.assertTrue(found.isPresent(), String.format("Could not find pip freeze entry with name '%s' and version '%s'", name, version));
    }
}
