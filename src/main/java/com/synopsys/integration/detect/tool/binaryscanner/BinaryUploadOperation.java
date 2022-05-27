package com.synopsys.integration.detect.tool.binaryscanner;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.Result;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScan;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanBatch;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanBatchOutput;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanOutput;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanUploadService;
import com.synopsys.integration.blackduck.exception.BlackDuckIntegrationException;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class BinaryUploadOperation {
    private static final String STATUS_KEY = "BINARY_SCAN";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CodeLocationNameManager codeLocationNameManager;
    private final BinaryScanOptions binaryScanOptions;
    private final StatusEventPublisher statusEventPublisher;

    public BinaryUploadOperation(StatusEventPublisher statusEventPublisher, CodeLocationNameManager codeLocationNameManager, BinaryScanOptions binaryScanOptions) {
        this.codeLocationNameManager = codeLocationNameManager;
        this.binaryScanOptions = binaryScanOptions;
        this.statusEventPublisher = statusEventPublisher;
    }

    public CodeLocationCreationData<BinaryScanBatchOutput> uploadBinaryScanFile(
        File binaryScanFile,
        BinaryScanUploadService binaryScanUploadService,
        NameVersion projectNameVersion
    )
        throws DetectUserFriendlyException {
        String codeLocationName = codeLocationNameManager.createBinaryScanCodeLocationName(
            binaryScanFile,
            projectNameVersion.getName(),
            projectNameVersion.getVersion()
        );
        try {
            logger.info("Preparing to upload binary scan file: " + binaryScanFile.getAbsolutePath());
            BinaryScan binaryScan = new BinaryScan(binaryScanFile, projectNameVersion.getName(), projectNameVersion.getVersion(), codeLocationName);
            BinaryScanBatch binaryScanBatch = new BinaryScanBatch(binaryScan);
            CodeLocationCreationData<BinaryScanBatchOutput> codeLocationCreationData = binaryScanUploadService.uploadBinaryScan(binaryScanBatch);

            BinaryScanBatchOutput binaryScanBatchOutput = codeLocationCreationData.getOutput();
            // The throwExceptionForError() in BinaryScanBatchOutput has a bug, so doing that work here
            throwExceptionForError(binaryScanBatchOutput);

            logger.info("Successfully uploaded binary scan file: " + binaryScanFile.getAbsolutePath());
            statusEventPublisher.publishStatusSummary(new Status(STATUS_KEY, StatusType.SUCCESS));
            return codeLocationCreationData;
        } catch (IntegrationException e) {
            statusEventPublisher.publishStatusSummary(new Status(STATUS_KEY, StatusType.FAILURE));
            throw new DetectUserFriendlyException("Failed to upload binary scan file.", e, ExitCodeType.FAILURE_BLACKDUCK_CONNECTIVITY);
        }
    }

    // BinaryScanBatchOutput used to do this, but our understanding of what needs to happen has been
    // changing rapidly. Once we're confident we know what it should do, it should presumably move back there.
    private void throwExceptionForError(BinaryScanBatchOutput binaryScanBatchOutput) throws BlackDuckIntegrationException {
        for (BinaryScanOutput binaryScanOutput : binaryScanBatchOutput) {
            if (binaryScanOutput.getResult() == Result.FAILURE) {
                // Black Duck responses are single-line message (loggable as-is), but nginx Bad Gateway responses are
                // multi-line html with the message embedded (that mess up the log).
                // cleanResponse() attempts to produce something reasonable to log in either case
                String cleanedBlackDuckResponse = cleanResponse(binaryScanOutput.getResponse());
                String uploadErrorMessage = String.format(
                    "Error when uploading binary scan: %s (Black Duck response: %s)",
                    binaryScanOutput.getErrorMessage().orElse(binaryScanOutput.getStatusMessage()),
                    cleanedBlackDuckResponse
                );
                logger.error(uploadErrorMessage);
                throw new BlackDuckIntegrationException(uploadErrorMessage);
            }
        }
    }

    private String cleanResponse(String response) {
        if (StringUtils.isBlank(response)) {
            return "";
        }
        String lengthLimitedResponse = StringUtils.substring(response, 0, 200);
        if (!lengthLimitedResponse.equals(response)) {
            lengthLimitedResponse = lengthLimitedResponse + "...";
        }
        String[] searchList = { "\n", "\r" };
        String[] replacementList = { " ", "" };
        return StringUtils.replaceEachRepeatedly(lengthLimitedResponse, searchList, replacementList);
    }
}
