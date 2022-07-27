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
and aim to provide a more user-friendly way to get the best results possible, while ensuring that you are alterted (via a [solution_name] failure)
if your accuracy requirements are not met.

In the table below, most Detector Types have a single Entry Point. For those Detector Types, the Entry Point column can be ignored.

There are a few Detector Types for which multiple Entry Points are defined.
When multipe Entry Points are defined for a Detector Type,
they exist to support the relatively rare need to define different nesting rules
within the same Detector Type for different scenarios.
(Nesting rules can usually be, and usually are, applied at the Detector Type level.)
For example: Detect should ignore XCODE project files that are nested inside an XCODE project that it has already processed.
Entry Points provide the mechanism by which more nuanced nesting rules such as this are applied.

Each Entry Point has one or more Detectors. Detectors are attempted in the order listed until one applies and succeeds.
If none succeed, [solution_name] proceeds to the next Entry Point (if there is one) for the Detector Type.

See [Detector cascade and accuracy](../downloadingandrunning/detectorcascade.md) for additional information on [detector_cascade]..

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


