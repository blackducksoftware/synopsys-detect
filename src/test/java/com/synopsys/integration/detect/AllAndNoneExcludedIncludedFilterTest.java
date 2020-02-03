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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.synopsys.integration.detect.util.filter.DetectFilter;
import com.synopsys.integration.detect.util.filter.DetectNameFilter;
import com.synopsys.integration.detect.util.filter.DetectOverrideableFilter;

public class AllAndNoneExcludedIncludedFilterTest {

    // NonOverridable

    @Test
    public void testNonOverridableNormalExcludeList() {
        DetectFilter filter = new DetectNameFilter("docker,rubygems", "");
        assertFalse(filter.shouldInclude("docker"));
        assertFalse(filter.shouldInclude("rubygems"));
        assertTrue(filter.shouldInclude("gradle"));
    }

    @Test
    public void testNonOverridableNormalIncludeList() {
        DetectFilter filter = new DetectNameFilter("", "docker,rubygems");
        assertTrue(filter.shouldInclude("docker"));
        assertTrue(filter.shouldInclude("rubygems"));
        assertFalse(filter.shouldInclude("gradle"));
    }

    @Test
    public void testNonOverridableAllExcluded() {
        DetectFilter filter = new DetectNameFilter("ALL", "");
        assertTrue(filter.shouldInclude("docker"));
        assertTrue(filter.shouldInclude("rubygems"));
        assertTrue(filter.shouldInclude("gradle"));
    }

    @Test
    public void testNonOverridableAllExcludedWithIgnoredIncludes() {
        DetectFilter filter = new DetectNameFilter("ALL", "docker,rubygems");
        assertTrue(filter.shouldInclude("docker"));
        assertTrue(filter.shouldInclude("rubygems"));
        assertFalse(filter.shouldInclude("gradle"));
    }

    // Overridable

    @Test
    public void testOverridableNormalExcludeList() {
        DetectFilter filter = new DetectOverrideableFilter("docker,rubygems", "");
        assertFalse(filter.shouldInclude("docker"));
        assertFalse(filter.shouldInclude("rubygems"));
        assertTrue(filter.shouldInclude("gradle"));
    }

    @Test
    public void testOverridableNormalIncludeList() {
        DetectFilter filter = new DetectOverrideableFilter("", "docker,rubygems");
        assertTrue(filter.shouldInclude("docker"));
        assertTrue(filter.shouldInclude("rubygems"));
        assertFalse(filter.shouldInclude("gradle"));
    }

    @Test
    public void testOverridableAllExcluded() {
        DetectFilter filter = new DetectOverrideableFilter("ALL", "");
        assertFalse(filter.shouldInclude("docker"));
        assertFalse(filter.shouldInclude("rubygems"));
        assertFalse(filter.shouldInclude("gradle"));
    }

    @Test
    public void testOverridableAllExcludedWithIgnoredIncludes() {
        DetectFilter filter = new DetectOverrideableFilter("ALL", "docker,rubygems");
        assertFalse(filter.shouldInclude("docker"));
        assertFalse(filter.shouldInclude("rubygems"));
        assertFalse(filter.shouldInclude("gradle"));
    }
}