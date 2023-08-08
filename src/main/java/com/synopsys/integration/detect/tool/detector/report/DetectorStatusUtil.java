package com.synopsys.integration.detect.tool.detector.report;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detect.workflow.report.ExceptionUtil;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detector.base.DetectorResultStatusCodeLookup;
import com.synopsys.integration.detector.base.DetectorStatusCode;
import com.synopsys.integration.detector.result.DetectorResult;

public class DetectorStatusUtil {
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
