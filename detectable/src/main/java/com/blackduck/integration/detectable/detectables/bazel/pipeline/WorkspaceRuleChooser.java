package com.blackduck.integration.detectable.detectables.bazel.pipeline;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.blackduck.integration.detectable.detectable.exception.DetectableException;
import com.blackduck.integration.detectable.detectables.bazel.WorkspaceRule;

public class WorkspaceRuleChooser {

    @NotNull
    public Set<WorkspaceRule> choose(Set<WorkspaceRule> rulesFromWorkspaceFile, Set<WorkspaceRule> rulesFromProperty)
        throws DetectableException {
        if (rulesFromProperty != null && !rulesFromProperty.isEmpty()) {
            return rulesFromProperty;
        } else if (!rulesFromWorkspaceFile.isEmpty()) {
            return rulesFromWorkspaceFile;
        } else {
            throw new DetectableException("Unable to determine Bazel workspace rule type; try setting it via the property");
        }
    }

}
