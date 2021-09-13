/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.conda.parser;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.conda.model.CondaListElement;

public class CondaDependencyCreator {

    private final ExternalIdFactory externalIdFactory;
    private CondaForgeMap forgeMap;
    private CondaVersionMap versionMap;

    public CondaDependencyCreator(ExternalIdFactory externalIdFactory, CondaForgeMap forgeMap, CondaVersionMap versionMap) {
        this.externalIdFactory = externalIdFactory;
        this.forgeMap = forgeMap;
        this.versionMap = versionMap;
    }

    public Dependency createFromCondaListElement(CondaListElement element, String platform) {
        String name = element.name;
        String version = versionMap.getVersionIfElse(element, platform);
        Forge forge = forgeMap.getForge(element.channel);

        ExternalId externalId = externalIdFactory.createNameVersionExternalId(forge, name, version);

        return new Dependency(name, version, externalId);
    }
}
