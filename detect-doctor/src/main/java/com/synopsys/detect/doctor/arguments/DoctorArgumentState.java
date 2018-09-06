package com.synopsys.detect.doctor.arguments;

public class DoctorArgumentState {
    private final boolean isExtraction;

    public DoctorArgumentState(final boolean isExtraction) {
        this.isExtraction = isExtraction;
    }

    public boolean getIsExtraction() {
        return isExtraction;
    }
}
