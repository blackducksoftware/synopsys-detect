package com.blackduck.integration.detectable.detectable;

import java.io.File;

import com.blackduck.integration.detectable.detectable.exception.DetectableException;

public interface Resolver {
    File resolve() throws DetectableException;
}
