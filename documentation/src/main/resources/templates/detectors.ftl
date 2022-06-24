# Detectors

[solution_name] uses detectors to find and extract dependencies from all supported package managers.

Each package manager ecosystem is assigned a detector type. Each detector type may have multiple methods used to extract dependencies.

## Detector Search

Detectors first check to see if they apply to your project by looking for hints such as files that exist in your project directory
or properties you have set.

By default, detectors only search the project directory. In some cases, such as when your project contains sub-projects,
or when package manager files reside in sub-directories, you may need to tell [solution_name] to search sub-directories
by increasing the detector search depth. For more information, refer to [detector search depth](../properties/configuration/paths.md#detector-search-depth).

Detectors then check that your environment is extractable, meaning you have all the relevant executables such as NPM or a Gradle wrapper, and all relevant downloads are present or available, such as the Docker or NuGet inspector.

Inspectors are used by detectors when the package manager requires an integration or embedded plugin to work. For example, Gradle uses an inspector as a plugin that executes a custom task. Most detectors do not require an inspector.

Finally, detectors perform their extraction to find your dependencies. This may require, but is not limited to, running executables, performing builds, parsing files, and communicating with web services.

## Detector Cascade

Each Detector has multiple ways it can extract dependencies from your project. The most accurate approaches are always attempted first
however if extraction cannot be completed, the next approach will be automatically attempted which may be less accurate.
By default, [solution_name] will only include extractions with high accuracy and low accuracy extractions will cause a non-zero exit code.
For example, if Gradle CLI applies but cannot extract because no Gradle executable could be found, the Gradle Project Inspector will be attempted but because it does not produce
accurate results, [solution_name] will exit with a non-zero exit code.



|Name|Language|Forge|Requirements
|---|---|---|---|
<#list detectors as detector>
|**${detector.detectorType}**||||
<#list detector.detectables as detectable>
| ${detectable.name} |${detectable.language!""}|${detectable.forge!""} | <#if detectable.requirementsMarkdown?has_content ><#noautoesc>${detectable.requirementsMarkdown!""}</#noautoesc></#if>|
</#list>
</#list>