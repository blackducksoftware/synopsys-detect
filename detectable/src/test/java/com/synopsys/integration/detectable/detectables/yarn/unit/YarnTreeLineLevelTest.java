package com.synopsys.integration.detectable.detectables.yarn.unit;

import org.junit.Assert;
import org.junit.Test;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLineLevelParser;

//These examples came from the babel yarn.list
public class YarnTreeLineLevelTest {
    @Test
    public void testLineLevel3() {
        checkLineLevel("│     ├─ graceful-fs@^4.1.11", 3);
    }

    @Test
    public void testLineLevel3Branch() {
        checkLineLevel("│  │  ├─ ansi-styles@^2.2.1", 3);
    }

    @Test
    public void testLineLevel1() {
        checkLineLevel("├─ @types/acorn@4.0.2", 1);
    }

    @Test
    public void testLineLevel2() {
        checkLineLevel("│  ├─ @types/estree@*", 2);
    }

    @Test
    public void testLineLevel4() {
        checkLineLevel("│  │  │  ├─ core-util-is@~1.0.0", 4);
    }

    private void checkLineLevel(String line, int level){
        YarnLineLevelParser lineLevelParser = new YarnLineLevelParser();
        int actual = lineLevelParser.parseTreeLevel(line);
        Assert.assertEquals(level, actual);
    }
}
