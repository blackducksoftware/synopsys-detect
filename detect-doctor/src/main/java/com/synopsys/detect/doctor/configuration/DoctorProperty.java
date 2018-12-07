package com.synopsys.detect.doctor.configuration;

import com.blackducksoftware.integration.hub.detect.property.PropertyType;

public enum DoctorProperty {
    DETECT_DIAGNOSTIC_FILE("doctor.detect.diagnostic.file", PropertyType.STRING, ""),

    DETECT_LOG_FILE("doctor.detect.log.file", PropertyType.STRING, ""),
    DETECT_EXTRACTION_ID("doctor.detect.extraction.id", PropertyType.STRING, ""),
    DETECT_OUTPUT_FOLDER("doctor.detect.output.folder", PropertyType.STRING, "");

    private final String propertyName;
    private final PropertyType propertyType;
    private final String defaultValue;

    DoctorProperty(final String propertyName, final PropertyType propertyType, final String defaultValue) {
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.defaultValue = defaultValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

}
