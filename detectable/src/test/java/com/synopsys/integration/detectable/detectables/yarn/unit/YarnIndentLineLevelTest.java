package com.synopsys.integration.detectable.detectables.yarn.unit;

import org.junit.Assert;
import org.junit.Test;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLineLevelParser;

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

    private void checkLineLevel(String line, int level){
        YarnLineLevelParser lineLevelParser = new YarnLineLevelParser();
        int actual = lineLevelParser.parseIndentLevel(line);
        Assert.assertEquals(level, actual);
    }
}
