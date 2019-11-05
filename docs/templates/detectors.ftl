# Detectors

Detect uses "detectors" to find and extract dependencies from all supported package managers.

Each package manager ecosystem is given a detector type. Each detector type may have multiple ways it extracts dependencies.

All supported detector types are listed here.

## Build detectors
|Detector type|Detectors|
|---|---|
<#list build as group>
|${group.groupName}|${group.detectors?join(", ")}|
</#list>

## Buildless detectors
|Detector type|Detectors|
|---|---|
<#list buildless as group>
|${group.groupName}|${group.detectors?join(", ")}|
</#list>

## Detector Search

Detectors first check if they "apply" to your project by looking for specific triggers such as present files or specified configuration properties.

Detectors then check that your environment is "extractable", meaning you have all the relevant executables (such as npm or a gradle wrapper) and all the relevant downloads are present or available (such as the docker or nuget inspector).

Inspectors are used by detectors when the package manager requires an integration or embedded plugin to work. For example, Gradle uses an inspector as a plugin that executes a custom task. Most detectors do not require an inspector.

Finally, detectors perform their "extraction" to find your dependencies. This may require but is not limited to: running executables, performing builds, parsing files and talking to web services.
