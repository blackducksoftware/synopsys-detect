package com.synopsys.integration.detect.interactive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.configuration.source.MapPropertySource;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;

public class InteractiveModeDecisionTreeTest {
    @Test
    public void testTraverse() {
        InteractiveModeDecisionTree decisionTree = new InteractiveModeDecisionTree(new ArrayList<>());

        InteractiveWriter mockWriter = Mockito.mock(InteractiveWriter.class);
        InteractivePropertySourceBuilder propertySourceBuilder = new InteractivePropertySourceBuilder(mockWriter);

        decisionTree.traverse(propertySourceBuilder, mockWriter);

        MapPropertySource actualPropertySource = propertySourceBuilder.build();

        assertEquals(2, actualPropertySource.getKeys().size());

        String detectToolsExcludedKey = DetectProperties.DETECT_TOOLS_EXCLUDED.getProperty().getKey();
        assertTrue(actualPropertySource.hasKey(detectToolsExcludedKey));
        assertEquals(actualPropertySource.getValue(detectToolsExcludedKey), DetectTool.SIGNATURE_SCAN.toString());

        String blackduckOfflineModeKey = DetectProperties.BLACKDUCK_OFFLINE_MODE.getProperty().getKey();
        assertTrue(actualPropertySource.hasKey(blackduckOfflineModeKey));
        assertEquals(actualPropertySource.getValue(blackduckOfflineModeKey), Boolean.TRUE.toString());
    }

}
