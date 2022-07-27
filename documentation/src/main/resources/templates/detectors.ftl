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

## [detector_cascade] and accuracy

[detector_cascade] and accuracy capabilities together replace the previous (pre-[solution_name] 8) distinction between "build mode" and "buildless mode",
and provide a better way to get the best results possible, while ensuring that you are alterted (via a [solution_name] failure)
if your accuracy requirements are not met.

Each Detector Type has one or more Entry Points, but usually just one. When multipe Entry Points exist, they handle ............... TBD

See [Detector cascade and accuracy](../downloadingandrunning/detectorcascade.md) for details.


|Detector Type|Entry Point|Detector|Language|Forge|Requirements|Accuracy|
|---|---|---|---|---|---|---|
<#list detectorTypes as detectorType>
|**${detectorType.name}**|||||||
<#list detectorType.entryPoints as entryPoint>
||${entryPoint.name}||||||
<#list entryPoint.detectables as detectable>
||| ${detectable.name} |${detectable.language!""}|${detectable.forge!""} | <#if detectable.requirementsMarkdown?has_content ><#noautoesc>${detectable.requirementsMarkdown!""}</#noautoesc></#if>|${detectable.accuracy!""}|
</#list>
</#list>
</#list>


