package com.synopsys.integration.detect.battery;

import com.synopsys.integration.configuration.property.Property;

public class BatteryExecutable {
    public Property detectProperty = null;
    public String windowsSourceFileName = null;
    public String linuxSourceFileName = null;
    public BatteryExecutableCreator creator = null;

    public static BatteryExecutable sourceFileExecutable(final String windowsSourceFileName, final String linuxSourceFileName, final BatteryExecutableCreator creator) {
        final BatteryExecutable executable = new BatteryExecutable();
        executable.creator = creator;
        executable.linuxSourceFileName = linuxSourceFileName;
        executable.windowsSourceFileName = windowsSourceFileName;
        return executable;
    }

    public static BatteryExecutable propertyOverrideExecutable(final Property detectProperty, final BatteryExecutableCreator creator) {
        final BatteryExecutable executable = new BatteryExecutable();
        executable.creator = creator;
        executable.detectProperty = detectProperty;
        return executable;
    }
}
