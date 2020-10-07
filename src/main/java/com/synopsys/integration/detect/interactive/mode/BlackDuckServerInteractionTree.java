package com.synopsys.integration.detect.interactive.mode;

import com.synopsys.integration.detect.configuration.DetectProperties;

public class BlackDuckServerInteractionTree implements InteractionTree {
    public BlackDuckServerInteractionTree() {
    }

    public void configure(InteractiveMode interactiveMode) {
        interactiveMode.setPropertyFromQuestion(DetectProperties.BLACKDUCK_URL.getProperty(), "What is the Black Duck server url?");

        interactiveMode
            .println("You can now configure Black Duck with either an API token -OR- a username and password. The API token must already exist on the Black Duck server, but it is the preferred approach to configure your connection.");
        Boolean useApiToken = interactiveMode.askYesOrNo("Would you like to use an existing API token?");
        if (useApiToken) {
            interactiveMode.setPropertyFromQuestion(DetectProperties.BLACKDUCK_API_TOKEN.getProperty(), "What is the API token?");
        } else {
            interactiveMode.setPropertyFromQuestion(DetectProperties.BLACKDUCK_USERNAME.getProperty(), "What is the username?");

            Boolean setHubPassword = interactiveMode.askYesOrNoWithMessage("Would you like to set the password?",
                "WARNING: If you choose to save the settings, this password will be stored in plain text. You can set this password as an environment variable BLACKDUCK_HUB_PASSWORD.");
            if (setHubPassword) {
                interactiveMode.setPropertyFromSecretQuestion(DetectProperties.BLACKDUCK_PASSWORD.getProperty(), "What is the password?");
            }
        }

        Boolean useProxy = interactiveMode.askYesOrNo("Would you like to configure a proxy?");
        if (useProxy) {
            interactiveMode.setPropertyFromQuestion(DetectProperties.BLACKDUCK_PROXY_HOST.getProperty(), "What is the proxy host?");
            interactiveMode.setPropertyFromQuestion(DetectProperties.BLACKDUCK_PROXY_PORT.getProperty(), "What is the proxy port?");
            interactiveMode.setPropertyFromQuestion(DetectProperties.BLACKDUCK_PROXY_USERNAME.getProperty(), "What is the Black Duck username?");
            Boolean setHubPassword = interactiveMode.askYesOrNoWithMessage("Would you like to set the Black Duck password?",
                "WARNING: If you choose to save the settings, this password will be stored in plain text. You can set this password as an environment variable BLACKDUCK_PROXY_PASSWORD.");
            if (setHubPassword) {
                interactiveMode.setPropertyFromSecretQuestion(DetectProperties.BLACKDUCK_PROXY_PASSWORD.getProperty(), "What is the proxy password?");
            }
            Boolean useNtlmProxy = interactiveMode.askYesOrNo("Do you use a ntlm proxy?");
            if (useNtlmProxy) {
                interactiveMode.setPropertyFromQuestion(DetectProperties.BLACKDUCK_PROXY_NTLM_DOMAIN.getProperty(), "What is the ntlm proxy domain?");
                interactiveMode.setPropertyFromQuestion(DetectProperties.BLACKDUCK_PROXY_NTLM_WORKSTATION.getProperty(), "What is the ntlm proxy workstation?");
            }
        }

        Boolean trustCert = interactiveMode.askYesOrNo("Would you like to automatically trust certificates?");
        if (trustCert) {
            interactiveMode.setProperty(DetectProperties.BLACKDUCK_TRUST_CERT.getProperty(), "true");
        }
    }

}
