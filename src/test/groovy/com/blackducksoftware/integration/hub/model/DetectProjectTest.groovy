/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.model

import org.junit.Assert
import org.junit.Test

import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.CodeLocationType
import com.blackducksoftware.integration.hub.detect.model.DetectProject

class DetectProjectTest {
    @Test
    public void getCodeLocationNameBomTest() {
        def detectProject = new DetectProject()
        detectProject.projectName = 'test-name'
        detectProject.projectVersionName = 'test-version'

        String actual = detectProject.getCodeLocationName('path-piece', BomToolType.NPM, CodeLocationType.BOM, 'prefix', 'suffix')
        String expected = 'prefix/path-piece/test-name/test-version/suffix npm/bom'
        Assert.assertEquals(expected, actual)
    }

    @Test
    public void getCodeLocationNameScanTest() {
        def detectProject = new DetectProject()
        detectProject.projectName = 'test-name'
        detectProject.projectVersionName = 'test-version'

        String actual = detectProject.getCodeLocationName('path-piece', null, CodeLocationType.SCAN, 'prefix', 'suffix')
        String expected = 'prefix/path-piece/test-name/test-version/suffix scan'

        Assert.assertEquals(expected, actual)
    }
}
