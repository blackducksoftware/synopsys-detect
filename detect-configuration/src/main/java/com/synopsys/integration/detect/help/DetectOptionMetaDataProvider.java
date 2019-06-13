package com.synopsys.integration.detect.help;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import com.synopsys.integration.util.ResourceUtil;

public class DetectOptionMetaDataProvider {
    public Map<String, DetectOptionMetaData> loadMetaDataFromYaml() {
        Map<String, DetectOptionMetaData> metaData = new HashMap<>();
        try {
            String metaDataText = ResourceUtil.getResourceAsString(this.getClass(), "/detect-properties.yaml", StandardCharsets.UTF_8.toString());
            Yaml yaml = new Yaml();
            Map<String, Object> obj = yaml.load(metaDataText);
            for (String propertyKey : obj.keySet()) {
                Map<String, Object> propertyMetaData = (Map<String, Object>) obj.get(propertyKey);
                DetectOptionMetaData optionMetaData = new DetectOptionMetaData();
                optionMetaData.fromVersion = (String) propertyMetaData.get("fromVersion");
                optionMetaData.help = (String) propertyMetaData.get("help");
                optionMetaData.helpDetailed = (String) propertyMetaData.get("helpDetailed");
                optionMetaData.name = (String) propertyMetaData.get("name");
                optionMetaData.primaryGroup = (String) propertyMetaData.get("primaryGroup");
                optionMetaData.additionalGroups = (List<String>) propertyMetaData.get("additionalGroups");

                if (optionMetaData.additionalGroups.size() == 0) {
                    if (StringUtils.isNotBlank(optionMetaData.primaryGroup)) {
                        optionMetaData.additionalGroups.add(optionMetaData.primaryGroup);
                    }
                }

                metaData.put(propertyKey, optionMetaData);
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return metaData;
    }
}
