package com.synopsys.integration.detector.finder;

import java.io.IOException;

public class DetectorFinderDirectoryListException extends Exception {
    public DetectorFinderDirectoryListException(String message, IOException e) {
        super(message, e);
    }
}
