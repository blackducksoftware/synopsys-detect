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

import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.BomCodeLocationNameService
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DockerScanCodeLocationNameService
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.ScanCodeLocationNameService

import org.junit.Test

import static org.junit.Assert.assertEquals

class CodeLocationNameFactoryTest {
    @Test
    public void testScanCodeLocationNameFactory() {
        String expected = 'hub-common-rest/target/hub-common-rest/2.5.1-SNAPSHOT scan'

        DetectFileFinder detectFileManager = [extractFinalPieceFromPath: { 'hub-common-rest' }] as DetectFileFinder
        ScanCodeLocationNameService scanCodeLocationNameFactory = new ScanCodeLocationNameService()
        scanCodeLocationNameFactory.detectFileFinder = detectFileManager

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
    public void testDockerScanCodeLocationNameFactory() {
        String expected = 'dockerTar.tar.gz/hub-common-rest/2.5.1-SNAPSHOT scan'

        DetectFileFinder detectFileManager = [extractFinalPieceFromPath: { 'hub-common-rest' }] as DetectFileFinder
        DockerScanCodeLocationNameService dockerScanCodeLocationNameService = new DockerScanCodeLocationNameService()
        dockerScanCodeLocationNameService.detectFileFinder = detectFileManager

        String dockerTarFileName = 'dockerTar.tar.gz'
        String projectName = 'hub-common-rest'
        String projectVersionName = '2.5.1-SNAPSHOT'
        String prefix = ''
        String suffix = ''
        String actual = dockerScanCodeLocationNameService.createCodeLocationName(dockerTarFileName, projectName, projectVersionName, prefix, suffix)

        assertEquals(expected, actual)
    }

    @Test
    public void testBomCodeLocationNameFactory() {

        String expected = 'hub-common-rest/child/group/name/version npm/bom'
        //= path/externalId tool/type

        ExternalIdFactory factory = new ExternalIdFactory();
        ExternalId externalId = factory.createMavenExternalId("group", "name", "version");
        BomCodeLocationNameService bomCodeLocationNameFactory = new BomCodeLocationNameService()

        String sourcePath = '/Users/ekerwin/Documents/source/integration/hub-common-rest'
        String codeLocationPath = '/Users/ekerwin/Documents/source/integration/hub-common-rest/child'

        String prefix = ''
        String suffix = ''
        String actual = bomCodeLocationNameFactory.createCodeLocationName(sourcePath, codeLocationPath, externalId, BomToolGroupType.NPM, prefix, suffix)

        assertEquals(expected, actual)
    }

    @Test
    public void testLongCodeLocationNames() {
        String expected = 'hub-common-rest/hub...esthub-common-rest/group/name/version npm/bom'


        ExternalIdFactory factory = new ExternalIdFactory();
        ExternalId externalId = factory.createMavenExternalId("group", "name", "version");
        BomCodeLocationNameService bomCodeLocationNameFactory = new BomCodeLocationNameService()

        String sourcePath = '/Users/ekerwin/Documents/source/integration/hub-common-rest'
        String codeLocationPath = '/Users/ekerwin/Documents/source/integration/hub-common-rest/hub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-rest'
        String prefix = ''
        String suffix = ''
        String actual = bomCodeLocationNameFactory.createCodeLocationName(sourcePath, codeLocationPath, externalId, BomToolGroupType.NPM, prefix, suffix)


        assertEquals(expected, actual)
    }
}
