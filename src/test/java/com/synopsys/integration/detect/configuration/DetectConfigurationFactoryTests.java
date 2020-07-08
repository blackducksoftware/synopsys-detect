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
import com.synopsys.integration.configuration.config.InvalidPropertyException;
import com.synopsys.integration.configuration.util.Bdo;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.rest.credentials.Credentials;

public class DetectConfigurationFactoryTests {

    //#region Proxy
    @Test
    public void proxyUsesCredentials() throws DetectUserFriendlyException {
        final DetectConfigurationFactory factory = factoryOf(
            Pair.of(DetectProperties.Companion.getBLACKDUCK_PROXY_HOST(), "host"),
            Pair.of(DetectProperties.Companion.getBLACKDUCK_PROXY_PORT(), "20"),
            Pair.of(DetectProperties.Companion.getBLACKDUCK_PROXY_USERNAME(), "username"),
            Pair.of(DetectProperties.Companion.getBLACKDUCK_PROXY_PASSWORD(), "password")
        );
        final Bdo<Credentials> result = Bdo.of(factory.createBlackDuckProxyInfo().getProxyCredentials());

        Assertions.assertEquals(Optional.of("username"), result.flatMap(Credentials::getUsername).toOptional());
        Assertions.assertEquals(Optional.of("password"), result.flatMap(Credentials::getPassword).toOptional());
    }
    //#endregion Proxy

    //#region Parallel Processors
    @Test
    public void parallelProcessorsDefaultsToOne() throws InvalidPropertyException {
        // Using the property default is the safe choice. See IDETECT-1970 - JM
        final DetectConfigurationFactory factory = spyFactoryOf();
        final Integer defaultValue = DetectProperties.Companion.getDETECT_PARALLEL_PROCESSORS().getDefaultValue();

        Assertions.assertEquals(defaultValue.intValue(), factory.findParallelProcessors());
        Mockito.verify(factory, Mockito.never()).findRuntimeProcessors();
    }

    @Test
    public void parallelProcessorsPrefersProperty() throws InvalidPropertyException {
        final DetectConfigurationFactory factory = factoryOf(Pair.of(DetectProperties.Companion.getDETECT_PARALLEL_PROCESSORS(), "3"));

        Assertions.assertEquals(3, factory.findParallelProcessors());
    }

    @Test
    public void parallelProcessorsPrefersNewProperty() throws InvalidPropertyException {
        final DetectConfigurationFactory factory = factoryOf(
            Pair.of(DetectProperties.Companion.getDETECT_PARALLEL_PROCESSORS(), "5"),
            Pair.of(DetectProperties.Companion.getDETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS(), "4")
        );

        Assertions.assertEquals(5, factory.findParallelProcessors());
    }

    @Test
    public void parallelProcessorsFallsBackToOldProperty() throws InvalidPropertyException {
        final DetectConfigurationFactory factory = factoryOf(
            Pair.of(DetectProperties.Companion.getDETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS(), "5")
        );

        Assertions.assertEquals(5, factory.findParallelProcessors());
    }
    //#endregion Parallel Processors

    //#region Snippet Matching
    @Test
    public void snippetMatchingDeprecatedPropertyEnablesSnippets() throws InvalidPropertyException {
        final DetectConfigurationFactory factory = factoryOf(
            Pair.of(DetectProperties.Companion.getDETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MODE(), "true")
        );

        Assertions.assertEquals(SnippetMatching.SNIPPET_MATCHING, factory.findSnippetMatching());
    }

    @Test
    public void snippetMatchingPrefersNewerProperty() throws InvalidPropertyException {
        final DetectConfigurationFactory factory = factoryOf(
            Pair.of(DetectProperties.Companion.getDETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MODE(), "true"),
            Pair.of(DetectProperties.Companion.getDETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MATCHING(), SnippetMatching.FULL_SNIPPET_MATCHING_ONLY.name())
        );

        Assertions.assertEquals(SnippetMatching.FULL_SNIPPET_MATCHING_ONLY, factory.findSnippetMatching());
    }
    //#endregion Snippet Matching

}