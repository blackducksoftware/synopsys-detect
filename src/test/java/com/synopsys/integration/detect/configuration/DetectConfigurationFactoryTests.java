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
package com.synopsys.integration.detect.configuration;

import static com.synopsys.integration.detect.configuration.DetectConfigurationFactoryTestUtils.factoryOf;
import static com.synopsys.integration.detect.configuration.DetectConfigurationFactoryTestUtils.spyFactoryOf;

import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.SnippetMatching;
import com.synopsys.integration.common.util.Bdo;
import com.synopsys.integration.rest.credentials.Credentials;

public class DetectConfigurationFactoryTests {

    //#region Proxy
    @Test
    public void proxyUsesCredentials() throws DetectUserFriendlyException {
        final DetectConfigurationFactory factory = factoryOf(
            Pair.of(DetectProperties.BLACKDUCK_PROXY_HOST.getProperty(), "host"),
            Pair.of(DetectProperties.BLACKDUCK_PROXY_PORT.getProperty(), "20"),
            Pair.of(DetectProperties.BLACKDUCK_PROXY_USERNAME.getProperty(), "username"),
            Pair.of(DetectProperties.BLACKDUCK_PROXY_PASSWORD.getProperty(), "password")
        );
        final Bdo<Credentials> result = Bdo.of(factory.createBlackDuckProxyInfo().getProxyCredentials());

        Assertions.assertEquals(Optional.of("username"), result.flatMap(Credentials::getUsername).toOptional());
        Assertions.assertEquals(Optional.of("password"), result.flatMap(Credentials::getPassword).toOptional());
    }
    //#endregion Proxy

    //#region Parallel Processors
    @Test
    public void parallelProcessorsDefaultsToOne() {
        // Using the property default is the safe choice. See IDETECT-1970 - JM
        final DetectConfigurationFactory factory = spyFactoryOf();
        final Integer defaultValue = DetectProperties.DETECT_PARALLEL_PROCESSORS.getProperty().getDefaultValue();

        Assertions.assertEquals(defaultValue.intValue(), factory.findParallelProcessors());
        Mockito.verify(factory, Mockito.never()).findRuntimeProcessors();
    }

    @Test
    public void parallelProcessorsPrefersProperty() {
        final DetectConfigurationFactory factory = factoryOf(Pair.of(DetectProperties.DETECT_PARALLEL_PROCESSORS.getProperty(), "3"));

        Assertions.assertEquals(3, factory.findParallelProcessors());
    }

    @Test
    public void parallelProcessorsPrefersNewProperty() {
        final DetectConfigurationFactory factory = factoryOf(
            Pair.of(DetectProperties.DETECT_PARALLEL_PROCESSORS.getProperty(), "5"),
            Pair.of(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS.getProperty(), "4")
        );

        Assertions.assertEquals(5, factory.findParallelProcessors());
    }

    @Test
    public void parallelProcessorsFallsBackToOldProperty() {
        final DetectConfigurationFactory factory = factoryOf(
            Pair.of(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS.getProperty(), "5")
        );

        Assertions.assertEquals(5, factory.findParallelProcessors());
    }
    //#endregion Parallel Processors

    //#region Snippet Matching
    @Test
    public void snippetMatchingDeprecatedPropertyEnablesSnippets() {
        final DetectConfigurationFactory factory = factoryOf(
            Pair.of(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MODE.getProperty(), "true")
        );

        Assertions.assertEquals(SnippetMatching.SNIPPET_MATCHING, factory.findSnippetMatching());
    }

    @Test
    public void snippetMatchingPrefersNewerProperty() {
        final DetectConfigurationFactory factory = factoryOf(
            Pair.of(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MODE.getProperty(), "true"),
            Pair.of(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MATCHING.getProperty(), SnippetMatching.FULL_SNIPPET_MATCHING_ONLY.name())
        );

        Assertions.assertEquals(SnippetMatching.FULL_SNIPPET_MATCHING_ONLY, factory.findSnippetMatching());
    }
    //#endregion Snippet Matching

}