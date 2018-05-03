package com.blackducksoftware.integration.hub.detect.extraction.bomtool.go;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.go.DepPackager;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

@Component
public class GoDepExtractor extends Extractor<GoDepContext> {

    @Autowired
    DepPackager goPackager;

    @Autowired
    ExternalIdFactory externalIdFactory;

    @Override
    public Extraction extract(final GoDepContext context) {

        DependencyGraph graph = goPackager.makeDependencyGraph(context.directory.toString(), context.goDepInspector);
        if(graph == null) {
            graph = new MutableMapDependencyGraph();
        }
        final ExternalId externalId = externalIdFactory.createPathExternalId(Forge.GOLANG, context.directory.toString());
        final DetectCodeLocation detectCodeLocation = new DetectCodeLocation.Builder(BomToolType.GO_DEP, context.directory.toString(), externalId, graph).build();

        return new Extraction(ExtractionResult.Success, detectCodeLocation);

    }

}
