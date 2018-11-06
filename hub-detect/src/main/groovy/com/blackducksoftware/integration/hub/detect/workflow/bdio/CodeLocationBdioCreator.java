package com.blackducksoftware.integration.hub.detect.workflow.bdio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.BdioCodeLocation;
import com.synopsys.integration.hub.bdio.SimpleBdioFactory;
import com.synopsys.integration.hub.bdio.graph.DependencyGraph;
import com.synopsys.integration.hub.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalId;
import com.synopsys.integration.util.NameVersion;

public class CodeLocationBdioCreator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectBdioWriter detectBdioWriter;
    private SimpleBdioFactory simpleBdioFactory;

    public CodeLocationBdioCreator(final DetectBdioWriter detectBdioWriter, final SimpleBdioFactory simpleBdioFactory) {
        this.detectBdioWriter = detectBdioWriter;
        this.simpleBdioFactory = simpleBdioFactory;
    }

    public List<File> createBdioFiles(File bdioOutput, final List<BdioCodeLocation> bdioCodeLocations, NameVersion projectNameVersion) throws DetectUserFriendlyException {
        final List<File> bdioFiles = new ArrayList<>();
        String projectName = projectNameVersion.getName();
        for (final BdioCodeLocation bdioCodeLocation : bdioCodeLocations) {
            String codeLocationName = bdioCodeLocation.codeLocationName;
            ExternalId externalId = bdioCodeLocation.codeLocation.getExternalId();
            DependencyGraph dependencyGraph = bdioCodeLocation.codeLocation.getDependencyGraph();

            final SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createSimpleBdioDocument(codeLocationName, projectName, externalId,  dependencyGraph);

            final File outputFile = new File(bdioOutput, bdioCodeLocation.bdioName);
            detectBdioWriter.writeBdioFile(outputFile, simpleBdioDocument);
            bdioFiles.add(outputFile);
        }

        return bdioFiles;
    }
}
