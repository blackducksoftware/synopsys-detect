package com.synopsys.integration.detect.workflow.codelocation;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

public class FileNameUtilsTest {

    @Test
    public void testStandard() {
        final String relativized = FileNameUtils.relativize("/a/b", "/a/b/d");
        assertEquals("d", relativized);
    }

    @Test
    public void testStandardParent() {
        final String relativized = FileNameUtils.relativizeParent("/a/b/c", "/a/b/d");
        assertEquals("d", relativized);
    }
}
