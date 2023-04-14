# Detectors

The [solution_name] Detector tool runs one or more detectors to find and extract dependencies from all supported package managers.

Each package manager ecosystem is assigned a detector type. Each detector type may have multiple methods (detectors) used to extract dependencies.

Which detector(s) will run against your project is determined by the [detector search](../runningdetect/detectorcascade.md) process.

## Detector Types, Entry Points, and Detectors

The following table contains details for each Detector type,
entry point, and detector.
Details on these terms is available on the [detector search](../runningdetect/detectorcascade.md) page.

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


