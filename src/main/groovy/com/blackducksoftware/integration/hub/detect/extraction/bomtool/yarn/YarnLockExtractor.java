package com.blackducksoftware.integration.hub.detect.extraction.bomtool.yarn;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.yarn.parse.YarnPackager;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

public class YarnLockExtractor extends Extractor<YarnLockContext> {

    @Autowired
    YarnPackager yarnPackager;

    @Autowired
    ExternalIdFactory externalIdFactory;

    @Override
    public Extraction extract(final YarnLockContext context) {
        try {
            final List<String> yarnLockText = Files.readAllLines(context.yarnlock.toPath(), StandardCharsets.UTF_8);
            final DependencyGraph dependencyGraph = yarnPackager.parse(yarnLockText);
            final ExternalId externalId = externalIdFactory.createPathExternalId(Forge.NPM, context.directory.getCanonicalPath());
            final DetectCodeLocation detectCodeLocation = new DetectCodeLocation.Builder(BomToolType.YARN, context.directory.getCanonicalPath(), externalId, dependencyGraph).build();

            return new Extraction.Builder().success(detectCodeLocation).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}