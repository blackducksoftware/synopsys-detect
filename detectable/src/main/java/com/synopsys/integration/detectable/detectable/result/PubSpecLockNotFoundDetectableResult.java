package com.synopsys.integration.detectable.detectable.result;

public class PubSpecLockNotFoundDetectableResult extends FailedDetectableResult {
    private static final String FORMAT = "A pubspec.yaml was located in %s, but the pubspec.lock file was NOT located. Please run 'pub get' or, if project requires the Flutter SDK, run 'flutter pub get' in that location and try again.";

    public PubSpecLockNotFoundDetectableResult(String directoryPath) {
        super(String.format(FORMAT, directoryPath));
    }
}
