package com.blackduck.integration.detect.tool.detector.report;

import com.blackduck.integration.detect.workflow.report.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import com.blackduck.integration.detectable.detectable.executable.ExecutableFailedException;
import com.blackduck.integration.detectable.detectable.result.DetectableResult;
import com.blackduck.integration.detectable.extraction.Extraction;
import com.blackduck.integration.detector.base.DetectorResultStatusCodeLookup;
import com.blackduck.integration.detector.base.DetectorStatusCode;
import com.blackduck.integration.detector.result.DetectorResult;

public class DetectorStatusUtil {
    private static final int SUBPROCESS_EXIT_CODE_OOM = 137;

    @Nullable
    public static DetectorStatusCode getStatusCode(DetectableResult detectableResult) {
        return DetectorResultStatusCodeLookup.standardLookup.getStatusCode(detectableResult.getClass());
    }

    @Nullable
    public static DetectorStatusCode getStatusCode(DetectorResult detectableResult) {
        return DetectorResultStatusCodeLookup.standardLookup.getStatusCode(detectableResult.getClass());
    }

    @Nullable
    public static DetectorStatusCode getFailedStatusCode(Extraction extraction) {
        if (extraction.getError() instanceof ExecutableFailedException) {
            ExecutableFailedException failedException = (ExecutableFailedException) extraction.getError();
            if (failedException.getReturnCode() == SUBPROCESS_EXIT_CODE_OOM) {
                return DetectorStatusCode.EXECUTABLE_TERMINATED_LIKELY_OUT_OF_MEMORY;
            }

            return DetectorStatusCode.EXECUTABLE_FAILED;
        } else {
            return DetectorStatusCode.EXTRACTION_FAILED;
        }
    }

    @Nullable
    public static String getFailedStatusReason(Extraction extraction) {
        if (extraction.getError() instanceof ExecutableFailedException) {
            ExecutableFailedException failedException = (ExecutableFailedException) extraction.getError();
            if (failedException.hasReturnCode()) {
                if (failedException.getReturnCode() == SUBPROCESS_EXIT_CODE_OOM) {
                    return DetectorStatusCode.EXECUTABLE_TERMINATED_LIKELY_OUT_OF_MEMORY.getDescription();
                }

                return String.format(
                    "Failed to execute command, returned non-zero (%d): %s",
                    failedException.getReturnCode(),
                    failedException.getExecutableDescription()
                );
            } else if (failedException.getExecutableException() != null) {
                return "Failed to execute command, " + failedException.getExecutableException().getMessage() + " : " + failedException.getExecutableDescription();
            } else {
                return "Failed to execute command, unknown reason: " + failedException.getExecutableDescription();
            }
        } else if (extraction.getError() != null) {
            return ExceptionUtil.oneSentenceDescription(extraction.getError());
        } else if (StringUtils.isNotBlank(extraction.getDescription())) {
            return extraction.getDescription();
        } else {
            return "See logs for further explanation";
        }
    }

}
