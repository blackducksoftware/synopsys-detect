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
package com.synopsys.integration.detect;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.util.filter.DetectFilter;
import com.synopsys.integration.detect.util.filter.DetectNameFilter;
import com.synopsys.integration.detect.util.filter.DetectOverrideableFilter;

public class AllAndNoneExcludedIncludedFilterTest {

    //#region Non-overridable

    @Test
    public void testNonOverridableNormalExcludeList() {
        final DetectFilter filter = new DetectNameFilter("docker,rubygems", "");
        Assertions.assertFalse(filter.shouldInclude("docker"));
        Assertions.assertFalse(filter.shouldInclude("rubygems"));
        Assertions.assertTrue(filter.shouldInclude("gradle"));
    }

    @Test
    public void testNonOverridableNormalIncludeList() {
        final DetectFilter filter = new DetectNameFilter("", "docker,rubygems");
        Assertions.assertTrue(filter.shouldInclude("docker"));
        Assertions.assertTrue(filter.shouldInclude("rubygems"));
        Assertions.assertFalse(filter.shouldInclude("gradle"));
    }

    @Test
    public void testNonOverridableAllExcluded() {
        final DetectFilter filter = new DetectNameFilter("ALL", "");
        Assertions.assertTrue(filter.shouldInclude("docker"));
        Assertions.assertTrue(filter.shouldInclude("rubygems"));
        Assertions.assertTrue(filter.shouldInclude("gradle"));
    }

    @Test
    public void testNonOverridableAllExcludedWithIgnoredIncludes() {
        final DetectFilter filter = new DetectNameFilter("ALL", "docker,rubygems");
        Assertions.assertTrue(filter.shouldInclude("docker"));
        Assertions.assertTrue(filter.shouldInclude("rubygems"));
        Assertions.assertFalse(filter.shouldInclude("gradle"));
    }

    //#endregion Non-overridable

    //#region Overridable

    @Test
    public void testOverridableNormalExcludeList() {
        final DetectFilter filter = new DetectOverrideableFilter("docker,rubygems", "");
        Assertions.assertFalse(filter.shouldInclude("docker"));
        Assertions.assertFalse(filter.shouldInclude("rubygems"));
        Assertions.assertTrue(filter.shouldInclude("gradle"));
    }

    @Test
    public void testOverridableNormalIncludeList() {
        final DetectFilter filter = new DetectOverrideableFilter("", "docker,rubygems");
        Assertions.assertTrue(filter.shouldInclude("docker"));
        Assertions.assertTrue(filter.shouldInclude("rubygems"));
        Assertions.assertFalse(filter.shouldInclude("gradle"));
    }

    @Test
    public void testOverridableAllExcluded() {
        final DetectFilter filter = new DetectOverrideableFilter("ALL", "");
        Assertions.assertFalse(filter.shouldInclude("docker"));
        Assertions.assertFalse(filter.shouldInclude("rubygems"));
        Assertions.assertFalse(filter.shouldInclude("gradle"));
    }

    @Test
    public void testOverridableAllExcludedWithIgnoredIncludes() {
        final DetectFilter filter = new DetectOverrideableFilter("ALL", "docker,rubygems");
        Assertions.assertFalse(filter.shouldInclude("docker"));
        Assertions.assertFalse(filter.shouldInclude("rubygems"));
        Assertions.assertFalse(filter.shouldInclude("gradle"));
    }

    //#endregion Overridable
}