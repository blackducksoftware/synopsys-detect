package com.synopsys.detect.doctor.arguments;

import com.blackducksoftware.integration.hub.detect.configuration.DetectPropertyType;

public enum DoctorProperty {
    DETECT_LOG_FILE("doctor.detect.log.file", DetectPropertyType.STRING, ""),
    DETECT_EXTRACTION_ID("doctor.detect.extraction.id", DetectPropertyType.STRING, ""),
    DETECT_OUTPUT_FOLDER("doctor.detect.output.folder", DetectPropertyType.STRING, "");

    private final String propertyName;
    private final DetectPropertyType propertyType;
    private final String defaultValue;

    DoctorProperty(final String propertyName, final DetectPropertyType propertyType, final String defaultValue) {
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.defaultValue = defaultValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public DetectPropertyType getPropertyType() {
        return propertyType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

}
