/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.polaris.common.cli;

import java.io.Serializable;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

/**
 * An abstract representation of the Polaris CLI's cli-scan.json version number. Conventional version numbers, as well as current and past
 * codenames are all valid and comparable.
 */
// TODO: This class should be moved to Polaris Common
public class PolarisCliResponseVersion implements Comparable<PolarisCliResponseVersion>, Serializable {
    private static final long serialVersionUID = -5934404604968505634L;
    private final int major;
    private final int minor;

    public PolarisCliResponseVersion(final int major, final int minor) {
        this.major = major;
        this.minor = minor;
    }

    public static Optional<PolarisCliResponseVersion> parse(final String s) {
        try {
            if (StringUtils.isEmpty(s)) {
                return Optional.empty();
            }

            final String[] parts = s.split("\\.");

            final int major;
            final int minor;

            if (parts.length == 1) {
                major = Integer.parseInt(parts[0]);
                minor = 0;
            } else if (parts.length == 2) {
                major = Integer.parseInt(parts[0]);
                minor = Integer.parseInt(parts[1]);
            } else {
                return Optional.empty();
            }

            return Optional.of(new PolarisCliResponseVersion(major, minor));
        } catch (Exception ignored) {
            // Do nothing
        }

        return Optional.empty();
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    @Override
    public String toString() {
        return major + "." + minor;
    }

    @Override
    public int compareTo(final PolarisCliResponseVersion o) {
        if (major != o.major) {
            return Integer.compare(major, o.major);
        }

        return Integer.compare(minor, o.minor);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PolarisCliResponseVersion other = (PolarisCliResponseVersion) o;

        return this.compareTo(other) == 0;
    }

    @Override
    public int hashCode() {
        int result = 31;
        result = 31 * result + major;
        result = 31 * result + minor;
        return result;
    }
}
