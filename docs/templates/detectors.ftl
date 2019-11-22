# Detectors

${solution_name} uses "detectors" to find and extract dependencies from all supported package managers.

Each package manager ecosystem is given a detector type. Each detector type may have multiple ways it extracts dependencies.

## Detector Search

Detectors first check to see if they "apply" to your project by looking for hints such as files that exist in your project directory,
or properties you have set.

By default, detectors only search the project directory itself. In some cases (such as when your project contains sub-projects,
or when package manager files reside in sub-directories), you may need to tell ${solution_name} to search sub-directories
by increasing the detector search depth. See [detector search depth](../properties/Configuration/paths.md#detector-search-depth) for details.

Detectors then check that your environment is "extractable", meaning you have all the relevant executables (such as npm or a gradle wrapper) and all the relevant downloads are present or available (such as the docker or nuget inspector).

Inspectors are used by detectors when the package manager requires an integration or embedded plugin to work. For example, Gradle uses an inspector as a plugin that executes a custom task. Most detectors do not require an inspector.

Finally, detectors perform their "extraction" to find your dependencies. This may require but is not limited to: running executables, performing builds, parsing files and talking to web services.

## Build detectors vs. buildless detectors

The recommended way to run ${solution_name} is as a post-build step, so that it has access to both build artifacts and the build tools (package manager, etc.) used to build the project.
${solution_name}'s "build detectors" work in this environment and produce the most accurate results. By default, ${solution_name} runs build detectors.

If you can't build your project, you may still be able to use ${solution_name}'s buildless detectors. The results from buildless detectors may not be as accurate as the results from
build detectors would be, but buildless detectors can run without accessing the tools required to build the project. You can choose to run
buildless detectors using the [buildless mode property](../properties/Configuration/general.md#buildless-mode).

The tables below show which detectors run in the default (build) mode, and which detectors run in buildless mode. There is some overlap across the two lists.

## Build detectors

Build detectors run package manager commands (for example: *mvn dependency:tree*) and/or inspectors (for example, the [Gradle inspector](inspectors.md#gradle-inspector)) to derive dependency information. Inspectors
act as extensions to ${solution_name} and will be downloaded automatically if needed. The required commands (indicated in the table below) must be present (and findable) on your system. ${solution_name} uses
the system PATH to find commands. In some cases, as an alternative to the system PATH, the location of a command can be provided via a property.

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
