package com.synopsys.integration.detector.finder;

import java.io.IOException;

public class DirectoryFinderDirectoryListException extends Exception {
    public DirectoryFinderDirectoryListException(String message, IOException e) {
        super(message, e);
    }
}
