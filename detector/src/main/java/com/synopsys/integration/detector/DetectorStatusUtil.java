package com.synopsys.integration.detector;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detector.accuracy.DetectableEvaluationResult;
import com.synopsys.integration.detector.base.DetectorResultStatusCodeLookup;
import com.synopsys.integration.detector.base.DetectorStatusCode;

public class DetectorStatusUtil {
    @Nullable
    public static DetectorStatusCode getStatusCode(DetectableEvaluationResult detectable) {
        Class resultClass = null;
        if (detectable.getApplicable() != null && !detectable.getApplicable().getPassed()) {
            resultClass = detectable.getApplicable().getClass();
        } else if (detectable.getExtractable() != null && !detectable.getExtractable().getPassed()) {
            resultClass = detectable.getExtractable().getClass();
        }
        if (resultClass != null) {
            return DetectorResultStatusCodeLookup.standardLookup.getStatusCode(resultClass);
        } else if (detectable.getExtraction() != null && !detectable.getExtraction().isSuccess()) {
            if (detectable.getExtraction().getError() instanceof ExecutableFailedException) {
                return DetectorStatusCode.EXECUTABLE_FAILED;
            } else {
                return DetectorStatusCode.EXTRACTION_FAILED;
            }
        } else {
            return DetectorStatusCode.PASSED;
        }
    }

    @NotNull
    public static String getStatusReason(DetectableEvaluationResult detectable) {
        if (detectable.getApplicable() != null && !detectable.getApplicable().getPassed()) {
            return detectable.getApplicable().toDescription();
        }
        if (detectable.getExtractable() != null && !detectable.getExtractable().getPassed()) {
            return detectable.getExtractable().toDescription();
        }
        if (detectable.getExtraction() != null && detectable.getExtraction().getResult() != Extraction.ExtractionResultType.SUCCESS) {
            if (detectable.getExtraction().getError() instanceof ExecutableFailedException) {
                ExecutableFailedException failedException = (ExecutableFailedException) detectable.getExtraction().getError();
                if (failedException.hasReturnCode()) {
                    return "Failed to execute command, returned non-zero: " + failedException.getExecutableDescription();
                } else if (failedException.getExecutableException() != null) {
                    return "Failed to execute command, " + failedException.getExecutableException().getMessage() + " : " + failedException.getExecutableDescription();
                } else {
                    return "Failed to execute command, unknown reason: " + failedException.getExecutableDescription();
                }
            } else {
                return "See logs for further explanation";
            }
        }
        return "Passed";
    }
}
