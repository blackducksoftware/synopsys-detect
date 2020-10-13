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
package com.synopsys.integration.detect.configuration;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.workflow.blackduck.CustomFieldDocument;
import com.synopsys.integration.detect.workflow.blackduck.CustomFieldElement;

public class DetectCustomFieldParserTest {

    @Test
    public void parsedProject() throws DetectUserFriendlyException {
        final Map<String, String> props = new HashMap<>();
        props.put("detect.custom.fields.project[0].label", "label");
        props.put("detect.custom.fields.project[0].value", "value1, value2");
        final DetectCustomFieldParser parser = new DetectCustomFieldParser();
        final CustomFieldDocument document = parser.parseCustomFieldDocument(props);

        Assertions.assertEquals(1, document.getProject().size());

        final CustomFieldElement element = document.getProject().get(0);
        Assertions.assertEquals("label", element.getLabel());
        Assertions.assertEquals(2, element.getValue().size());
        Assertions.assertTrue(element.getValue().contains("value1"));
        Assertions.assertTrue(element.getValue().contains("value2"));
    }

    @Test
    public void parsedVersion() throws DetectUserFriendlyException {
        final Map<String, String> props = new HashMap<>();
        props.put("detect.custom.fields.version[0].label", "label");
        props.put("detect.custom.fields.version[0].value", "value1, value2");

        final DetectCustomFieldParser parser = new DetectCustomFieldParser();
        final CustomFieldDocument document = parser.parseCustomFieldDocument(props);

        Assertions.assertEquals(1, document.getVersion().size());

        final CustomFieldElement element = document.getVersion().get(0);
        Assertions.assertEquals("label", element.getLabel());
        Assertions.assertEquals(2, element.getValue().size());
        Assertions.assertTrue(element.getValue().contains("value1"));
        Assertions.assertTrue(element.getValue().contains("value2"));
    }

    @Test
    public void parsedProjectMultiple() throws DetectUserFriendlyException {
        final Map<String, String> props = new HashMap<>();
        props.put("detect.custom.fields.project[0].label", "label");
        props.put("detect.custom.fields.project[0].value", "value1");
        props.put("detect.custom.fields.project[1].label", "label");
        props.put("detect.custom.fields.project[1].value", "value1");
        props.put("detect.custom.fields.project[2].label", "label");
        props.put("detect.custom.fields.project[2].value", "value1");

        final DetectCustomFieldParser parser = new DetectCustomFieldParser();
        final CustomFieldDocument document = parser.parseCustomFieldDocument(props);

        Assertions.assertEquals(3, document.getProject().size());
    }

    @Test
    public void parsedVersionMultiple() throws DetectUserFriendlyException {
        final Map<String, String> props = new HashMap<>();
        props.put("detect.custom.fields.version[0].label", "label");
        props.put("detect.custom.fields.version[0].value", "value1");
        props.put("detect.custom.fields.version[1].label", "label");
        props.put("detect.custom.fields.version[1].value", "value1");
        props.put("detect.custom.fields.version[2].label", "label");
        props.put("detect.custom.fields.version[2].value", "value1");

        final DetectCustomFieldParser parser = new DetectCustomFieldParser();
        final CustomFieldDocument document = parser.parseCustomFieldDocument(props);

        Assertions.assertEquals(3, document.getVersion().size());
    }

    @Test
    public void parsedMissingIndexFails() {
        final Map<String, String> props = new HashMap<>();
        props.put("detect.custom.fields.version[0].label", "label");
        props.put("detect.custom.fields.version[0].value", "value1");
        props.put("detect.custom.fields.version[2].label", "label");
        props.put("detect.custom.fields.version[2].value", "value1");

        final DetectCustomFieldParser parser = new DetectCustomFieldParser();

        Assertions.assertThrows(DetectUserFriendlyException.class, () -> parser.parseCustomFieldDocument(props));
    }

    @Test
    public void parsedMissingValueStillList() throws DetectUserFriendlyException {
        final Map<String, String> props = new HashMap<>();
        props.put("detect.custom.fields.project[0].label", "label");

        final DetectCustomFieldParser parser = new DetectCustomFieldParser();
        final CustomFieldDocument document = parser.parseCustomFieldDocument(props);

        final CustomFieldElement element = document.getProject().get(0);
        Assertions.assertEquals(0, element.getValue().size());
    }

    @Test
    public void parsedMissingLabelStillList() throws DetectUserFriendlyException {
        final Map<String, String> props = new HashMap<>();
        props.put("detect.custom.fields.project[0].value", "value1");

        final DetectCustomFieldParser parser = new DetectCustomFieldParser();
        final CustomFieldDocument document = parser.parseCustomFieldDocument(props);

        final CustomFieldElement element = document.getProject().get(0);
        Assertions.assertEquals("", element.getLabel());
    }

    @Test
    public void parsedEmptyStringAsEmptyArray() throws DetectUserFriendlyException {
        final Map<String, String> props = new HashMap<>();
        props.put("detect.custom.fields.project[0].name", "example");
        props.put("detect.custom.fields.project[0].value", "");

        final DetectCustomFieldParser parser = new DetectCustomFieldParser();
        final CustomFieldDocument document = parser.parseCustomFieldDocument(props);

        final CustomFieldElement element = document.getProject().get(0);
        Assertions.assertEquals(0, element.getValue().size());
    }

    @Test
    public void parsedEmptyQuotesAsEmptyArray() throws DetectUserFriendlyException {
        final Map<String, String> props = new HashMap<>();
        props.put("detect.custom.fields.project[0].name", "example");
        props.put("detect.custom.fields.project[0].value", "\"\"");

        final DetectCustomFieldParser parser = new DetectCustomFieldParser();
        final CustomFieldDocument document = parser.parseCustomFieldDocument(props);

        final CustomFieldElement element = document.getProject().get(0);
        Assertions.assertEquals(0, element.getValue().size());
    }

    @Test
    public void parsedEmptySingleQuotesAsEmptyArray() throws DetectUserFriendlyException {
        final Map<String, String> props = new HashMap<>();
        props.put("detect.custom.fields.project[0].name", "example");
        props.put("detect.custom.fields.project[0].value", "''");

        final DetectCustomFieldParser parser = new DetectCustomFieldParser();
        final CustomFieldDocument document = parser.parseCustomFieldDocument(props);

        final CustomFieldElement element = document.getProject().get(0);
        Assertions.assertEquals(0, element.getValue().size());
    }
}
