/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.detect.type

enum ExecutableType {
    MVN([(OperatingSystemType.WINDOWS): 'mvn.cmd', (OperatingSystemType.LINUX): 'mvn']),
    MVNW([(OperatingSystemType.WINDOWS): 'mvnw.bat', (OperatingSystemType.LINUX): 'mvnw']),
    GRADLE([(OperatingSystemType.WINDOWS): 'gradle.bat', (OperatingSystemType.LINUX): 'gradle']),
    GRADLEW([(OperatingSystemType.WINDOWS): 'gradlew.bat', (OperatingSystemType.LINUX): 'gradlew']),
    NUGET((OperatingSystemType.WINDOWS): 'nuget.exe'),
    PIP([(OperatingSystemType.WINDOWS): 'pip.exe', (OperatingSystemType.LINUX): 'pip']),
    VIRTUALENV([(OperatingSystemType.WINDOWS): 'virtualenv.exe', (OperatingSystemType.LINUX): 'virtualenv']),
    PYTHON([(OperatingSystemType.WINDOWS): 'python.exe', (OperatingSystemType.LINUX): 'python']),
    PIP3([(OperatingSystemType.WINDOWS): 'pip3.exe', (OperatingSystemType.LINUX): 'pip3']),
    PYTHON3([(OperatingSystemType.WINDOWS): 'python3.exe', (OperatingSystemType.LINUX): 'python3']),
    GO([(OperatingSystemType.WINDOWS): 'go.exe', (OperatingSystemType.LINUX): 'go']),
    GO_DEP([(OperatingSystemType.WINDOWS): 'dep.exe', (OperatingSystemType.LINUX): 'dep']),
    DOCKER([(OperatingSystemType.WINDOWS): 'docker.exe', (OperatingSystemType.LINUX): 'docker']),
    NPM([(OperatingSystemType.WINDOWS): 'npm.cmd', (OperatingSystemType.LINUX): 'npm']),
    BASH([(OperatingSystemType.WINDOWS): 'bash.exe', (OperatingSystemType.LINUX): 'bash']),
    PERL([(OperatingSystemType.WINDOWS): 'perl.exe', (OperatingSystemType.LINUX): 'perl']),
    CPAN([(OperatingSystemType.WINDOWS): 'cpan.exe', (OperatingSystemType.LINUX): 'cpan']),
    CPANM([(OperatingSystemType.WINDOWS): 'cpanm.exe', (OperatingSystemType.LINUX): 'cpanm'])

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
