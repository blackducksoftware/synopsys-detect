/**
 * detector
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
package com.synopsys.integration.detector.finder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleDetectable;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliDetectable;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSet;
import com.synopsys.integration.detector.rule.DetectorRuleSetBuilder;

public class DetectorRuleSetBuilderTest {
    @Test
    public void testOrderGivenUsed() {
        DetectorRuleSetBuilder ruleSetBuilder = new DetectorRuleSetBuilder();
        ruleSetBuilder.addDetector(DetectorType.GRADLE, "Gradle", GradleDetectable.class, (e) -> null).build();
        ruleSetBuilder.addDetector(DetectorType.NPM, "Npm", NpmCliDetectable.class, (e) -> null).build();
        DetectorRuleSet ruleSet = ruleSetBuilder.build();

        Assertions.assertEquals(2, ruleSet.getOrderedDetectorRules().size());
        Assertions.assertEquals("Gradle", ruleSet.getOrderedDetectorRules().get(0).getName());
        Assertions.assertEquals("Npm", ruleSet.getOrderedDetectorRules().get(1).getName());
    }

    @Test
    public void testReorderedWithYield() {
        DetectorRuleSetBuilder ruleSetBuilder = new DetectorRuleSetBuilder();
        DetectorRule gradle = ruleSetBuilder.addDetector(DetectorType.GRADLE, "Gradle", GradleDetectable.class, (e) -> null).build();
        DetectorRule npm = ruleSetBuilder.addDetector(DetectorType.NPM, "Npm", NpmCliDetectable.class, (e) -> null).build();
        ruleSetBuilder.yield(gradle).to(npm);
        DetectorRuleSet ruleSet = ruleSetBuilder.build();

        Assertions.assertEquals(2, ruleSet.getOrderedDetectorRules().size());
        Assertions.assertEquals("Npm", ruleSet.getOrderedDetectorRules().get(0).getName());
        Assertions.assertEquals("Gradle", ruleSet.getOrderedDetectorRules().get(1).getName());
    }

    @Test
    public void testCircularYieldThrows() {
        DetectorRuleSetBuilder ruleSetBuilder = new DetectorRuleSetBuilder();
        DetectorRule gradle = ruleSetBuilder.addDetector(DetectorType.GRADLE, "Gradle", GradleDetectable.class, (e) -> null).build();
        DetectorRule npm = ruleSetBuilder.addDetector(DetectorType.NPM, "Npm", NpmCliDetectable.class, (e) -> null).build();

        ruleSetBuilder.yield(gradle).to(npm);
        ruleSetBuilder.yield(npm).to(gradle);

        Assertions.assertThrows(RuntimeException.class, ruleSetBuilder::build);
    }
}
