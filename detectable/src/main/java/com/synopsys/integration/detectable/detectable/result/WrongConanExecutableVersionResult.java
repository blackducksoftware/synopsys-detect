package com.synopsys.integration.detectable.detectable.result;

public class WrongConanExecutableVersionResult extends FailedDetectableResult {
    private static final String FORMAT = "Conan CLI version is %s. Expected %s.";

    public WrongConanExecutableVersionResult(String expected, String actual) {
        super(String.format(FORMAT, actual, expected));
    }
}
