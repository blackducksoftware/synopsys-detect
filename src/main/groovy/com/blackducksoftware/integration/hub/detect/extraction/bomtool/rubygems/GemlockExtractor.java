package com.blackducksoftware.integration.hub.detect.extraction.bomtool.rubygems;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.rubygems.parse.RubygemsNodePackager;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

@Component
public class GemlockExtractor extends Extractor<GemlockContext> {

    @Autowired
    RubygemsNodePackager rubygemsNodePackager;

    @Autowired
    ExternalIdFactory externalIdFactory;

    @Override
    public Extraction extract(final GemlockContext context) {
        try {
            final List<String> gemlockText = Files.readAllLines(context.gemlock.toPath(), StandardCharsets.UTF_8);

            final DependencyGraph dependencyGraph = rubygemsNodePackager.extractProjectDependencies(gemlockText);
            final ExternalId externalId = externalIdFactory.createPathExternalId(Forge.RUBYGEMS, context.directory.toString());

            final DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(BomToolType.RUBYGEMS, context.directory.toString(), externalId, dependencyGraph).build();
            return new Extraction.Builder().success(codeLocation).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}