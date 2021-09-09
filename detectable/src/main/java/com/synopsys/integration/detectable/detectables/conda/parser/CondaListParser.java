/*
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
    private static final String PYPI_CHANNEL = "pypi";
    private final Gson gson;
    private final ExternalIdFactory externalIdFactory;

    public CondaListParser(Gson gson, ExternalIdFactory externalIdFactory) {
        this.gson = gson;
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph parse(String listJsonText, String infoJsonText) {
        Type listType = new TypeToken<ArrayList<CondaListElement>>() {
        }.getType();
        List<CondaListElement> condaList = gson.fromJson(listJsonText, listType);
        CondaInfo condaInfo = gson.fromJson(infoJsonText, CondaInfo.class);
        String platform = condaInfo.platform;

        MutableDependencyGraph graph = new MutableMapDependencyGraph();

        for (CondaListElement condaListElement : condaList) {
            graph.addChildToRoot(condaListElementToDependency(platform, condaListElement));
        }

        return graph;
    }

    public Dependency condaListElementToDependency(String platform, CondaListElement element) {
        String name = element.name;
        String version;
        Forge forge;
        if (element.channel.equals(PYPI_CHANNEL)) {
            forge = Forge.PYPI;
            version = element.version;
        } else {
            forge = Forge.ANACONDA;
            version = String.format("%s-%s-%s", element.version, element.buildString, platform);
        }

        ExternalId externalId = externalIdFactory.createNameVersionExternalId(forge, name, version);

        return new Dependency(name, version, externalId);
    }

}
