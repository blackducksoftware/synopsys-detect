package com.synopsys.integration.detectable.detectables.bazel.pipeline.step;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.util.NameVersion;

public class FinalStepTransformJsonProtoHaskellCabalLibrariesToHackage implements FinalStep {
    private static final String FORGE_NAME = "hackage";
    private static final String FORGE_SEPARATOR = "/";
    private final Forge hackageForge = new Forge(FORGE_SEPARATOR, FORGE_NAME);
    private final HaskellCabalLibraryJsonProtoParser parser;
    private final ExternalIdFactory externalIdFactory;

    public FinalStepTransformJsonProtoHaskellCabalLibrariesToHackage(HaskellCabalLibraryJsonProtoParser parser, ExternalIdFactory externalIdFactory) {
        this.parser = parser;
        this.externalIdFactory = externalIdFactory;
    }

    @Override
    public List<Dependency> finish(List<String> input) throws DetectableException {
        List<Dependency> dependencies = new ArrayList<>();
        Optional<String> jsonString = extractJsonString(input);
        if (!jsonString.isPresent()) {
            return dependencies;
        }
        List<NameVersion> dependencyDetailsList = parser.parse(jsonString.get());
        for (NameVersion dependencyDetails : dependencyDetailsList) {
            Dependency dependency = hackageCompNameVersionToDependency(dependencyDetails.getName(), dependencyDetails.getVersion());
            dependencies.add(dependency);
        }
        return dependencies;
    }

    private Optional<String> extractJsonString(List<String> input) throws DetectableException {
        if (input.size() > 1) {
            throw new DetectableException(String.format("Input size is %d; expected 1", input.size()));
        }
        if (input.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(input.get(0));
    }

    private Dependency hackageCompNameVersionToDependency(String compName, String compVersion) {
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(hackageForge, compName, compVersion);
        externalId.createBdioId(); // Validity check; throws IllegalStateException if invalid
        return new Dependency(compName, compVersion, externalId);
    }
}
