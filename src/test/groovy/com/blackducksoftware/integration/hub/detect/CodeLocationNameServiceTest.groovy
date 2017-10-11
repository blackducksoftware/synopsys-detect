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

import com.blackducksoftware.integration.hub.detect.hub.HubManager
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.CodeLocationName
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager

class CodeLocationNameServiceTest {
    @Test
    public void testScanVersion1() {
        String expected = 'hub-common-rest/target/hub-common-rest/2.5.1-SNAPSHOT Hub Detect Scan'

        HubManager hubManager = [logCodeLocationNamesExists: {}] as HubManager
        DetectFileManager detectFileManager = [extractFinalPieceFromPath: { 'hub-common-rest' }] as DetectFileManager
        CodeLocationNameService codeLocationNameService = new CodeLocationNameService()
        codeLocationNameService.hubManager = hubManager
        codeLocationNameService.detectFileManager = detectFileManager

        String sourcePath = '/Users/ekerwin/Documents/source/integration/hub-common-rest'
        String scanTargetPath = '/Users/ekerwin/Documents/source/integration/hub-common-rest/target'
        String projectName = 'hub-common-rest'
        String projectVersionName = '2.5.1-SNAPSHOT'
        String prefix = ''
        String suffix = ''
        CodeLocationName codeLocationName = codeLocationNameService.createScanName(sourcePath, scanTargetPath, projectName, projectVersionName, prefix, suffix)
        String actual = codeLocationNameService.generateScanVersion1(codeLocationName)

        assertEquals(expected, actual)
    }

    @Test
    public void testScanVersion2() {
        String expected = 'hub-common-rest/target/hub-common-rest/2.5.1-SNAPSHOT SCAN'

        HubManager hubManager = [logCodeLocationNamesExists: {}] as HubManager
        DetectFileManager detectFileManager = [extractFinalPieceFromPath: { 'hub-common-rest' }] as DetectFileManager
        CodeLocationNameService codeLocationNameService = new CodeLocationNameService()
        codeLocationNameService.hubManager = hubManager
        codeLocationNameService.detectFileManager = detectFileManager

        String sourcePath = '/Users/ekerwin/Documents/source/integration/hub-common-rest'
        String scanTargetPath = '/Users/ekerwin/Documents/source/integration/hub-common-rest/target'
        String projectName = 'hub-common-rest'
        String projectVersionName = '2.5.1-SNAPSHOT'
        String prefix = ''
        String suffix = ''
        CodeLocationName codeLocationName = codeLocationNameService.createScanName(sourcePath, scanTargetPath, projectName, projectVersionName, prefix, suffix)
        String actual = codeLocationNameService.generateScanVersion2(codeLocationName)

        assertEquals(expected, actual)
    }

    @Test
    public void testScanVersion3() {
        String expected = 'hub-common-rest/target/hub-common-rest/2.5.1-SNAPSHOT scan'

        HubManager hubManager = [logCodeLocationNamesExists: {}] as HubManager
        DetectFileManager detectFileManager = [extractFinalPieceFromPath: { 'hub-common-rest' }] as DetectFileManager
        CodeLocationNameService codeLocationNameService = new CodeLocationNameService()
        codeLocationNameService.hubManager = hubManager
        codeLocationNameService.detectFileManager = detectFileManager

        String sourcePath = '/Users/ekerwin/Documents/source/integration/hub-common-rest'
        String scanTargetPath = '/Users/ekerwin/Documents/source/integration/hub-common-rest/target'
        String projectName = 'hub-common-rest'
        String projectVersionName = '2.5.1-SNAPSHOT'
        String prefix = ''
        String suffix = ''
        CodeLocationName codeLocationName = codeLocationNameService.createScanName(sourcePath, scanTargetPath, projectName, projectVersionName, prefix, suffix)
        String actual = codeLocationNameService.generateScanCurrent(codeLocationName)

        assertEquals(expected, actual)
    }

    @Test
    public void testBomVersion1() {
        String expected = 'NPM/hub-common-rest/hub-common-rest/2.5.1-SNAPSHOT Hub Detect Tool'

        HubManager hubManager = [logCodeLocationNamesExists: {}] as HubManager
        DetectFileManager detectFileManager = [extractFinalPieceFromPath: { 'hub-common-rest' }] as DetectFileManager
        CodeLocationNameService codeLocationNameService = new CodeLocationNameService()
        codeLocationNameService.hubManager = hubManager
        codeLocationNameService.detectFileManager = detectFileManager

        String sourcePath = '/Users/ekerwin/Documents/source/integration/hub-common-rest'
        String projectName = 'hub-common-rest'
        String projectVersionName = '2.5.1-SNAPSHOT'
        String prefix = ''
        String suffix = ''
        CodeLocationName codeLocationName = codeLocationNameService.createBomToolName(sourcePath, projectName, projectVersionName, BomToolType.NPM, prefix, suffix)
        String actual = codeLocationNameService.generateBomToolVersion1(codeLocationName)

        assertEquals(expected, actual)
    }

    @Test
    public void testBomVersion2() {
        String expected = 'NPM/hub-common-rest/hub-common-rest/2.5.1-SNAPSHOT BOM'

        HubManager hubManager = [logCodeLocationNamesExists: {}] as HubManager
        DetectFileManager detectFileManager = [extractFinalPieceFromPath: { 'hub-common-rest' }] as DetectFileManager
        CodeLocationNameService codeLocationNameService = new CodeLocationNameService()
        codeLocationNameService.hubManager = hubManager
        codeLocationNameService.detectFileManager = detectFileManager

        String sourcePath = '/Users/ekerwin/Documents/source/integration/hub-common-rest'
        String projectName = 'hub-common-rest'
        String projectVersionName = '2.5.1-SNAPSHOT'
        String prefix = ''
        String suffix = ''
        CodeLocationName codeLocationName = codeLocationNameService.createBomToolName(sourcePath, projectName, projectVersionName, BomToolType.NPM, prefix, suffix)
        String actual = codeLocationNameService.generateBomToolVersion2(codeLocationName)

        assertEquals(expected, actual)
    }

    @Test
    public void testBomVersion3() {
        String expected = 'hub-common-rest/hub-common-rest/2.5.1-SNAPSHOT npm/bom'

        HubManager hubManager = [logCodeLocationNamesExists: {}] as HubManager
        DetectFileManager detectFileManager = [extractFinalPieceFromPath: {'hub-common-rest'}] as DetectFileManager
        CodeLocationNameService codeLocationNameService = new CodeLocationNameService()
        codeLocationNameService.hubManager = hubManager
        codeLocationNameService.detectFileManager = detectFileManager

        String sourcePath = '/Users/ekerwin/Documents/source/integration/hub-common-rest'
        String projectName = 'hub-common-rest'
        String projectVersionName = '2.5.1-SNAPSHOT'
        String prefix = ''
        String suffix = ''
        CodeLocationName codeLocationName = codeLocationNameService.createBomToolName(sourcePath, projectName, projectVersionName, BomToolType.NPM, prefix, suffix)
        String actual = codeLocationNameService.generateBomToolCurrent(codeLocationName)

        assertEquals(expected, actual)
    }
}
