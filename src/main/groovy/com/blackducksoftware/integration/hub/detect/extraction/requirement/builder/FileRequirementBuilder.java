package com.blackducksoftware.integration.hub.detect.extraction.requirement.builder;

import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.FileRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.Requirement;

@SuppressWarnings("rawtypes")
public class FileRequirementBuilder<C extends ExtractionContext> extends RequirementBuilder<C, FileRequirement> {
    String filename;

    public FileRequirementBuilder(final RequirementSetBuilder set, final String filename) {
        super(set);
        this.filename = filename;
    }

    @Override
    public Requirement build() {
        final FileRequirement it = new FileRequirement();
        it.filename = filename;
        return it;
    }
}
