/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.interactive;

import com.synopsys.integration.detect.configuration.DetectProperties;

public class BlackDuckServerDecisionBranch implements DecisionTree {
    public void traverse(Interactions interactions) {
        interactions.setPropertyFromQuestion(DetectProperties.BLACKDUCK_URL.getProperty(), "What is the Black Duck server url?");

        interactions
            .println("You can now configure Black Duck with either an API token -OR- a username and password. The API token must already exist on the Black Duck server, but it is the preferred approach to configure your connection.");
        Boolean useApiToken = interactions.askYesOrNo("Would you like to use an existing API token?");
        if (useApiToken) {
            interactions.setPropertyFromQuestion(DetectProperties.BLACKDUCK_API_TOKEN.getProperty(), "What is the API token?");
        } else {
            interactions.setPropertyFromQuestion(DetectProperties.BLACKDUCK_USERNAME.getProperty(), "What is the username?");

            Boolean setHubPassword = interactions.askYesOrNoWithMessage("Would you like to set the password?",
                "WARNING: If you choose to save the settings, this password will be stored in plain text. You can set this password as an environment variable BLACKDUCK_HUB_PASSWORD.");
            if (setHubPassword) {
                interactions.setPropertyFromSecretQuestion(DetectProperties.BLACKDUCK_PASSWORD.getProperty(), "What is the password?");
            }
        }

        Boolean useProxy = interactions.askYesOrNo("Would you like to configure a proxy?");
        if (useProxy) {
            interactions.setPropertyFromQuestion(DetectProperties.BLACKDUCK_PROXY_HOST.getProperty(), "What is the proxy host?");
            interactions.setPropertyFromQuestion(DetectProperties.BLACKDUCK_PROXY_PORT.getProperty(), "What is the proxy port?");
            interactions.setPropertyFromQuestion(DetectProperties.BLACKDUCK_PROXY_USERNAME.getProperty(), "What is the Black Duck username?");
            Boolean setHubPassword = interactions.askYesOrNoWithMessage("Would you like to set the Black Duck password?",
                "WARNING: If you choose to save the settings, this password will be stored in plain text. You can set this password as an environment variable BLACKDUCK_PROXY_PASSWORD.");
            if (setHubPassword) {
                interactions.setPropertyFromSecretQuestion(DetectProperties.BLACKDUCK_PROXY_PASSWORD.getProperty(), "What is the proxy password?");
            }
            Boolean useNtlmProxy = interactions.askYesOrNo("Do you use a ntlm proxy?");
            if (useNtlmProxy) {
                interactions.setPropertyFromQuestion(DetectProperties.BLACKDUCK_PROXY_NTLM_DOMAIN.getProperty(), "What is the ntlm proxy domain?");
                interactions.setPropertyFromQuestion(DetectProperties.BLACKDUCK_PROXY_NTLM_WORKSTATION.getProperty(), "What is the ntlm proxy workstation?");
            }
        }

        Boolean trustCert = interactions.askYesOrNo("Would you like to automatically trust certificates?");
        if (trustCert) {
            interactions.setProperty(DetectProperties.BLACKDUCK_TRUST_CERT.getProperty(), "true");
        }
    }

}
