package com.synopsys.integration.detector.base;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detector.accuracy.DetectorEvaluation;
import com.synopsys.integration.detector.accuracy.DetectorRuleEvaluation;

public class DetectorEvaluationUtil {
    public static List<DetectorRuleEvaluation> allDescendentFound(DetectorEvaluation evaluation) {
        List<DetectorRuleEvaluation> types = new ArrayList<>(evaluation.getFoundDetectorRuleEvaluations());
        for (DetectorEvaluation child : evaluation.getChildren()) {
            types.addAll(allDescendentFound(child));
        }
        return types;
    }

    public static List<DetectorEvaluation> asFlatList(DetectorEvaluation node) {
        List<DetectorEvaluation> allChildren = new ArrayList<>();
        allChildren.add(node);
        for (DetectorEvaluation child : node.getChildren()) {
            allChildren.addAll(DetectorEvaluationUtil.asFlatList(child));
        }
        return allChildren;
    }

    @Nullable
    public DetectorStatusCode getStatusCode(Extraction extraction) {
        //return DetectorResultStatusCodeLookup.standardLookup.getStatusCode(resultClass);
        if (!extraction.isSuccess()) {
            if (extraction.getError() instanceof ExecutableFailedException) {
                return DetectorStatusCode.EXECUTABLE_FAILED;
            } else {
                return DetectorStatusCode.EXTRACTION_FAILED;
            }
        } else {
            return DetectorStatusCode.PASSED;
        }
    }

    @NotNull
    public String getStatusReason(Extraction extraction) {
        if (extraction.getResult() != Extraction.ExtractionResultType.SUCCESS) {
            if (extraction.getError() instanceof ExecutableFailedException) {
                ExecutableFailedException failedException = (ExecutableFailedException) extraction.getError();
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
