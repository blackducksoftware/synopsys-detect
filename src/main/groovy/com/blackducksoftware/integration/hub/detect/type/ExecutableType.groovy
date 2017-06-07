package com.blackducksoftware.integration.hub.detect.type

enum ExecutableType {
    MVN([(OperatingSystemType.WINDOWS): 'mvn.cmd', (OperatingSystemType.LINUX): 'mvn']),
    GRADLE([(OperatingSystemType.WINDOWS): 'gradle.bat', (OperatingSystemType.LINUX): 'gradle']),
    GRADLEW([(OperatingSystemType.WINDOWS): 'gradlew.bat', (OperatingSystemType.LINUX): 'gradlew']),
    NUGET((OperatingSystemType.WINDOWS): 'nuget.exe'),
    PIP([(OperatingSystemType.WINDOWS): 'pip.exe', (OperatingSystemType.LINUX): 'pip']),
    PYTHON([(OperatingSystemType.WINDOWS): 'python.exe', (OperatingSystemType.LINUX): 'python']),
    PIP3([(OperatingSystemType.WINDOWS): 'pip3.exe', (OperatingSystemType.LINUX): 'pip3']),
    PYTHON3([(OperatingSystemType.WINDOWS): 'python3.exe', (OperatingSystemType.LINUX): 'python3']),
    GO([(OperatingSystemType.WINDOWS): 'go.exe', (OperatingSystemType.LINUX): 'go']),
    GODEP([(OperatingSystemType.WINDOWS): 'godep.exe', (OperatingSystemType.LINUX): 'godep']);

    private Map<OperatingSystemType, String> osToExecutableMap = [:]

    private ExecutableType(Map<OperatingSystemType, String> osToExecutableMap) {
        this.osToExecutableMap.putAll(osToExecutableMap)
    }

    /**
     * If an operating system specific executable is not present, the linux executable, which could itself not be present, will be returned.
     */
    public String getExecutable(OperatingSystemType operatingSystemType) {
        String osSpecificExecutable = osToExecutableMap[operatingSystemType]
        if (osSpecificExecutable) {
            return osSpecificExecutable
        } else {
            return osToExecutableMap[OperatingSystemType.LINUX]
        }
    }
}
