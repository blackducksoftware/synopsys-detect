package com.synopsys.integration.detectable.detectables.yarn.parse.entry.element;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryBuilder;

public class DependencyAdder {
    private final YarnLockDependencySpecParser yarnLockDependencySpecParser;

    public DependencyAdder(YarnLockDependencySpecParser yarnLockDependencySpecParser) {
        this.yarnLockDependencySpecParser = yarnLockDependencySpecParser;
    }

    public void addDependencyToEntry(YarnLockEntryBuilder yarnLockEntryBuilder, String s) {
        // TODO when should optional be true?
        YarnLockDependency yarnLockDependency = yarnLockDependencySpecParser.parse(s, false);
        yarnLockEntryBuilder.addDependency(yarnLockDependency);
    }
}
