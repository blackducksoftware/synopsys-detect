package com.synopsys.integration.detectable.detectable.result;

public class WrongConanExecutableVersionResult extends FailedDetectableResult {
    private String expected;
    private String actual;

    public WrongConanExecutableVersionResult(String expected, String actual) {
        this.expected = expected;
        this.actual = actual;
    }

    @Override
    public String toDescription() {
        return String.format("Conan CLI version is %s. Expected %s.", actual, expected);
    }
}
