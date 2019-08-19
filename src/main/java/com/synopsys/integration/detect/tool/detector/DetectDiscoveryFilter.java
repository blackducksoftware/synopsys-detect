package com.synopsys.integration.detect.tool.detector;

import org.codehaus.plexus.util.StringUtils;

import com.synopsys.integration.detect.kotlin.nameversion.DetectorNameVersionHandler;
import com.synopsys.integration.detect.kotlin.nameversion.DetectorProjectInfo;
import com.synopsys.integration.detect.kotlin.nameversion.DetectorProjectInfoMetadata;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.evaluation.DiscoveryFilter;
import com.synopsys.integration.util.NameVersion;

public class DetectDiscoveryFilter implements DiscoveryFilter {
    private DetectorNameVersionHandler detectorNameVersionHandler;

    public DetectDiscoveryFilter(EventSystem eventSystem, final DetectorNameVersionHandler detectorNameVersionHandler) {
        this.detectorNameVersionHandler = detectorNameVersionHandler;

        eventSystem.registerListener(Event.DiscoveryEnded, this::discoveryEnded);
    }

    public void discoveryEnded(DetectorEvaluation detectorEvaluation) {
        DetectorProjectInfo info = toProjectInfo(detectorEvaluation);
        if (info != null) {
            detectorNameVersionHandler.accept(info);
        }
    }

    @Override
    public boolean shouldDiscover(final DetectorEvaluation detectorEvaluation) {
        return detectorNameVersionHandler.willAccept(toMetadataProjectInfo(detectorEvaluation));
    }

    private DetectorProjectInfo toProjectInfo(DetectorEvaluation detectorEvaluation) {
        if (detectorEvaluation.wasDiscoverySuccessful()) {
            String projectName = detectorEvaluation.getDiscovery().getProjectName();
            String projectVersion = detectorEvaluation.getDiscovery().getProjectVersion();

            if (StringUtils.isNotBlank(projectName)) {
                NameVersion nameVersion = new NameVersion(projectName, projectVersion);
                return new DetectorProjectInfo(detectorEvaluation.getDetectorRule().getDetectorType(), detectorEvaluation.getSearchEnvironment().getDepth(), nameVersion);
            }
        }
        return null;
    }

    private DetectorProjectInfoMetadata toMetadataProjectInfo(DetectorEvaluation detectorEvaluation) {
        return new DetectorProjectInfoMetadata(detectorEvaluation.getDetectorRule().getDetectorType(), detectorEvaluation.getSearchEnvironment().getDepth());
    }
}
