package com.synopsys.integration.detectable.detectables.yarn.unit.parse.entry;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;

public class YarnLockLineAnalyzerTest {
    private static YarnLockLineAnalyzer analyzer;

    @BeforeAll
    static void setup() {
        analyzer = new YarnLockLineAnalyzer();
    }

    @Test
    public void testUnQuoting() {
        assertEquals("abc", analyzer.unquote("\"abc\""));
        assertEquals("\"abc", analyzer.unquote("\"abc"));
        assertEquals("abc", analyzer.unquote("\"'abc'\""));
        assertEquals("abc", analyzer.unquote("'\"abc\"'"));
        assertEquals("'abc", analyzer.unquote("'abc"));
    }

    @Test
    public void testSimpleDepthMeasurement() {
        checkDepth("abc", 0);
        checkDepth("  abc", 1);
        checkDepth("    abc", 2);
    }

    //These examples came from the babel yarn.lock
    @Test
    public void testDepth0() {
        checkDepth("\"@types/webpack@^3.0.0\":", 0);
    }

    @Test
    public void testDepth1version() {
        checkDepth("  version \"4.0.2\"", 1);
    }

    @Test
    public void testDepth1deps() {
        checkDepth("  dependencies:", 1);
    }

    @Test
    public void testDepth2() {
        checkDepth("    \"@types/node\" \"*\"", 2);
    }

    private void checkDepth(String line, int level) {
        int actual = analyzer.measureIndentDepth(line);
        Assertions.assertEquals(level, actual);
    }
}
