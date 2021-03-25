/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.font;

import java.io.File;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;

public interface DetectFontLocator {
    File locateRegularFontFile() throws DetectUserFriendlyException;

    File locateBoldFontFile() throws DetectUserFriendlyException;
}
