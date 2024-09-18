package com.blackduck.integration.detectable.detectables.yarn.functional;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.blackduck.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryParser;
import com.blackduck.integration.detectable.detectables.yarn.parse.entry.section.YarnLockDependencySpecParser;
import com.blackduck.integration.detectable.detectables.yarn.parse.entry.section.YarnLockEntrySectionParserSet;
import com.blackduck.integration.detectable.util.FunctionalTestFiles;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.blackduck.integration.detectable.detectables.yarn.parse.YarnLock;
import com.blackduck.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.blackduck.integration.detectable.detectables.yarn.parse.YarnLockParser;

public class YarnLockParserFunctionalTest {

    @Test
    void testV1Lockfile() throws IOException {
        File lockfile = FunctionalTestFiles.asFile("/yarn/lockfilev1/yarn.lock");
        List<String> yarnLockLines = FileUtils.readLines(lockfile, StandardCharsets.UTF_8);
        Assertions.assertTrue(lockfile.exists());
        YarnLockLineAnalyzer yarnLockLineAnalyzer = new YarnLockLineAnalyzer();
        YarnLockDependencySpecParser yarnLockDependencySpecParser = new YarnLockDependencySpecParser(yarnLockLineAnalyzer);
        YarnLockEntrySectionParserSet yarnLockEntryElementParser = new YarnLockEntrySectionParserSet(yarnLockLineAnalyzer, yarnLockDependencySpecParser);
        YarnLockEntryParser yarnLockEntryParser = new YarnLockEntryParser(yarnLockLineAnalyzer, yarnLockEntryElementParser);
        YarnLockParser yarnLockParser = new YarnLockParser(yarnLockEntryParser);
        YarnLock yarnLock = yarnLockParser.parseYarnLock(yarnLockLines);
        Assertions.assertEquals(4238, yarnLock.getEntries().size());
        Assertions.assertEquals("zwitch", yarnLock.getEntries().get(4237).getIds().get(0).getName());
        Assertions.assertEquals("^1.0.0", yarnLock.getEntries().get(4237).getIds().get(0).getVersion());
    }
    
    @Test
    void testV3Lockfile() throws IOException {
        File lockfile = FunctionalTestFiles.asFile("/yarn/lockfilev3/yarn.lock");
        List<String> yarnLockLines = FileUtils.readLines(lockfile, StandardCharsets.UTF_8);
        Assertions.assertTrue(lockfile.exists());
        YarnLockLineAnalyzer yarnLockLineAnalyzer = new YarnLockLineAnalyzer();
        YarnLockDependencySpecParser yarnLockDependencySpecParser = new YarnLockDependencySpecParser(yarnLockLineAnalyzer);
        YarnLockEntrySectionParserSet yarnLockEntryElementParser = new YarnLockEntrySectionParserSet(yarnLockLineAnalyzer, yarnLockDependencySpecParser);
        YarnLockEntryParser yarnLockEntryParser = new YarnLockEntryParser(yarnLockLineAnalyzer, yarnLockEntryElementParser);
        YarnLockParser yarnLockParser = new YarnLockParser(yarnLockEntryParser);
        YarnLock yarnLock = yarnLockParser.parseYarnLock(yarnLockLines);
        Assertions.assertEquals(1936, yarnLock.getEntries().size());
        Assertions.assertEquals("zen-observable", yarnLock.getEntries().get(1934).getIds().get(0).getName());
        Assertions.assertEquals("0.8.15", yarnLock.getEntries().get(1934).getVersion());
        Assertions.assertEquals("0.8.15", yarnLock.getEntries().get(1934).getIds().get(0).getVersion());
    }
    
    @Test
    void testV4Lockfile() throws IOException {
        File lockfile = FunctionalTestFiles.asFile("/yarn/lockfilev4/yarn.lock");
        List<String> yarnLockLines = FileUtils.readLines(lockfile, StandardCharsets.UTF_8);
        Assertions.assertTrue(lockfile.exists());
        YarnLockLineAnalyzer yarnLockLineAnalyzer = new YarnLockLineAnalyzer();
        YarnLockDependencySpecParser yarnLockDependencySpecParser = new YarnLockDependencySpecParser(yarnLockLineAnalyzer);
        YarnLockEntrySectionParserSet yarnLockEntryElementParser = new YarnLockEntrySectionParserSet(yarnLockLineAnalyzer, yarnLockDependencySpecParser);
        YarnLockEntryParser yarnLockEntryParser = new YarnLockEntryParser(yarnLockLineAnalyzer, yarnLockEntryElementParser);
        YarnLockParser yarnLockParser = new YarnLockParser(yarnLockEntryParser);
        YarnLock yarnLock = yarnLockParser.parseYarnLock(yarnLockLines);
        Assertions.assertEquals(594, yarnLock.getEntries().size());
        Assertions.assertEquals("yeast", yarnLock.getEntries().get(592).getIds().get(0).getName());
        Assertions.assertEquals("0.1.2", yarnLock.getEntries().get(592).getVersion());
        Assertions.assertEquals("0.1.2", yarnLock.getEntries().get(592).getIds().get(0).getVersion());
    }
}
