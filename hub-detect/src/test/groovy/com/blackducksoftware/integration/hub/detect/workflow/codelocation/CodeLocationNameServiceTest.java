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
package com.blackducksoftware.integration.hub.detect.workflow.codelocation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

public class CodeLocationNameServiceTest {
    @Test
    public void testScanCodeLocationName() {
        final String expected = "hub-common-rest/target/hub-common-rest/2.5.1-SNAPSHOT scan";

        final DetectFileFinder detectFileFinder = mock(DetectFileFinder.class);
        when(detectFileFinder.extractFinalPieceFromPath("/Users/ekerwin/Documents/source/integration/hub-common-rest")).thenReturn("hub-common-rest");
        final CodeLocationNameService codeLocationNameService = new CodeLocationNameService(detectFileFinder);

        final String sourcePath = "/Users/ekerwin/Documents/source/integration/hub-common-rest";
        final String scanTargetPath = "/Users/ekerwin/Documents/source/integration/hub-common-rest/target";
        final String projectName = "hub-common-rest";
        final String projectVersionName = "2.5.1-SNAPSHOT";
        final String prefix = "";
        final String suffix = "";
        final String actual = codeLocationNameService.createScanCodeLocationName(sourcePath, scanTargetPath, projectName, projectVersionName, prefix, suffix);

        assertEquals(expected, actual);
    }

    @Test
    public void testDockerScanCodeLocationName() {
        final String expected = "dockerTar.tar.gz/hub-common-rest/2.5.1-SNAPSHOT scan";

        final DetectFileFinder detectFileFinder = mock(DetectFileFinder.class);
        when(detectFileFinder.extractFinalPieceFromPath("")).thenReturn("hub-common-rest");
        final CodeLocationNameService codeLocationNameService = new CodeLocationNameService(detectFileFinder);

        final String dockerTarFileName = "dockerTar.tar.gz";
        final String projectName = "hub-common-rest";
        final String projectVersionName = "2.5.1-SNAPSHOT";
        final String prefix = "";
        final String suffix = "";
        final String actual = codeLocationNameService.createDockerScanCodeLocationName(dockerTarFileName, projectName, projectVersionName, prefix, suffix);

        assertEquals(expected, actual);
    }

    @Test
    public void testBomCodeLocationName() {

        final String expected = "hub-common-rest/child/group/name/version npm/bom/hahafuckyou";
        // = path/externalId tool/type

        final ExternalIdFactory factory = new ExternalIdFactory();
        final ExternalId externalId = factory.createMavenExternalId("group", "name", "version");
        final DetectFileFinder detectFileFinder = new DetectFileFinder();
        final CodeLocationNameService codeLocationNameService = new CodeLocationNameService(detectFileFinder);

        final String sourcePath = "/Users/ekerwin/Documents/source/integration/hub-common-rest";
        final String codeLocationPath = "/Users/ekerwin/Documents/source/integration/hub-common-rest/child";

        final String prefix = "";
        final String suffix = "";
        final String actual = codeLocationNameService.createBomCodeLocationName(sourcePath, codeLocationPath, externalId, BomToolGroupType.NPM, prefix, suffix);

        assertEquals(expected, actual);
    }

    @Test
    public void testLongCodeLocationNames() {
        final String expected = "hub-common-rest/hub...esthub-common-rest/group/name/version npm/bom";

        final ExternalIdFactory factory = new ExternalIdFactory();
        final ExternalId externalId = factory.createMavenExternalId("group", "name", "version");
        final DetectFileFinder detectFileFinder = new DetectFileFinder();
        final CodeLocationNameService codeLocationNameService = new CodeLocationNameService(detectFileFinder);

        final String sourcePath = "/Users/ekerwin/Documents/source/integration/hub-common-rest";
        final String codeLocationPath = "/Users/ekerwin/Documents/source/integration/hub-common-rest/hub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-resthub-common-rest";
        final String prefix = "";
        final String suffix = "";
        final String actual = codeLocationNameService.createBomCodeLocationName(sourcePath, codeLocationPath, externalId, BomToolGroupType.NPM, prefix, suffix);

        assertEquals(expected, actual);
    }
}
