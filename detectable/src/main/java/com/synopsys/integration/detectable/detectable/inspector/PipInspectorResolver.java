package com.synopsys.integration.detectable.detectable.inspector;

import java.io.File;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public interface PipInspectorResolver {
    File resolvePipInspector() throws DetectableException;
}
