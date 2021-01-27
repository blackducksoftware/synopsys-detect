package com.synopsys.integration.detectable.detectables.yarn.parse.entry.element;

import java.util.List;

import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryBuilder;

public interface YarnLockElementTypeParser {
    boolean applies(String elementLine);

    int parseElement(YarnLockEntryBuilder entryBuilder, List<String> yarnLockLines, int bodyElementLineIndex);
}
