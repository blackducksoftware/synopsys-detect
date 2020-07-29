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
package com.synopsys.integration.detect.workflow.codelocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class CodeLocationNameGeneratorTest {
    @Test
    public void testScanCodeLocationName() throws IOException {
        final String expected = "common-rest/target/common-rest/2.5.1-SNAPSHOT scan";
        final CodeLocationNameGenerator codeLocationNameGenerator = new CodeLocationNameGenerator(null);

        final File sourcePath = mockCanonical("/Users/ekerwin/Documents/source/functional/common-rest");
        final File scanTargetPath = mockCanonical("/Users/ekerwin/Documents/source/functional/common-rest/target");
        final String projectName = "common-rest";
        final String projectVersionName = "2.5.1-SNAPSHOT";
        final String prefix = "";
        final String suffix = "";
        final String actual = codeLocationNameGenerator.createScanCodeLocationName(sourcePath, scanTargetPath, projectName, projectVersionName, prefix, suffix);

        assertEquals(expected, actual);
    }

    private File mockCanonical(final String mock) throws IOException {
        final File mockFile = Mockito.mock(File.class);
        Mockito.when(mockFile.getCanonicalPath()).thenReturn(mock);
        return mockFile;
    }

    @Test
    public void testDockerScanCodeLocationName() {
        final String expected = "dockerTar.tar.gz/common-rest/2.5.1-SNAPSHOT scan";
        final CodeLocationNameGenerator codeLocationNameGenerator = new CodeLocationNameGenerator(null);

        final File dockerTar = new File("dockerTar.tar.gz");
        final String projectName = "common-rest";
        final String projectVersionName = "2.5.1-SNAPSHOT";
        final String prefix = "";
        final String suffix = "";
        final String actual = codeLocationNameGenerator.createDockerScanCodeLocationName(dockerTar, projectName, projectVersionName, prefix, suffix);

        assertEquals(expected, actual);
    }

    @Test
    public void testBomCodeLocationName() {
        final String expected = "projectName/projectVersion/child/group/name/version npm/bom";
        // = path/externalId tool/type

        final ExternalIdFactory factory = new ExternalIdFactory();
        final ExternalId externalId = factory.createMavenExternalId("group", "name", "version");
        final CodeLocationNameGenerator codeLocationNameGenerator = new CodeLocationNameGenerator(null);

        final DetectCodeLocation detectCodeLocation = Mockito.mock(DetectCodeLocation.class);
        Mockito.when(detectCodeLocation.getExternalId()).thenReturn(externalId);
        Mockito.when(detectCodeLocation.getCreatorName()).thenReturn(Optional.of("NPM"));

        final File sourcePath = new File("/Users/ekerwin/Documents/source/functional/common-rest");
        final File codeLocationPath = new File("/Users/ekerwin/Documents/source/functional/common-rest/child");

        final String prefix = null;
        final String suffix = null;
        final String actual = codeLocationNameGenerator.createBomCodeLocationName(sourcePath, codeLocationPath, "projectName", "projectVersion", detectCodeLocation, prefix, suffix);

        assertEquals(expected, actual);
    }

    @Test
    public void testBomCodeLocationNameOversized() {
        final String projectNameStart = "really really ";
        final String projectName =
            projectNameStart + "really really really really really really really really really really really really really really really really really really really really really really really really really really long projectName";
        final String expected = projectName + "/projectVersion/child/group/name/version npm/bom";
        // = path/externalId tool/type

        final ExternalIdFactory factory = new ExternalIdFactory();
        final ExternalId externalId = factory.createMavenExternalId("group", "name", "version");
        final CodeLocationNameGenerator codeLocationNameGenerator = new CodeLocationNameGenerator(null);

        final DetectCodeLocation detectCodeLocation = Mockito.mock(DetectCodeLocation.class);
        Mockito.when(detectCodeLocation.getExternalId()).thenReturn(externalId);
        Mockito.when(detectCodeLocation.getCreatorName()).thenReturn(Optional.of("NPM"));

        final File sourcePath = new File("/Users/ekerwin/Documents/source/functional/common-rest");
        final File codeLocationPath = new File("/Users/ekerwin/Documents/source/functional/common-rest/child");

        final String prefix = null;
        final String suffix = null;
        final String actual = codeLocationNameGenerator.createBomCodeLocationName(sourcePath, codeLocationPath, projectName, "projectVersion", detectCodeLocation, prefix, suffix);

        assertTrue(actual.startsWith(projectNameStart));
    }

    @Test
    public void testLongCodeLocationNames() {
        final String expected = "projectName/projectVersion/common-rest-common-...n-rest-common-rest/group/name/version npm/bom";
        final ExternalIdFactory factory = new ExternalIdFactory();
        final ExternalId externalId = factory.createMavenExternalId("group", "name", "version");
        final CodeLocationNameGenerator codeLocationNameGenerator = new CodeLocationNameGenerator(null);

        final DetectCodeLocation detectCodeLocation = Mockito.mock(DetectCodeLocation.class);
        Mockito.when(detectCodeLocation.getExternalId()).thenReturn(externalId);
        Mockito.when(detectCodeLocation.getCreatorName()).thenReturn(Optional.of("NPM"));

        final File sourcePath = new File("/Users/ekerwin/Documents/source/functional/common-rest");
        final File codeLocationPath = new File(
            "/Users/ekerwin/Documents/source/functional/common-rest/common-rest-common-rest-common-rest-common-rest-common-rest-common-rest-common-rest-common-rest-common-rest-common-rest-common-rest-common-rest-common-rest-common-rest-common-rest-common-rest-common-rest-common-rest-common-rest-common-rest");
        final String prefix = "";
        final String suffix = "";
        final String actual = codeLocationNameGenerator.createBomCodeLocationName(sourcePath, codeLocationPath, "projectName", "projectVersion", detectCodeLocation, prefix, suffix);

        assertEquals(expected, actual);
    }

    @Test
    public void testExternalId() {
        final CodeLocationNameGenerator codeLocationNameGenerator = new CodeLocationNameGenerator(null);
        final DetectCodeLocation detectCodeLocation = Mockito.mock(DetectCodeLocation.class);

        final ExternalId externalId = new ExternalId(Forge.MAVEN);
        externalId.setName("externalIdName");
        externalId.setVersion("externalIdVersion");
        externalId.setGroup("externalIdGroup");
        externalId.setArchitecture("externalIdArch");
        externalId.setPath("externalIdPath");
        Mockito.when(detectCodeLocation.getExternalId()).thenReturn(externalId);

        final String actual = codeLocationNameGenerator.createBomCodeLocationName(new File("/tmp/aaa"), new File("/tmp/aaa/bbb"), "projectName", "projectVersion", detectCodeLocation, "testPrefix", "testSuffix");
        assertEquals("testPrefix/projectName/projectVersion/bbb/externalIdPath/testSuffix detect/bom", actual);
    }

    @Test
    public void testGivenNameCounters() {
        final CodeLocationNameGenerator codeLocationNameGenerator = new CodeLocationNameGenerator("myscanname");

        assertTrue(codeLocationNameGenerator.useCodeLocationOverride());

        final DetectCodeLocation detectCodeLocation = Mockito.mock(DetectCodeLocation.class);
        Mockito.when(detectCodeLocation.getCreatorName()).thenReturn(Optional.of("testCreator"));
        assertEquals("testCreator", codeLocationNameGenerator.deriveCreator(detectCodeLocation));

        assertEquals("myscanname scan", codeLocationNameGenerator.getNextCodeLocationOverrideNameUnSourced(CodeLocationNameType.SCAN));
        assertEquals("myscanname scan 2", codeLocationNameGenerator.getNextCodeLocationOverrideNameUnSourced(CodeLocationNameType.SCAN));
        assertEquals("myscanname bom", codeLocationNameGenerator.getNextCodeLocationOverrideNameUnSourced(CodeLocationNameType.BOM));
        assertEquals("myscanname bom 2", codeLocationNameGenerator.getNextCodeLocationOverrideNameUnSourced(CodeLocationNameType.BOM));

        assertEquals("myscanname testcreator/bom", codeLocationNameGenerator.getNextCodeLocationOverrideNameSourcedBom(detectCodeLocation));
        assertEquals("myscanname testcreator/bom 2", codeLocationNameGenerator.getNextCodeLocationOverrideNameSourcedBom(detectCodeLocation));
        assertEquals("myscanname testcreator/bom 3", codeLocationNameGenerator.getNextCodeLocationOverrideNameSourcedBom(detectCodeLocation));
    }
}
