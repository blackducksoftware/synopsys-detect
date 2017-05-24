package com.blackducksoftware.integration.hub.packman.type

enum CommandType {
    MVN([(OperatingSystemType.WINDOWS): 'mvn.cmd', (OperatingSystemType.LINUX): 'mvn']),
    GRADLE([(OperatingSystemType.WINDOWS): 'gradle.bat', (OperatingSystemType.LINUX): 'gradle']),
    GRADLEW([(OperatingSystemType.WINDOWS): 'gradlew.bat', (OperatingSystemType.LINUX): 'gradlew']),
    NUGET((OperatingSystemType.WINDOWS): 'nuget.exe'),
    PIP([(OperatingSystemType.WINDOWS): 'pip.exe', (OperatingSystemType.LINUX): 'pip']),
    PYTHON([(OperatingSystemType.WINDOWS): 'python.exe', (OperatingSystemType.LINUX): 'python']),
    PIP3([(OperatingSystemType.WINDOWS): 'pip3.exe', (OperatingSystemType.LINUX): 'pip3']),
    PYTHON3([(OperatingSystemType.WINDOWS): 'python3.exe', (OperatingSystemType.LINUX): 'python3']);

    private Map<OperatingSystemType, String> osToCommandMap = [:]

    private CommandType(Map<OperatingSystemType, String> osToCommandMap) {
        this.osToCommandMap.putAll(osToCommandMap)
    }

    /**
     * If an operating system specific command is not present, the linux command, which could itself not be present, will be returned.
     */
    public String getCommand(OperatingSystemType operatingSystemType) {
        String osSpecificCommand = osToCommandMap[operatingSystemType]
        if (osSpecificCommand) {
            return osSpecificCommand
        } else {
            return osToCommandMap[OperatingSystemType.LINUX]
        }
    }
}
