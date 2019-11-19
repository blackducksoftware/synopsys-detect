# Detectors

${solution_name} uses "detectors" to find and extract dependencies from all supported package managers.

Each package manager ecosystem is given a detector type. Each detector type may have multiple ways it extracts dependencies.

## Detector Search

Detectors first check to see if they "apply" to your project by looking for hints such as files that exist in your project directory,
or properties you have set.

By default, detectors only search the project directory itself. In some cases (such as when your project contains sub-projects,
or when package manager files reside in sub-directories), you may need to tell ${solution_name} to search sub-directories
by increasing the detector search depth. See [detector search depth](/properties/Configuration/paths/#detector-search-depth) for details.

Detectors then check that your environment is "extractable", meaning you have all the relevant executables (such as npm or a gradle wrapper) and all the relevant downloads are present or available (such as the docker or nuget inspector).

Inspectors are used by detectors when the package manager requires an integration or embedded plugin to work. For example, Gradle uses an inspector as a plugin that executes a custom task. Most detectors do not require an inspector.

Finally, detectors perform their "extraction" to find your dependencies. This may require but is not limited to: running executables, performing builds, parsing files and talking to web services.

## Build detectors

Build detectors run package manager commands (for example: *mvn dependency:tree*) or inspectors (for example, the [Gradle inspector](/components/inspectors/#gradle-inspector)) to derive dependency information.

|Type|Name|Language|Forge|Requirements
|---|---|---|---|---|
<#list build as detector>
|${detector.detectorType} | ${detector.detectorName} |${detector.detectableLanguage!""}|${detector.detectableForge!""} | <#if detector.detectableRequirementsMarkdown?has_content ><#noautoesc>${detector.detectableRequirementsMarkdown!""}</#noautoesc></#if>|
</#list>

## Buildless detectors

Buildless detectors parse package manager files (for example: *pom.xml*) to derive dependency information.

|Type|Name|Language|Forge|Requirements
|---|---|---|---|---|
<#list buildless as detector>
|${detector.detectorType} | ${detector.detectorName} |${detector.detectableLanguage!""}|${detector.detectableForge!""} | <#if detector.detectableRequirementsMarkdown?has_content ><#noautoesc>${detector.detectableRequirementsMarkdown!""}</#noautoesc></#if>|
</#list>