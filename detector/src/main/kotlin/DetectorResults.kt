package com.synopsys.integration.detector.result

open class DetectorResult(val passed: Boolean, val description: String)

open class FailedDetectorResult(description: String) : DetectorResult(false, description)

open class PassedDetectorResult(description: String = "Passed.") : DetectorResult(true, description)

class ExcludedDetectorResult : FailedDetectorResult("Detector type was excluded.")

class ForcedNestedPassedDetectorResult : PassedDetectorResult("Forced to pass because nested forced by user.")

class MaxDepthExceededDetectorResult(depth: Int, maxDepth: Int) : FailedDetectorResult("Max depth of $maxDepth exceeded by $depth")

class NotNestableDetectorResult : FailedDetectorResult("Not nestable and a detector already applied in parent directory.")

class NotSelfNestableDetectorResult : FailedDetectorResult("Nestable but this detector already applied in a parent directory.")

class YieldedDetectorResult(yieldedTo: Set<String>) : FailedDetectorResult("Yielded to detectors: ${yieldedTo.joinToString(",")}")
