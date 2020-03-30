# Detectors

${solution_name} uses detectors to find and extract dependencies from all supported package managers.

Each package manager ecosystem is assigned a detector type. Each detector type may have multiple methods used to extract dependencies.

## Detector Search

Detectors first check to see if they apply to your project by looking for hints such as files that exist in your project directory 
or properties you have set.

By default, detectors only search the project directory. In some cases, such as when your project contains sub-projects,
or when package manager files reside in sub-directories, you may need to tell ${solution_name} to search sub-directories
by increasing the detector search depth. For more information, refer to [detector search depth](../../properties/configuration/paths/#detector-search-depth).

Detectors then check that your environment is extractable, meaning you have all the relevant executables such as NPM or a Gradle wrapper, and all relevant downloads are present or available, such as the Docker or NuGet inspector.

Inspectors are used by detectors when the package manager requires an integration or embedded plugin to work. For example, Gradle uses an inspector as a plugin that executes a custom task. Most detectors do not require an inspector.

Finally, detectors perform their extraction to find your dependencies. This may require, but is not limited to, running executables, performing builds, parsing files, and communicating with web services.

## Build detectors versus buildless detectors

The recommended way to run ${solution_name} is as a post-build step, so that it has access to both build artifacts and the build tools (package managers and others) used to build the project.
${solution_name}'s build detectors work in this environment and produce the most accurate results. By default, ${solution_name} runs build detectors.

If you can't build your project, you may still be able to use ${solution_name}'s buildless detectors. The results from buildless detectors may not be as accurate as the results from
build detectors, but buildless detectors can run without accessing the tools required to build the project. You can choose to run
buildless detectors using the [buildless mode property](../../properties/configuration/general/#buildless-mode).

The following tables show which detectors run in the default (build) mode, and which detectors run in buildless mode. There is some overlap across the two lists.

## Build detectors

Build detectors run package manager commands; for example: *mvn dependency:tree*, and/or inspectors; for example, the [Gradle inspector](../inspectors/#gradle-inspector) to derive dependency information. Inspectors
act as extensions to ${solution_name}, and are downloaded automatically if needed. The required commands as shown in the following table must be present and findable on your system. ${solution_name} uses
the system PATH to find commands. In some cases as an alternative to the system PATH, the location of a command can be provided through a property.

|Type|Name|Language|Forge|Requirements
|---|---|---|---|---|
<#list build as detector>
|${detector.detectorType} | ${detector.detectorName} |${detector.detectableLanguage!""}|${detector.detectableForge!""} | <#if detector.detectableRequirementsMarkdown?has_content ><#noautoesc>${detector.detectableRequirementsMarkdown!""}</#noautoesc></#if>|
</#list>

## Buildless detectors

Buildless detectors parse package manager files; for example, *pom.xml*, to derive dependency information.

|Type|Name|Language|Forge|Requirements
|---|---|---|---|---|
<#list buildless as detector>
|${detector.detectorType} | ${detector.detectorName} |${detector.detectableLanguage!""}|${detector.detectableForge!""} | <#if detector.detectableRequirementsMarkdown?has_content ><#noautoesc>${detector.detectableRequirementsMarkdown!""}</#noautoesc></#if>|
</#list>
