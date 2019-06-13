package com.synopsys.integration.detect.help;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.synopsys.integration.detect.configuration.DetectProperty;

public class YamlPropertyTest {
    @Test
    public void YamlMatchesPropertyEnum() {
        DetectOptionMetaDataProvider metaDataProvider = new DetectOptionMetaDataProvider();
        Map<String, DetectOptionMetaData> metaData = metaDataProvider.loadMetaDataFromYaml();

        Assert.assertEquals("Yaml meta data must have exactly one entry for every detect property.", metaData.size(), DetectProperty.values().length);

        for (DetectProperty property : DetectProperty.values()) {
            Assert.assertTrue("Yaml meta data was missing a detect property key: " + property.getPropertyKey(), metaData.containsKey(property.getPropertyKey()));

            DetectOptionMetaData optionMetaData = metaData.get(property.getPropertyKey());

            Assert.assertNotNull("Yaml entry " + property.getPropertyKey() + " missing help!", optionMetaData.help);
            Assert.assertNotNull("Yaml entry " + property.getPropertyKey() + " missing helpDetailed!", optionMetaData.helpDetailed);
            Assert.assertNotNull("Yaml entry " + property.getPropertyKey() + " missing fromVersion!", optionMetaData.fromVersion);
            Assert.assertNotNull("Yaml entry " + property.getPropertyKey() + " missing name!", optionMetaData.name);
            Assert.assertNotNull("Yaml entry " + property.getPropertyKey() + " missing additionalGroups!", optionMetaData.additionalGroups);
            Assert.assertNotNull("Yaml entry " + property.getPropertyKey() + " missing primaryGroup!", optionMetaData.primaryGroup);
        }
    }
}
