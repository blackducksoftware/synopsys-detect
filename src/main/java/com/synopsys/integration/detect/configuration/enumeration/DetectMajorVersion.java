/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.configuration.enumeration;

import com.synopsys.integration.configuration.util.ProductMajorVersion;

public class DetectMajorVersion extends ProductMajorVersion {
    public static final DetectMajorVersion ONE = new DetectMajorVersion(1);
    public static final DetectMajorVersion TWO = new DetectMajorVersion(2);
    public static final DetectMajorVersion THREE = new DetectMajorVersion(3);
    public static final DetectMajorVersion FOUR = new DetectMajorVersion(4);
    public static final DetectMajorVersion FIVE = new DetectMajorVersion(5);
    public static final DetectMajorVersion SIX = new DetectMajorVersion(6);
    public static final DetectMajorVersion SEVEN = new DetectMajorVersion(7);
    public static final DetectMajorVersion EIGHT = new DetectMajorVersion(8);
    public static final DetectMajorVersion NINE = new DetectMajorVersion(9);

    public DetectMajorVersion(Integer intValue) {
        super(intValue);
    }
}