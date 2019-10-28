package com.synopsys.integration.detectable.detectables.yarn.unit;

import org.junit.Assert;
import org.junit.Test;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockParser;

//These examples came from the babel yarn.lock
public class YarnIndentLineLevelTest {
    @Test
    public void testLineLevel0() {
        checkLineLevel("\"@types/webpack@^3.0.0\":", 0);
    }

    @Test
    public void testLineLevel1version() {
        checkLineLevel("  version \"4.0.2\"", 1);
    }

    @Test
    public void testLineLevel1deps() {
        checkLineLevel("  dependencies:", 1);
    }

    @Test
    public void testLineLevel2() {
        checkLineLevel("    \"@types/node\" \"*\"", 2);
    }

    private void checkLineLevel(final String line, final int level) {
        final YarnLockParser yarnLockParser = new YarnLockParser();
        final int actual = yarnLockParser.countIndent(line);
        Assert.assertEquals(level, actual);
    }
}
