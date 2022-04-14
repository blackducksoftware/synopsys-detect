package com.synopsys.integration.detectable.detectables.xcode.model;

import java.nio.file.Path;

public class XcodeFileReference {
    private final Path relativeLocation;
    private final FileReferenceType fileReferenceType;

    public XcodeFileReference(Path relativeLocation, FileReferenceType fileReferenceType) {
        this.relativeLocation = relativeLocation;
        this.fileReferenceType = fileReferenceType;
    }

    public Path getRelativeLocation() {
        return relativeLocation;
    }

    public FileReferenceType getFileReferenceType() {
        return fileReferenceType;
    }
}
