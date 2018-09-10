package com.synopsys.detect.doctor.configuration;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.DetectPropertySource;
import com.blackducksoftware.integration.hub.detect.property.PropertyMap;
import com.blackducksoftware.integration.hub.detect.property.SpringPropertySource;

public class DoctorConfiguration {
    private final Logger logger = LoggerFactory.getLogger(DetectConfiguration.class);
    private final SpringPropertySource propertySource;
    private final PropertyMap<DoctorProperty> propertyMap;

    public DoctorConfiguration(final SpringPropertySource propertySource, final PropertyMap<DoctorProperty> propertyMap) {
        this.propertySource = propertySource;
        this.propertyMap = propertyMap;
    }

    public void init() {
        Arrays.stream(DoctorProperty.values()).forEach(currentProperty -> {
            initStandardProperty(currentProperty);
        });
    }

    private void initStandardProperty(final DoctorProperty currentProperty) {
        propertyMap.setProperty(currentProperty, currentProperty.getPropertyType(), propertySource.getProperty(currentProperty.getPropertyName()));
    }

    // Redirect to the underlying map
    public boolean getBooleanProperty(final DoctorProperty doctorProperty) {
        return propertyMap.getBooleanProperty(doctorProperty);
    }

    public Long getLongProperty(final DoctorProperty doctorProperty) {
        return propertyMap.getLongProperty(doctorProperty);
    }

    public Integer getIntegerProperty(final DoctorProperty doctorProperty) {
        return propertyMap.getIntegerProperty(doctorProperty);
    }

    public String[] getStringArrayProperty(final DoctorProperty doctorProperty) {
        return propertyMap.getStringArrayProperty(doctorProperty);
    }

    public String getProperty(final DoctorProperty doctorProperty) {
        return propertyMap.getProperty(doctorProperty);
    }

    public String getPropertyValueAsString(final DoctorProperty doctorProperty) {
        return propertyMap.getPropertyValueAsString(doctorProperty, doctorProperty.getPropertyType());
    }

}
