# Detectors

The [solution_name] Detector tool uses one or more detectors to find and extract dependencies from all supported package managers.

Each package manager ecosystem is assigned a detector type. Each detector type may have multiple methods (detectors) used to extract dependencies.

## Detector Search

Detector search is the process of finding, for each project, it's root directory, and determining which detector(s) should run on that project root directory.
A project's root directory is the project's top-level directory viewed from the perspective of the project's package manager.

For example, directory /a/b might be the root directory of a gradle project. There might also be gradle subprojects
in subdirectories underneath it, but /a/b is the only directory that detector search must find. (Once it is running,
the detector itself will find those subprojects via its own subproject discovery process, influenced by properties such as `detect.{pkgmgr}.excluded.*` and `detect.{pkgmgr}.included.*`,
that is separate from to detector search.)

Properties that affect detector search:

* detect.detector.search.depth
* detect.detector.search.continue

The Detector tool always performs detector search on the source path (*detect.source.path*).
If *detect.detector.search.depth* is greater than zero, it will search subdirectories as well
to a depth indicated by the value of that property.

For each directory to be searched by detector search, the Detector tool 
first considers "yielding rules", which may specify that a detector
should not run on a directory if another superceding detector already has.

After considering yielding rules, the Detector tool determines which detector(s) should run on that directory
by calling each detector's applicable method
The detector's applicable method decides whether the detector
applies by looking for hints such as files that exist in your project directory
or properties you have set.

Each detector that applies will also do an "extractable" check to see if it can find what it needs, such as package manager executables, inspectors, etc.

Inspectors are used by detectors when the package manager requires an integration or embedded plugin to work.
For example, Gradle uses an inspector as a plugin that executes a custom task. Most detectors do not require an inspector.

Finally, detectors perform extraction to discover project dependencies. This may require operations such as running package manager executables,
parsing package manager files, communicating with web services, etc.

`detect.detector.search.continue` disables yielding rules.
It is usually best to leave property `detect.detector.search.continue` set to false (the default), but there are exceptions.
For example, if two detectors could apply to a directory, but only one runs (as a result of yielding rules), setting 
`detect.detector.search.continue` to true may solve the problem by allowing both detectors to run.
However, if `detector.search.depth` is greater than zero, setting `detect.detector.search.continue` to
true can have the undesirable side effect of running a detector on a directory which is 
not a project root directory, but a subproject directory.

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


