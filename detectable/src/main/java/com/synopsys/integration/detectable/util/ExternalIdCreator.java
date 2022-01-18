package com.synopsys.integration.detectable.util;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class ExternalIdCreator {
    public static ExternalIdFactory sharedFactory = new ExternalIdFactory();

    public static ExternalId path(final Forge forge, final String path) {
        return sharedFactory.createPathExternalId(forge, path);
    }

    public static ExternalId moduleNames(final Forge forge, final String... moduleNames) {
        return sharedFactory.createModuleNamesExternalId(forge, moduleNames);
    }

    public static ExternalId nameVersion(final Forge forge, final String name, final String version) {
        return sharedFactory.createNameVersionExternalId(forge, name, version);
    }

    public static ExternalId nameVersion(final Forge forge, final String name) {
        return sharedFactory.createNameVersionExternalId(forge, name);
    }

    public static ExternalId yocto(final String layer, final String name, final String version) {
        return sharedFactory.createYoctoExternalId(layer, name, version);
    }

    public static ExternalId yocto(final String layer, final String name) {
        return sharedFactory.createYoctoExternalId(layer, name);
    }

    public static ExternalId maven(final String group, final String name, final String version) {
        return sharedFactory.createMavenExternalId(group, name, version);
    }

    public static ExternalId maven(final String group, final String name) {
        return sharedFactory.createMavenExternalId(group, name);
    }

    public static ExternalId architecture(final Forge forge, final String name, final String version, final String architecture) {
        return sharedFactory.createArchitectureExternalId(forge, name, version, architecture);
    }

    public static ExternalId architecture(final Forge forge, final String name, final String architecture) {
        return sharedFactory.createArchitectureExternalId(forge, name, architecture);
    }
}
