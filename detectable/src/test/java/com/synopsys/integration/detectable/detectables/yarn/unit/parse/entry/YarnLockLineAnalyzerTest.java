package com.synopsys.integration.detectable.detectables.yarn.unit.parse.entry;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;

public class YarnLockLineAnalyzerTest {

    @Test
    public void testUnQuoting() {
        YarnLockLineAnalyzer analyzer = new YarnLockLineAnalyzer();
        assertEquals("abc", analyzer.unquote("\"abc\""));
        assertEquals("\"abc", analyzer.unquote("\"abc"));
        assertEquals("abc", analyzer.unquote("\"'abc'\""));
        assertEquals("abc", analyzer.unquote("'\"abc\"'"));
        assertEquals("'abc", analyzer.unquote("'abc"));
    }

    @Test
    public void testDepthMeasurement() {
        YarnLockLineAnalyzer analyzer = new YarnLockLineAnalyzer();
        assertEquals(0, analyzer.measureIndentDepth("abc"));
        assertEquals(1, analyzer.measureIndentDepth("  abc"));
        assertEquals(2, analyzer.measureIndentDepth("    abc"));
    }
}
