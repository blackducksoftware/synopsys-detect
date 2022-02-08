package com.synopsys.integration.detectable.detectable;

import java.io.File;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public interface Resolver {
    File resolve() throws DetectableException;
}
