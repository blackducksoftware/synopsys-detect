package com.blackducksoftware.integration.hub.detect.detector.go;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocationType;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.google.gson.Gson;
import com.synopsys.integration.hub.bdio.graph.DependencyGraph;
import com.synopsys.integration.hub.bdio.model.Forge;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalId;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class GoVendorExtractor {
    private final Gson gson;
    private final ExternalIdFactory externalIdFactory;

    public GoVendorExtractor(final Gson gson, final ExternalIdFactory externalIdFactory) {
        this.gson = gson;
        this.externalIdFactory = externalIdFactory;
    }

    public Extraction extract(final File directory, final File vendorJsonFile) {
        try {
            final GoVendorJsonParser vendorJsonParser = new GoVendorJsonParser(externalIdFactory);
            final String vendorJsonContents = FileUtils.readFileToString(vendorJsonFile, StandardCharsets.UTF_8);
            final DependencyGraph dependencyGraph = vendorJsonParser.parseVendorJson(gson, vendorJsonContents);

            final ExternalId externalId = externalIdFactory.createPathExternalId(Forge.GOLANG, directory.toString());

            final DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(DetectCodeLocationType.GO_VENDOR, directory.toString(), externalId, dependencyGraph).build();
            return new Extraction.Builder().success(codeLocation).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
