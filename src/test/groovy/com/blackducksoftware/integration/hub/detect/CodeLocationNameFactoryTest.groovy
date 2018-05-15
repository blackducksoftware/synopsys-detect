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
package com.blackducksoftware.integration.hub.detect

import static org.junit.Assert.*

import org.junit.Test

import com.blackducksoftware.integration.hub.detect.codelocation.BomCodeLocationNameFactory
import com.blackducksoftware.integration.hub.detect.codelocation.ScanCodeLocationNameFactory
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager

class CodeLocationNameFactoryTest {
    @Test
    public void testScanCodeLocationNameFactory() {
        String expected = 'hub-common-rest/target/hub-common-rest/2.5.1-SNAPSHOT scan'

        DetectFileManager detectFileManager = [extractFinalPieceFromPath: { 'hub-common-rest' }] as DetectFileManager
        ScanCodeLocationNameFactory scanCodeLocationNameFactory = new ScanCodeLocationNameFactory()
        scanCodeLocationNameFactory.detectFileManager = detectFileManager

        String sourcePath = '/Users/ekerwin/Documents/source/integration/hub-common-rest'
        String scanTargetPath = '/Users/ekerwin/Documents/source/integration/hub-common-rest/target'
        String projectName = 'hub-common-rest'
        String projectVersionName = '2.5.1-SNAPSHOT'
        String prefix = ''
        String suffix = ''
        String actual = scanCodeLocationNameFactory.createCodeLocationName(sourcePath, scanTargetPath, projectName, projectVersionName, prefix, suffix)

        assertEquals(expected, actual)
    }

    @Test
    public void testBomCodeLocationNameFactory() {
        String expected = 'hub-common-rest/hub-common-rest/2.5.1-SNAPSHOT npm/bom'

        DetectFileManager detectFileManager = [extractFinalPieceFromPath: { 'hub-common-rest' }] as DetectFileManager
        BomCodeLocationNameFactory bomCodeLocationNameFactory = new BomCodeLocationNameFactory()
        bomCodeLocationNameFactory.detectFileManager = detectFileManager

        String sourcePath = '/Users/ekerwin/Documents/source/integration/hub-common-rest'
        String projectName = 'hub-common-rest'
        String projectVersionName = '2.5.1-SNAPSHOT'
        String prefix = ''
        String suffix = ''
        String actual = bomCodeLocationNameFactory.createCodeLocationName(sourcePath, projectName, projectVersionName, BomToolType.NPM, prefix, suffix)

        assertEquals(expected, actual)
    }

    @Test
    public void testLongCodeLocationNames() {
        String expected = 'hub-common-rest/hub-common-resthub-...esthub-common-rest/2.5.1-SNAPSHOT npm/bom'

        DetectFileManager detectFileManager = [extractFinalPieceFromPath: { 'hub-common-rest' }] as DetectFileManager
        BomCodeLocationNameFactory bomCodeLocationNameFactory = new BomCodeLocationNameFactory()
        bomCodeLocationNameFactory.detectFileManager = detectFileManager

        String sourcePath = '/Users/ekerwin/Documents/source/integration/hub-common-rest'
        String projectName = 'hub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-rest'
        String projectVersionName = '2.5.1-SNAPSHOT'
        String prefix = ''
        String suffix = ''
        String actual = bomCodeLocationNameFactory.createCodeLocationName(sourcePath, projectName, projectVersionName, BomToolType.NPM, prefix, suffix)

        assertEquals(expected, actual)
    }
}
