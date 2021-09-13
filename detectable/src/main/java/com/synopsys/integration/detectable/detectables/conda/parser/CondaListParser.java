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
import com.synopsys.integration.detectable.detectables.conda.model.CondaInfo;
import com.synopsys.integration.detectable.detectables.conda.model.CondaListElement;

public class CondaListParser {
    private final Gson gson;
    private final CondaDependencyCreator dependencyCreator;

    public CondaListParser(Gson gson, CondaDependencyCreator dependencyCreator) {
        this.gson = gson;
        this.dependencyCreator = dependencyCreator;
    }

    public DependencyGraph parse(String listJsonText, String infoJsonText) {
        Type listType = new TypeToken<ArrayList<CondaListElement>>() {
        }.getType();
        List<CondaListElement> condaList = gson.fromJson(listJsonText, listType);
        CondaInfo condaInfo = gson.fromJson(infoJsonText, CondaInfo.class);
        String platform = condaInfo.platform;

        MutableDependencyGraph graph = new MutableMapDependencyGraph();

        for (CondaListElement condaListElement : condaList) {
            graph.addChildToRoot(dependencyCreator.createFromCondaListElement(condaListElement, platform));
        }

        return graph;
    }

}
