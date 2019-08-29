package com.synopsys.integration.detectable.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;

public class MockDetectableEnvironment extends DetectableEnvironment {

    public MockDetectableEnvironment(final File directory) {
        super(directory);
    }

    public static MockDetectableEnvironment empty() {
        return new MockDetectableEnvironment(new File("."));
    }
}
