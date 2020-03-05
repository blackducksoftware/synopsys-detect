package com.synopsys.integration.detectable.detectables.swift.functional;

import java.io.File;
import java.io.IOException;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.SwiftResolver;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;

public class SwiftDetectableTest extends DetectableFunctionalTest {

    public SwiftDetectableTest() throws IOException {
        super("swift");
    }

    @Override
    protected void setup() throws IOException {

    }

    @NotNull
    @Override
    public Detectable create(@NotNull final DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createSwiftCliDetectable(detectableEnvironment, new SwiftResolver() {
            @Override
            public File resolveSwift() throws DetectableException {
                return null;
            }
        });
    }

    @Override
    public void assertExtraction(@NotNull final Extraction extraction) {

    }
}
