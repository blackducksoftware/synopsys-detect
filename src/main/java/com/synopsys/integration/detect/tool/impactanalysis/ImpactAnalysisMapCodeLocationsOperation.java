package com.synopsys.integration.detect.tool.impactanalysis;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.synopsys.integration.blackduck.api.core.ResourceMetadata;
import com.synopsys.integration.blackduck.api.generated.view.CodeLocationView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectView;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisBatchOutput;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisOutput;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisUploadView;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.HttpUrl;

public class ImpactAnalysisMapCodeLocationsOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final BlackDuckApiClient blackDuckService;

    public ImpactAnalysisMapCodeLocationsOperation(BlackDuckApiClient blackDuckService) {
        this.blackDuckService = blackDuckService;
    }

    // TODO: Create a code location mapping service generic enough for all tools.
    public void mapCodeLocations(Path impactAnalysisPath, CodeLocationCreationData<ImpactAnalysisBatchOutput> codeLocationCreationData, ProjectVersionWrapper projectVersionWrapper)
        throws IntegrationException {
        for (ImpactAnalysisOutput output : codeLocationCreationData.getOutput().getOutputs()) {
            ImpactAnalysisUploadView impactAnalysisUploadView = output.getImpactAnalysisUploadView();
            ProjectView projectView = projectVersionWrapper.getProjectView();
            ProjectVersionView projectVersionView = projectVersionWrapper.getProjectVersionView();
            HttpUrl projectVersionUrl = projectVersionView.getHref();
            HttpUrl codeLocationUrl = impactAnalysisUploadView.getFirstLink(ImpactAnalysisUploadView.CODE_LOCATION_LINK);

            logger.info(String.format("Mapping code location to project \"%s\" version \"%s\".", projectView.getName(), projectVersionView.getVersionName()));
            mapCodeLocation(projectVersionUrl, codeLocationUrl);
            logger.info("Successfully mapped code location.");
        }
    }

    // TODO: Use the method provided in blackduck-common:49.2.0
    private void mapCodeLocation(HttpUrl projectVersionUrl, HttpUrl codeLocationUrl) throws IntegrationException {
        // Retrieving a Code Location with just the Project Code Scanner role is not possible so we must construct it ourselves.
        CodeLocationView codeLocationView = new CodeLocationView();

        ResourceMetadata resourceMetadata = new ResourceMetadata();
        resourceMetadata.setHref(codeLocationUrl);
        codeLocationView.setMeta(resourceMetadata);

        NullNode pathJsonNode = new JsonNodeFactory(false).nullNode();
        codeLocationView.setPatch(pathJsonNode);

        codeLocationView.setMappedProjectVersion(projectVersionUrl.string());
        blackDuckService.put(codeLocationView);
    }
}
