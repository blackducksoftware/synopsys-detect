# [detector_cascade] and accuracy

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







