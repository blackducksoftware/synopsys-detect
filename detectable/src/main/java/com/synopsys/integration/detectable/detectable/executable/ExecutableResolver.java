package com.synopsys.integration.detectable.detectable.executable;

import java.io.File;

import org.antlr.v4.runtime.misc.Nullable;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public interface ExecutableResolver {
    @Nullable
    File resolveExecutable(ExecutableType executableType, DetectableEnvironment environment) throws DetectableException;
}
