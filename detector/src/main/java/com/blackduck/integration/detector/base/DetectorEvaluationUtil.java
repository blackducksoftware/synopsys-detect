package com.blackduck.integration.detector.base;

import java.util.ArrayList;
import java.util.List;

import com.blackduck.integration.detector.accuracy.directory.DirectoryEvaluation;

public class DetectorEvaluationUtil {
    public static List<DirectoryEvaluation> asFlatList(DirectoryEvaluation node) {
        List<DirectoryEvaluation> allChildren = new ArrayList<>();
        allChildren.add(node);
        for (DirectoryEvaluation child : node.getChildren()) {
            allChildren.addAll(DetectorEvaluationUtil.asFlatList(child));
        }
        return allChildren;
    }
}
