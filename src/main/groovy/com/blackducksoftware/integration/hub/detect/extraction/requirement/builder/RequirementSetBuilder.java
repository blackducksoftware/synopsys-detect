package com.blackducksoftware.integration.hub.detect.extraction.requirement.builder;

import java.util.HashMap;
import java.util.Map;

import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;
import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContextAction;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.Requirement;

@SuppressWarnings("rawtypes")
public class RequirementSetBuilder<C extends ExtractionContext> {

    Map<Requirement, ExtractionContextAction> requirementMap = new HashMap<>();

    public RequirementSetBuilder() {

    }

    public FileRequirementBuilder requireFile(final String file) {
        return new FileRequirementBuilder<C>(this, file);
    }

    public void add(final Requirement requirement, final ExtractionContextAction action) {
        this.requirementMap.put(requirement, action);
    }

    public Map<Requirement, ExtractionContextAction> build() {
        return requirementMap;
    }

}
