package com.synopsys.integration.detect.configuration;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.workflow.blackduck.project.customfields.CustomFieldDocument;
import com.synopsys.integration.detect.workflow.blackduck.project.customfields.CustomFieldElement;

public class DetectCustomFieldParserTest {

    @Test
    public void parsedProject() throws DetectUserFriendlyException {
        Map<String, String> props = new HashMap<>();
        props.put("detect.custom.fields.project[0].label", "label");
        props.put("detect.custom.fields.project[0].value", "value1, value2");
        DetectCustomFieldParser parser = new DetectCustomFieldParser();
        CustomFieldDocument document = parser.parseCustomFieldDocument(props);

        Assertions.assertEquals(1, document.getProject().size());

        CustomFieldElement element = document.getProject().get(0);
        Assertions.assertEquals("label", element.getLabel());
        Assertions.assertEquals(2, element.getValue().size());
        Assertions.assertTrue(element.getValue().contains("value1"));
        Assertions.assertTrue(element.getValue().contains("value2"));
    }

    @Test
    public void parsedVersion() throws DetectUserFriendlyException {
        Map<String, String> props = new HashMap<>();
        props.put("detect.custom.fields.version[0].label", "label");
        props.put("detect.custom.fields.version[0].value", "value1, value2");

        DetectCustomFieldParser parser = new DetectCustomFieldParser();
        CustomFieldDocument document = parser.parseCustomFieldDocument(props);

        Assertions.assertEquals(1, document.getVersion().size());

        CustomFieldElement element = document.getVersion().get(0);
        Assertions.assertEquals("label", element.getLabel());
        Assertions.assertEquals(2, element.getValue().size());
        Assertions.assertTrue(element.getValue().contains("value1"));
        Assertions.assertTrue(element.getValue().contains("value2"));
    }

    @Test
    public void parsedProjectMultiple() throws DetectUserFriendlyException {
        Map<String, String> props = new HashMap<>();
        props.put("detect.custom.fields.project[0].label", "label");
        props.put("detect.custom.fields.project[0].value", "value1");
        props.put("detect.custom.fields.project[1].label", "label");
        props.put("detect.custom.fields.project[1].value", "value1");
        props.put("detect.custom.fields.project[2].label", "label");
        props.put("detect.custom.fields.project[2].value", "value1");

        DetectCustomFieldParser parser = new DetectCustomFieldParser();
        CustomFieldDocument document = parser.parseCustomFieldDocument(props);

        Assertions.assertEquals(3, document.getProject().size());
    }

    @Test
    public void parsedVersionMultiple() throws DetectUserFriendlyException {
        Map<String, String> props = new HashMap<>();
        props.put("detect.custom.fields.version[0].label", "label");
        props.put("detect.custom.fields.version[0].value", "value1");
        props.put("detect.custom.fields.version[1].label", "label");
        props.put("detect.custom.fields.version[1].value", "value1");
        props.put("detect.custom.fields.version[2].label", "label");
        props.put("detect.custom.fields.version[2].value", "value1");

        DetectCustomFieldParser parser = new DetectCustomFieldParser();
        CustomFieldDocument document = parser.parseCustomFieldDocument(props);

        Assertions.assertEquals(3, document.getVersion().size());
    }

    @Test
    public void parsedMissingIndexFails() {
        Map<String, String> props = new HashMap<>();
        props.put("detect.custom.fields.version[0].label", "label");
        props.put("detect.custom.fields.version[0].value", "value1");
        props.put("detect.custom.fields.version[2].label", "label");
        props.put("detect.custom.fields.version[2].value", "value1");

        DetectCustomFieldParser parser = new DetectCustomFieldParser();

        Assertions.assertThrows(DetectUserFriendlyException.class, () -> parser.parseCustomFieldDocument(props));
    }

    @Test
    public void parsedMissingValueStillList() throws DetectUserFriendlyException {
        Map<String, String> props = new HashMap<>();
        props.put("detect.custom.fields.project[0].label", "label");

        DetectCustomFieldParser parser = new DetectCustomFieldParser();
        CustomFieldDocument document = parser.parseCustomFieldDocument(props);

        CustomFieldElement element = document.getProject().get(0);
        Assertions.assertEquals(0, element.getValue().size());
    }

    @Test
    public void parsedMissingLabelStillList() throws DetectUserFriendlyException {
        Map<String, String> props = new HashMap<>();
        props.put("detect.custom.fields.project[0].value", "value1");

        DetectCustomFieldParser parser = new DetectCustomFieldParser();
        CustomFieldDocument document = parser.parseCustomFieldDocument(props);

        CustomFieldElement element = document.getProject().get(0);
        Assertions.assertEquals("", element.getLabel());
    }

    @Test
    public void parsedEmptyStringAsEmptyArray() throws DetectUserFriendlyException {
        Map<String, String> props = new HashMap<>();
        props.put("detect.custom.fields.project[0].name", "example");
        props.put("detect.custom.fields.project[0].value", "");

        DetectCustomFieldParser parser = new DetectCustomFieldParser();
        CustomFieldDocument document = parser.parseCustomFieldDocument(props);

        CustomFieldElement element = document.getProject().get(0);
        Assertions.assertEquals(0, element.getValue().size());
    }

    @Test
    public void parsedEmptyQuotesAsEmptyArray() throws DetectUserFriendlyException {
        Map<String, String> props = new HashMap<>();
        props.put("detect.custom.fields.project[0].name", "example");
        props.put("detect.custom.fields.project[0].value", "\"\"");

        DetectCustomFieldParser parser = new DetectCustomFieldParser();
        CustomFieldDocument document = parser.parseCustomFieldDocument(props);

        CustomFieldElement element = document.getProject().get(0);
        Assertions.assertEquals(0, element.getValue().size());
    }

    @Test
    public void parsedEmptySingleQuotesAsEmptyArray() throws DetectUserFriendlyException {
        Map<String, String> props = new HashMap<>();
        props.put("detect.custom.fields.project[0].name", "example");
        props.put("detect.custom.fields.project[0].value", "''");

        DetectCustomFieldParser parser = new DetectCustomFieldParser();
        CustomFieldDocument document = parser.parseCustomFieldDocument(props);

        CustomFieldElement element = document.getProject().get(0);
        Assertions.assertEquals(0, element.getValue().size());
    }
}
