/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.conda.parser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.conda.model.CondaInfo;
import com.synopsys.integration.detectable.detectables.conda.model.CondaListElement;

public class CondaListParser {
    private final Gson gson;
    private final ExternalIdFactory externalIdFactory;

    public CondaListParser(final Gson gson, final ExternalIdFactory externalIdFactory) {
        this.gson = gson;
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph parse(final String listJsonText, final String infoJsonText) {
        final Type listType = new TypeToken<ArrayList<CondaListElement>>() {
        }.getType();
        final List<CondaListElement> condaList = gson.fromJson(listJsonText, listType);
        final CondaInfo condaInfo = gson.fromJson(infoJsonText, CondaInfo.class);
        final String platform = condaInfo.platform;

        final MutableDependencyGraph graph = new MutableMapDependencyGraph();

        for (final CondaListElement condaListElement : condaList) {
            graph.addChildToRoot(condaListElementToDependency(platform, condaListElement));
        }

        return graph;
    }

    public Dependency condaListElementToDependency(final String platform, final CondaListElement element) {
        final String name = element.name;
        final String version = String.format("%s-%s-%s", element.version, element.buildString, platform);
        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.ANACONDA, name, version);

        return new Dependency(name, version, externalId);
    }

}
