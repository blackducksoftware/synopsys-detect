package com.synopsys.integration.detect.interactive.mode;

import com.synopsys.integration.detect.configuration.DetectProperties;

public class CliInteractionTree implements InteractionTree {
    private final boolean connectedToBlackDuck;

    public CliInteractionTree(boolean connectedToBlackDuck) {
        this.connectedToBlackDuck = connectedToBlackDuck;
    }

    public void configure(InteractiveMode interactiveMode) {
        if (connectedToBlackDuck) {
            Boolean upload = interactiveMode.askYesOrNo("Would you like to upload CLI scan results to the Black Duck server?");
            if (!upload) {
                interactiveMode.setProperty(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN.getProperty(), "true");
            }
        }

        Boolean customScanner = interactiveMode.askYesOrNo("Would you like to provide a custom scanner?");
        if (customScanner) {
            Boolean downloadCustomScanner = interactiveMode.askYesOrNo("Would you like to download the custom scanner?");
            if (downloadCustomScanner) {
                interactiveMode.setPropertyFromQuestion(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL.getProperty(), "What is the scanner host url?");
            } else {
                interactiveMode.setPropertyFromQuestion(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH.getProperty(), "What is the location of your offline scanner?");
            }
        }
    }
}
