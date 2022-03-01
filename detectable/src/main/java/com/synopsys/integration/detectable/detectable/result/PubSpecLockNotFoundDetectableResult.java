package com.synopsys.integration.detectable.detectable.result;

public class PubSpecLockNotFoundDetectableResult extends FailedDetectableResult {
    private final String directoryPath;

    public PubSpecLockNotFoundDetectableResult(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public String toDescription() {
        return String.format(
            "A pubspec.yaml was located in %s, but the pubspec.lock file was NOT located. Please run 'pub get' or, if project requires the Flutter SDK, run 'flutter pub get' in that location and try again.",
            directoryPath
        );
    }
}
