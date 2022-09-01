# Detector search, cascade, and accuracy

## Detector search

Detector search is the process of finding, for each project, it's root directory, and determining which detector(s) should run on that project root directory.
Detector search is performed by the detector tool, and is described [here](../components/detectors.md).

[detector_cascade] is the current (starting in [solution_name] 8.0.0) implementation of detector search.
The next section describes how [detector_cascade] makes decisions about which detectors to run on each
searched directory.

## [detector_cascade] 

[detector_cascade] and accuracy capabilities together replace the previous (pre-[solution_name] 8) distinction between "build mode" and "buildless mode",
and provide a better way to get the best results possible, while ensuring that you are alterted (via a [solution_name] failure)
if your accuracy requirements are not met.

[solution_name] often has a choice of multiple detectors available for a given project type.
[detector_cascade], which considers detector accuracy, is the mechinism [solution_name] uses to decide which of multiple detectors to run.

Accuracy is an assessment of how complete and reliable a detector's results are. Each detector has one of two possible accuracy values: HIGH, or LOW.
A detector's accuracy value is not configurable.
You can find the accuracy for each detector in the [detector table](../components/detectors.md).

Detectors that run the project's package manager and
discover dependencies from its output are generally assigned high accuracy because the package manager is generally a reliable source of truth
about dependencies.
Detectors that parse package manager-generated lockfiles also tend to be high accuracy.
Detectors that parse human-editable files are generally assigned low accuracy due to challenges and limitations that are inherent in that approach.

Consider, for example, a Gradle project.
[solution_name] could run the Gradle Native Inspector detector
(which discovers dependencies by running the Gradle CLI), or the
Gradle Project Inspector detector (which discovers dependencies by parsing Gradle files).
If the Gradle Native Inspector succeeds, it would produce higher accuracy results than the Gradle Project Inspector detector.
However, the Gradle Native Inspector may not succeed (since, for example, it must be able to find and execute a Gradle executable),
and (depending on the user's preference) low accuracy might be better than nothing.

[solution_name] will always try the more accurate detectors first, falling back to less accurate detectors only if the more accurate
detectors fail (or can't be run). 

## Nesting rules

When detect.detector.search.depth is greater than 0,
nesting rules may prevent a detector from applying on a subdirectory of the source directory (say, src/a/b/c/d)
based on which detectors applied on any of its parent directories (src/a/b/c, src/a/b, src/a, or src).

Here are two examples of nesting rules:
1. If any GRADLE detector applied on any parent directory, no GRADLE detector will apply on the current directory.
1. If any XCODE detector applied on any parent directory, neither SWIFT detector will apply on the current directory.

Nesting rules can be disabled by setting property `detect.detector.search.continue` to true.

## Troubleshooting detector search

For more insight into the decisions [solution_name] made during detector search, generate
a diagnostic zip file (run with `-d`) and read the reports/search_detailed_report.txt file.

## Specifying accuracy requirements

You choose the the list of detector types from which you require accurate results using the `detect.accuracy.required` property.
This property accepts a list of detector types (MAVEN, GRADLE, ...).
This property defaults ALL, which means you want [solution_name] to fail if any detector type applies, but
only low accuracy results could be generated for that detector type.
When [solution_name] fails due to accuracy requirements it returns the FAILURE_ACCURACY_NOT_MET exit code.
This default produces behavior roughly similar to the default mode (detect.detector.buildless=false) prior to [solution_name] 8.

To get the best results available regardless of accuracy, set this property to NONE.
This value produces behavior roughly similar to buildless mode (detect.detector.buildless=true) prior to [solution_name] 8,
except that high accuracy results will be produced where possible (buildless mode used to prevent that).

To specify that you require accurate results from some (but not all) detector types, set property
`detect.accuracy.required` to the list of detector types from which you require accurate results.

## Evaluation of accuracy

Detect evaluates whether or not the detector results it was able to generate meet the user's accuracy requirements after
executing detectors and actual result accuracy is known. If not, it fails with the FAILURE_ACCURACY_NOT_MET exit code.
