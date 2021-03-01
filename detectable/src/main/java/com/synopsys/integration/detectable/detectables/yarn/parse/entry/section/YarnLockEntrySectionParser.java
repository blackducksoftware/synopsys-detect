/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn.parse.entry.section;

import java.util.List;

import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryBuilder;

public interface YarnLockEntrySectionParser {
    boolean applies(String sectionFirstLine);

    int parseSection(YarnLockEntryBuilder entryBuilder, List<String> yarnLockLines, int lineIndexOfStartOfSection);
}
