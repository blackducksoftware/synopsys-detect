package com.synopsys.integration.detectable.detectables.yarn.unit.parse.entry.element;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntry;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryBuilder;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryId;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.element.YarnLockEntryHeaderParser;

public class YarnLockEntryHeaderParserTest {
    private static YarnLockEntryHeaderParser yarnLockParser;

    @BeforeAll
    static void setup() {
        YarnLockLineAnalyzer yarnLockLineAnalyzer = new YarnLockLineAnalyzer();
        yarnLockParser = new YarnLockEntryHeaderParser(yarnLockLineAnalyzer);
    }

    @Test
    void testList() {
        String line = "\"@apollographql/apollo-tools@^0.4.2\", \"@apollographql/apollo-tools@^0.4.3\":";
        List<String> lines = Arrays.asList(line);
        Assertions.assertTrue(yarnLockParser.applies(line));

        YarnLockEntryBuilder builder = new YarnLockEntryBuilder();
        yarnLockParser.parseElement(builder, lines, 0);

        // Complete the builder requirements and build the entry
        builder.setVersion("testVersion");
        Optional<YarnLockEntry> entry = builder.build();

        Assertions.assertTrue(entry.isPresent());
        List<YarnLockEntryId> ids = entry.get().getIds();
        Assertions.assertEquals(2, ids.size());
        Assertions.assertEquals(ids.get(0).getName(), "@apollographql/apollo-tools");
        Assertions.assertEquals(ids.get(0).getVersion(), "^0.4.2");
        Assertions.assertEquals(ids.get(1).getName(), "@apollographql/apollo-tools");
        Assertions.assertEquals(ids.get(1).getVersion(), "^0.4.3");
    }

    @Test
    void testParserHandlesMissingSymbol() {
        String line = "example, example@1";
        List<String> lines = Arrays.asList(line);
        Assertions.assertTrue(yarnLockParser.applies(line));

        YarnLockEntryBuilder builder = new YarnLockEntryBuilder();
        yarnLockParser.parseElement(builder, lines, 0);

        // Complete the builder requirements and build the entry
        builder.setVersion("testVersion");
        Optional<YarnLockEntry> entry = builder.build();

        Assertions.assertTrue(entry.isPresent());
        List<YarnLockEntryId> ids = entry.get().getIds();
        Assertions.assertEquals(2, ids.size());
        Assertions.assertEquals(ids.get(0).getName(), "example");
        Assertions.assertEquals(ids.get(0).getVersion(), "");
        Assertions.assertEquals(ids.get(1).getName(), "example");
        Assertions.assertEquals(ids.get(1).getVersion(), "1");
    }

    @Test
    void handlesSymbolInName() {
        String line = "@example";
        List<String> lines = Arrays.asList(line);
        Assertions.assertTrue(yarnLockParser.applies(line));

        YarnLockEntryBuilder builder = new YarnLockEntryBuilder();
        yarnLockParser.parseElement(builder, lines, 0);

        // Complete the builder requirements and build the entry
        builder.setVersion("testVersion");
        Optional<YarnLockEntry> entry = builder.build();

        Assertions.assertTrue(entry.isPresent());
        List<YarnLockEntryId> ids = entry.get().getIds();
        Assertions.assertEquals(1, ids.size());
        Assertions.assertEquals(ids.get(0).getName(), "@example");
        Assertions.assertEquals(ids.get(0).getVersion(), "");
    }
}
