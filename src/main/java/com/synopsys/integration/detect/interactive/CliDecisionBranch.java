package com.synopsys.integration.detect.interactive;

import com.synopsys.integration.detect.configuration.DetectProperties;

public class CliDecisionBranch implements DecisionTree {
    private final boolean connectedToBlackDuck;

    public CliDecisionBranch(boolean connectedToBlackDuck) {
        this.connectedToBlackDuck = connectedToBlackDuck;
    }

    public void traverse(Interactions interactions) {
        if (connectedToBlackDuck) {
            Boolean upload = interactions.askYesOrNo("Would you like to upload CLI scan results to the Black Duck server?");
            if (!upload) {
                interactions.setProperty(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN.getProperty(), "true");
            }
        }

        Boolean customScanner = interactions.askYesOrNo("Would you like to provide a custom scanner?");
        if (customScanner) {
            Boolean downloadCustomScanner = interactions.askYesOrNo("Would you like to download the custom scanner?");
            if (downloadCustomScanner) {
                interactions.setPropertyFromQuestion(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL.getProperty(), "What is the scanner host url?");
            } else {
                interactions.setPropertyFromQuestion(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH.getProperty(), "What is the location of your offline scanner?");
            }
        }
    }
}
