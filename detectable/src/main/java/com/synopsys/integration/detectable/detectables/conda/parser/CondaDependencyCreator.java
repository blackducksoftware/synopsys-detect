package com.synopsys.integration.detectable.detectables.conda.parser;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.conda.model.CondaListElement;

public class CondaDependencyCreator {
    private static final String PYPI_CHANNEL = "pypi";

    private final ExternalIdFactory externalIdFactory;

    public CondaDependencyCreator(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public Dependency createFromCondaListElement(CondaListElement element, String platform) {
        if (PYPI_CHANNEL.equals(element.channel)) {
            return getPypiDependency(element);
        } else {
            return getAnacondaDependency(element, platform);
        }
    }

    private Dependency getAnacondaDependency(CondaListElement element, String platform) {
        String name = element.name;
        String version = String.format("%s-%s-%s", element.version, element.buildString, platform);
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.ANACONDA, name, version);
        return new Dependency(name, version, externalId);
    }

    private Dependency getPypiDependency(CondaListElement element) {
        String name = element.name;
        String version = element.version;
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.PYPI, name, version);
        return new Dependency(name, version, externalId);
    }
}
