package com.blackducksoftware.integration.hub.detect.bomtool.conda;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Component
public class CondaListParser {
    @Autowired
    private Gson gson;

    @Autowired
    private ExternalIdFactory externalIdFactory;

    public DependencyGraph parse(String listJsonText, String infoJsonText) {
        final Type listType = new TypeToken<ArrayList<CondaListElement>>() {}.getType();
        final List<CondaListElement> condaList = gson.fromJson(listJsonText, listType);
        final CondaInfo condaInfo = gson.fromJson(infoJsonText, CondaInfo.class);
        final String platform = condaInfo.getPlatform();

        MutableDependencyGraph graph = new MutableMapDependencyGraph();

        for (CondaListElement condaListElement : condaList) {
            graph.addChildToRoot(condaListElementToDependency(platform, condaListElement));
        }

        return graph;
    }

    public Dependency condaListElementToDependency(String platform, CondaListElement element) {
        String name = element.getName();
        String version = String.format("%s-%s-%s",element.getVersion(),element.getBuildString(),platform);
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.ANACONDA, name, version);

        return new Dependency(name, version, externalId);
    }

}
