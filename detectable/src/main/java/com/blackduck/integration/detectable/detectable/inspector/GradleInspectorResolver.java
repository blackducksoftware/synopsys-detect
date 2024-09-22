package com.blackduck.integration.detectable.detectable.inspector;

import java.io.File;

import com.blackduck.integration.detectable.detectable.exception.DetectableException;

public interface GradleInspectorResolver {
    File resolveGradleInspector() throws DetectableException;
}
