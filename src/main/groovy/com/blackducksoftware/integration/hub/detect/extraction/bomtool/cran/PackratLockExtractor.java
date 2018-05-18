package com.blackducksoftware.integration.hub.detect.extraction.bomtool.cran;

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
import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.cran.parse.PackratPackager;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class PackratLockExtractor extends Extractor<PackratLockContext> {

    @Autowired
    PackratPackager packratPackager;

    @Autowired
    protected ExternalIdFactory externalIdFactory;

    @Autowired
    protected DetectFileFinder detectFileFinder;

    @Override
    public Extraction extract(final PackratLockContext context) {
        try {
            String projectName = "";
            String projectVersion = "";
            if (detectFileFinder.containsAllFiles(context.directory, "DESCRIPTION")) {
                final File descriptionFile = new File(context.directory, "DESCRIPTION");
                final List<String> descriptionText = Files.readAllLines(descriptionFile.toPath(), StandardCharsets.UTF_8);
                projectName = packratPackager.getProjectName(descriptionText);
                projectVersion = packratPackager.getVersion(descriptionText);
            }
            final List<String> packratLockText = Files.readAllLines(context.packratlock.toPath(), StandardCharsets.UTF_8);
            final DependencyGraph dependencyGraph = packratPackager.extractProjectDependencies(packratLockText);
            final ExternalId externalId = externalIdFactory.createPathExternalId(Forge.CRAN, context.directory.toString());
            final DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(BomToolType.CRAN, context.directory.toString(), externalId, dependencyGraph).build();
            return new Extraction.Builder().success(codeLocation).projectName(projectName).projectVersion(projectVersion).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
