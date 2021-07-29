/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.interactive;

import static com.synopsys.integration.detect.configuration.DetectProperties.BLACKDUCK_API_TOKEN;
import static com.synopsys.integration.detect.configuration.DetectProperties.BLACKDUCK_PROXY_HOST;
import static com.synopsys.integration.detect.configuration.DetectProperties.BLACKDUCK_PROXY_NTLM_DOMAIN;
import static com.synopsys.integration.detect.configuration.DetectProperties.BLACKDUCK_PROXY_NTLM_WORKSTATION;
import static com.synopsys.integration.detect.configuration.DetectProperties.BLACKDUCK_PROXY_PASSWORD;
import static com.synopsys.integration.detect.configuration.DetectProperties.BLACKDUCK_PROXY_PORT;
import static com.synopsys.integration.detect.configuration.DetectProperties.BLACKDUCK_PROXY_USERNAME;
import static com.synopsys.integration.detect.configuration.DetectProperties.BLACKDUCK_TRUST_CERT;
import static com.synopsys.integration.detect.configuration.DetectProperties.BLACKDUCK_URL;

public class BlackDuckServerDecisionBranch implements DecisionTree {
    public static final String SHOULD_USE_API_TOKEN = "Would you like to use an existing API token?";
    public static final String SET_API_TOKEN = "What is the API token?";
    public static final String SET_USERNAME = "What is the username?";
    public static final String SHOULD_SET_PASSWORD = "Would you like to set the password?";
    public static final String SET_PASSWORD = "What is the password?";
    public static final String SHOULD_CONFIGURE_PROXY = "Would you like to configure a proxy?";
    public static final String SET_PROXY_HOST = "What is the proxy host?";
    public static final String SET_PROXY_PORT = "What is the proxy port?";
    public static final String SET_PROXY_USERNAME = "What is the proxy username?";
    public static final String SHOULD_SET_PROXY_PASSWORD = "Would you like to set the proxy password?";
    public static final String SET_PROXY_PASSWORD = "What is the proxy password?";
    public static final String SHOULD_SET_PROXY_NTLM = "Do you use a ntlm proxy?";
    public static final String SET_PROXY_NTLM_DOMAIN = "What is the ntlm proxy domain?";
    public static final String SET_PROXY_NTLM_WORKSTATION = "What is the ntlm proxy workstation?";
    public static final String SHOULD_TRUST_CERTS = "Would you like to automatically trust certificates?";
    public static final String SET_BLACKDUCK_SERVER_URL = "What is the Black Duck server url?";
    public static final String WARNING_PROXY_PASSWORD =
        "WARNING: If you choose to save the settings, this password will be stored in plain text. You can set this password as an environment variable " + BLACKDUCK_PROXY_PASSWORD.getProperty().getKeyAsEnvironmentVariable() + ".";

    @Override
    public void traverse(InteractivePropertySourceBuilder propertySourceBuilder, InteractiveWriter writer) {
        propertySourceBuilder.setPropertyFromQuestion(BLACKDUCK_URL, SET_BLACKDUCK_SERVER_URL);

        propertySourceBuilder.setPropertyFromQuestion(BLACKDUCK_API_TOKEN, SET_API_TOKEN);

        Boolean useProxy = writer.askYesOrNo(SHOULD_CONFIGURE_PROXY);
        if (useProxy) {
            propertySourceBuilder.setPropertyFromQuestion(BLACKDUCK_PROXY_HOST, SET_PROXY_HOST);
            propertySourceBuilder.setPropertyFromQuestion(BLACKDUCK_PROXY_PORT, SET_PROXY_PORT);
            propertySourceBuilder.setPropertyFromQuestion(BLACKDUCK_PROXY_USERNAME, SET_PROXY_USERNAME);
            Boolean setHubPassword = writer.askYesOrNoWithMessage(SHOULD_SET_PROXY_PASSWORD, WARNING_PROXY_PASSWORD);
            if (setHubPassword) {
                propertySourceBuilder.setPropertyFromSecretQuestion(BLACKDUCK_PROXY_PASSWORD, SET_PROXY_PASSWORD);
            }
            Boolean useNtlmProxy = writer.askYesOrNo(SHOULD_SET_PROXY_NTLM);
            if (useNtlmProxy) {
                propertySourceBuilder.setPropertyFromQuestion(BLACKDUCK_PROXY_NTLM_DOMAIN, SET_PROXY_NTLM_DOMAIN);
                propertySourceBuilder.setPropertyFromQuestion(BLACKDUCK_PROXY_NTLM_WORKSTATION, SET_PROXY_NTLM_WORKSTATION);
            }
        }

        Boolean trustCert = writer.askYesOrNo(SHOULD_TRUST_CERTS);
        if (trustCert) {
            propertySourceBuilder.setProperty(BLACKDUCK_TRUST_CERT, Boolean.TRUE.toString());
        }
    }

}
