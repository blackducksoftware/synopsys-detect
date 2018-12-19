package com.blackducksoftware.integration.hub.detect.detector.bazel;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.detector.Detector;
import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.detector.DetectorException;
import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.detector.ExtractionId;
import com.blackducksoftware.integration.hub.detect.detector.clang.ClangLinuxPackageManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.FileNotFoundDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PassedDetectorResult;
import com.synopsys.integration.exception.IntegrationException;

public class BazelDetector extends Detector {
    private static final String BAZEL_WORKSPACE_FILENAME = "WORKSPACE";
    private final BazelExtractor bazelExtractor;
    private File workspaceFile;
    private final DetectFileFinder fileFinder;
    private final ExecutableRunner executableRunner;

    public BazelDetector(final DetectorEnvironment environment, final ExecutableRunner executableRunner, final DetectFileFinder fileFinder, final BazelExtractor bazelExtractor) {
        super(environment, "Clang", DetectorType.BAZEL);
        this.fileFinder = fileFinder;
        this.executableRunner = executableRunner;
        this.bazelExtractor = bazelExtractor;
    }

    @Override
    public DetectorResult applicable() {
        workspaceFile = fileFinder.findFile(environment.getDirectory(), BAZEL_WORKSPACE_FILENAME);
        if (workspaceFile == null) {
            return new FileNotFoundDetectorResult(BAZEL_WORKSPACE_FILENAME);
        }
        return new PassedDetectorResult();
    }

    @Override
    public DetectorResult extractable() throws DetectorException {
//        try {
            // TODO run a bazel command to see if we are in a valid workspace
//        } catch (final IntegrationException e) {
//            return new ExecutableNotFoundDetectorResult("bazel");
//        }
        return new PassedDetectorResult();
    }


    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return bazelExtractor.extract(environment.getDirectory(), environment.getDepth(), extractionId);
    }
}
