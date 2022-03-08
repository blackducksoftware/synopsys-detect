package com.synopsys.integration.detect.workflow.codelocation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FileNameUtilsTest {

    @Test
    public void testStandard() {
        String relativized = FileNameUtils.relativize("/a/b", "/a/b/d");
        Assertions.assertEquals("d", relativized);
    }

    @Test
    public void testStandardParent() {
        String relativized = FileNameUtils.relativizeParent("/a/b/c", "/a/b/d");
        Assertions.assertEquals("d", relativized);
    }
}
