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

import com.blackducksoftware.integration.hub.detect.codelocation.BomCodeLocationNameProvider
import com.blackducksoftware.integration.hub.detect.codelocation.CodeLocationNameProvider
import com.blackducksoftware.integration.hub.detect.codelocation.CodeLocationNameService
import com.blackducksoftware.integration.hub.detect.codelocation.ScanCodeLocationNameProvider
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager

class CodeLocationNameServiceTest {
    def codeLocationNameService = new CodeLocationNameService()

    @Test
    public void testScanCodeLocationNameProvider() {
        String expected = 'hub-common-rest/target/hub-common-rest/2.5.1-SNAPSHOT scan'

        DetectFileManager detectFileManager = [extractFinalPieceFromPath: { 'hub-common-rest' }] as DetectFileManager
        CodeLocationNameProvider codeLocationNameProvider = new ScanCodeLocationNameProvider()
        codeLocationNameProvider.detectFileManager = detectFileManager

        String sourcePath = '/Users/ekerwin/Documents/source/integration/hub-common-rest'
        String scanTargetPath = '/Users/ekerwin/Documents/source/integration/hub-common-rest/target'
        String projectName = 'hub-common-rest'
        String projectVersionName = '2.5.1-SNAPSHOT'
        String prefix = ''
        String suffix = ''
        String actual = codeLocationNameService.createScanName(sourcePath, scanTargetPath, projectName, projectVersionName, prefix, suffix)

        assertEquals(expected, actual)
    }

    @Test
    public void testBomCodeLocationNameProvider() {
        String expected = 'hub-common-rest/hub-common-rest/2.5.1-SNAPSHOT npm/bom'

        DetectFileManager detectFileManager = [extractFinalPieceFromPath: { 'hub-common-rest' }] as DetectFileManager
        CodeLocationNameProvider codeLocationNameProvider = new BomCodeLocationNameProvider()
        codeLocationNameProvider.detectFileManager = detectFileManager

        String sourcePath = '/Users/ekerwin/Documents/source/integration/hub-common-rest'
        String projectName = 'hub-common-rest'
        String projectVersionName = '2.5.1-SNAPSHOT'
        String prefix = ''
        String suffix = ''
        String actual = codeLocationNameService.createBomToolName(sourcePath, projectName, projectVersionName, BomToolType.NPM, prefix, suffix)

        assertEquals(expected, actual)
    }

    @Test
    public void testLongCodeLocationNames() {
        String expected = 'hub-common-rest/hub-common-resthub-...esthub-common-rest/2.5.1-SNAPSHOT npm/bom'

        DetectFileManager detectFileManager = [extractFinalPieceFromPath: { 'hub-common-rest' }] as DetectFileManager
        CodeLocationNameProvider codeLocationNameProvider = new BomCodeLocationNameProvider()
        codeLocationNameProvider.detectFileManager = detectFileManager

        String sourcePath = '/Users/ekerwin/Documents/source/integration/hub-common-rest'
        String projectName = 'hub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-rest'
        String projectVersionName = '2.5.1-SNAPSHOT'
        String prefix = ''
        String suffix = ''
        String actual = codeLocationNameService.createBomToolName(sourcePath, projectName, projectVersionName, BomToolType.NPM, prefix, suffix)

        assertEquals(expected, actual)
    }
}
