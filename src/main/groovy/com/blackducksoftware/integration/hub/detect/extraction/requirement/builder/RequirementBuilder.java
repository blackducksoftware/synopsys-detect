package com.blackducksoftware.integration.hub.detect.extraction.requirement.builder;

import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;
import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContextAction;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.Requirement;

@SuppressWarnings("rawtypes")
public abstract class RequirementBuilder<C extends ExtractionContext, V> {

    RequirementSetBuilder parent;
    ExtractionContextAction<C, V> action;

    public RequirementBuilder(final RequirementSetBuilder set) {
        this.parent = set;
    }

    public RequirementSetBuilder notRequiredInContext() {
        this.injectInContext((o1, o2) -> {});
        return this.parent;
    }

    public RequirementSetBuilder injectInContext(final ExtractionContextAction<C, V> action) {
        this.action = action;
        this.parent.add(build(), action);
        return this.parent;
    }

    public abstract Requirement build();

}
