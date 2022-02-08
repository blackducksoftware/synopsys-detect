package com.synopsys.integration.detectable.detectables.yarn.parse.entry.section;

import java.util.List;

import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryBuilder;

public interface YarnLockEntrySectionParser {
    boolean applies(String sectionFirstLine);

    int parseSection(YarnLockEntryBuilder entryBuilder, List<String> yarnLockLines, int lineIndexOfStartOfSection);
}
