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
package com.synopsys.integration.detect.integration;

import static java.nio.file.Files.lines;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.blackduck.api.generated.view.CodeLocationView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionComponentView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectView;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.Application;
import com.synopsys.integration.exception.IntegrationException;

@Tag("integration")
public class DetectOnDetectHappyPath extends BlackDuckIntegrationTest {
    public static final String SIGNATURE_SCAN_CODE_LOCATION_SUFFIX = "/synopsys-detect-junit/happy-path scan";
    public static final String DETECTABLE_CODE_LOCATION_SUFFIX = "/detectable/com.synopsys.integration/detectable/%s gradle/bom";
    public static final String SYNOPSYS_DETECT_CODE_LOCATION_SUFFIX = "/com.synopsys.integration/synopsys-detect/%s gradle/bom";
    public static final String COMMON_CODE_LOCATION_SUFFIX = "/common/com.synopsys.integration/common/%s gradle/bom";
    public static final String COMMON_TEST_CODE_LOCATION_SUFFIX = "/common-test/com.synopsys.integration/common-test/%s gradle/bom";
    public static final String CONFIGURATION_CODE_LOCATION_SUFFIX = "/configuration/com.synopsys.integration/configuration/%s gradle/bom";
    public static final String DETECTOR_CODE_LOCATION_SUFFIX = "/detector/com.synopsys.integration/detector/%s gradle/bom";

    private ProjectView projectToDelete = null;
    private List<CodeLocationView> codeLocationsToDelete = null;

    @AfterEach
    public void deleteBlackDuckItems() {
        if (null != projectToDelete) {
            try {
                blackDuckService.delete(projectToDelete);
            } catch (IntegrationException e) {
                e.printStackTrace();
            }
        }

        if (null != codeLocationsToDelete) {
            for (CodeLocationView toDelete : codeLocationsToDelete) {
                try {
                    blackDuckService.delete(toDelete);
                } catch (IntegrationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testBomCreatedInBlackDuck() throws Exception {
        Path buildGradle = Paths.get("build.gradle");
        Optional<String> versionLine = lines(buildGradle).map(String::trim).filter(line -> line.startsWith("version")).findFirst();
        String version = StringUtils.substringBetween(versionLine.get(), "'");

        List<String> codeLocationNameSuffixesToCheck = new ArrayList<>();
        codeLocationNameSuffixesToCheck.add(SIGNATURE_SCAN_CODE_LOCATION_SUFFIX);
        codeLocationNameSuffixesToCheck.add(String.format(DETECTABLE_CODE_LOCATION_SUFFIX, version));
        codeLocationNameSuffixesToCheck.add(String.format(SYNOPSYS_DETECT_CODE_LOCATION_SUFFIX, version));
        codeLocationNameSuffixesToCheck.add(String.format(COMMON_CODE_LOCATION_SUFFIX, version));
        codeLocationNameSuffixesToCheck.add(String.format(COMMON_TEST_CODE_LOCATION_SUFFIX, version));
        codeLocationNameSuffixesToCheck.add(String.format(CONFIGURATION_CODE_LOCATION_SUFFIX, version));
        codeLocationNameSuffixesToCheck.add(String.format(DETECTOR_CODE_LOCATION_SUFFIX, version));
        
        final String projectName = "synopsys-detect-junit";
        final String projectVersionName = "happy-path";
        ProjectVersionWrapper projectVersionWrapper = assertProjectVersionReady(projectName, projectVersionName);
        projectToDelete = projectVersionWrapper.getProjectView();

        List<String> detectArgs = getInitialArgs(projectName, projectVersionName);
        detectArgs.add("--detect.wait.for.results=true");
        Application.main(detectArgs.toArray(ArrayUtils.EMPTY_STRING_ARRAY));

        codeLocationsToDelete = blackDuckService.getAllResponses(projectVersionWrapper.getProjectVersionView(), ProjectVersionView.CODELOCATIONS_LINK_RESPONSE);
        Set<String> createdCodeLocationNames = codeLocationsToDelete.stream().map(CodeLocationView::getName).collect(Collectors.toSet());
        createdCodeLocationNames.forEach(System.out::println);
        codeLocationNameSuffixesToCheck.forEach(System.out::println);

        assertEquals(codeLocationNameSuffixesToCheck.size(), createdCodeLocationNames.size());
        int matches = 0;
        for (String suffix : codeLocationNameSuffixesToCheck) {
            for (String codeLocationName : createdCodeLocationNames) {
                if (codeLocationName.endsWith(suffix)) {
                    matches++;
                    break;
                }
            }
        }
        assertEquals(codeLocationNameSuffixesToCheck.size(), matches);

        List<ProjectVersionComponentView> bomComponents = projectBomService.getComponentsForProjectVersion(projectVersionWrapper.getProjectVersionView());
        // We used to look for blackduck-common, but we adopt new versions faster than KB can pick them up
        Optional<ProjectVersionComponentView> blackDuckCommonComponent = bomComponents.stream().filter(ProjectVersionComponentView -> "jackson-core".equals(ProjectVersionComponentView.getComponentName())).findFirst();
        assertTrue(blackDuckCommonComponent.isPresent());
    }

}
