package com.synopsys.integration.detect.battery.util.executable;

import com.synopsys.integration.configuration.property.Property;

public class BatteryExecutable {
    public Property detectProperty = null;
    public String windowsSourceFileName = null;
    public String linuxSourceFileName = null;
    public BatteryExecutableCreator creator = null;

    public static BatteryExecutable sourceFileExecutable(String windowsSourceFileName, String linuxSourceFileName, BatteryExecutableCreator creator) {
        BatteryExecutable executable = new BatteryExecutable();
        executable.creator = creator;
        executable.linuxSourceFileName = linuxSourceFileName;
        executable.windowsSourceFileName = windowsSourceFileName;
        return executable;
    }

    public static BatteryExecutable propertyOverrideExecutable(Property detectProperty, BatteryExecutableCreator creator) {
        BatteryExecutable executable = new BatteryExecutable();
        executable.creator = creator;
        executable.detectProperty = detectProperty;
        return executable;
    }
}
