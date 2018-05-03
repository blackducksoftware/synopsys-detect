package com.blackducksoftware.integration.hub.detect.extraction.bomtool.go;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.go.vndr.VndrParser;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.google.gson.Gson;

@Component
public class GoVndrExtractor  extends Extractor<GoVndrContext> {

    @Autowired
    Gson gson;

    @Autowired
    ExternalIdFactory externalIdFactory;

    @Override
    public Extraction extract(final GoVndrContext context) {
        try {
            final File sourceDirectory = context.directory;

            final VndrParser vndrParser = new VndrParser(externalIdFactory);
            final List<String> venderConfContents = Files.readAllLines(context.vndrConfig.toPath(), StandardCharsets.UTF_8);
            final DependencyGraph dependencyGraph = vndrParser.parseVendorConf(venderConfContents);
            final ExternalId externalId = externalIdFactory.createPathExternalId(Forge.GOLANG, context.directory.toString());

            final DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(BomToolType.GO_VNDR, sourceDirectory.toString(), externalId, dependencyGraph).build();
            return new Extraction(ExtractionResult.Success, codeLocation);
        }catch (final Exception e) {
            return new Extraction(ExtractionResult.Failure, e);
        }
    }

}
