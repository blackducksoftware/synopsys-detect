package com.blackducksoftware.integration.hub.detect.workflow;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;

public class RequiredBomToolChecker {

    public class RequiredBomToolResult {
        public RequiredBomToolResult(final Set<BomToolGroupType> missingBomTools) {
            this.missingBomTools = missingBomTools;
        }

        public boolean wereBomToolsMissing() {
            return missingBomTools.size() > 0;
        }

        public Set<BomToolGroupType> getMissingBomTools() {
            return missingBomTools;
        }

        private final Set<BomToolGroupType> missingBomTools;
    }

    public RequiredBomToolResult checkForMissingBomTools(final String requiredBomToolString, final Set<BomToolGroupType> applicableBomTools) {
        final Set<BomToolGroupType> required = parseRequiredBomTools(requiredBomToolString);

        final Set<BomToolGroupType> missingBomTools = required.stream()
                .filter(it -> !applicableBomTools.contains(it))
                .collect(Collectors.toSet());

        return new RequiredBomToolResult(missingBomTools);
    }

    private Set<BomToolGroupType> parseRequiredBomTools(final String rawRequiredTypeString) {
        final Set<BomToolGroupType> required = new HashSet<>();
        final String[] rawRequiredTypes = rawRequiredTypeString.split(",");
        for (final String rawType : rawRequiredTypes) {
            final BomToolGroupType type = BomToolGroupType.valueOf(rawType);
            required.add(type);
        }
        return required;
    }
}
