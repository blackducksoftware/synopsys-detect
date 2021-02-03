package com.synopsys.integration.detectable.detectables.yarn.functional;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLock;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockParser;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryParser;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.section.YarnLockDependencySpecParser;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.section.YarnLockEntrySectionParserSet;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

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
}
