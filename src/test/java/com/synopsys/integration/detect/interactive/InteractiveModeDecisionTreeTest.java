package com.synopsys.integration.detect.interactive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.configuration.source.MapPropertySource;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;

public class InteractiveModeDecisionTreeTest {
    public static String EXPECTED_BLACKDUCK_SERVER_URL = "https://example.com/blackduck";
    public static String EXPECTED_BLACKDUCK_API_TOKEN = "Api Token";
    public static String EXPECTED_PROXY_HOST = "proxy.example.com";
    public static String EXPECTED_PROXY_PORT = "1234";
    public static String EXPECTED_PROXY_USERNAME = "proxyUser";
    public static String EXPECTED_PROXY_PASSWORD = "proxyPassword";
    public static String EXPECTED_PROXY_NTLM_DOMAIN = "proxyNtlmDomain";
    public static String EXPECTED_PROXY_NTLM_WORKSTATION = "proxyNtlmWorkstation";

    private static Stream<Arguments> getTraversalData() {
        return Stream.of(
            Arguments.of(
                Bds.mapOf(
                    Pair.of(InteractiveModeDecisionTree.SHOULD_CONNECT_TO_BLACKDUCK, Boolean.FALSE.toString()),
                    Pair.of(InteractiveModeDecisionTree.SHOULD_RUN_CLI_SCAN, Boolean.FALSE.toString()),
                    Pair.of(InteractiveModeDecisionTree.SHOULD_SAVE_TO_APPLICATION_PROPERTIES, Boolean.FALSE.toString())
                ),
                Bds.mapOf(
                    Pair.of(DetectProperties.DETECT_TOOLS_EXCLUDED, DetectTool.SIGNATURE_SCAN.toString()),
                    Pair.of(DetectProperties.BLACKDUCK_OFFLINE_MODE, Boolean.TRUE.toString())
                )
            ),
            Arguments.of(
                Bds.mapOf(
                    Pair.of(InteractiveModeDecisionTree.SHOULD_CONNECT_TO_BLACKDUCK, Boolean.TRUE.toString()),
                    Pair.of(BlackDuckServerDecisionBranch.SET_BLACKDUCK_SERVER_URL, EXPECTED_BLACKDUCK_SERVER_URL),
                    Pair.of(BlackDuckServerDecisionBranch.SHOULD_USE_API_TOKEN, Boolean.TRUE.toString()),
                    Pair.of(BlackDuckServerDecisionBranch.SET_API_TOKEN, EXPECTED_BLACKDUCK_API_TOKEN),
                    Pair.of(BlackDuckServerDecisionBranch.SHOULD_TRUST_CERTS, Boolean.TRUE.toString()),
                    Pair.of(BlackDuckServerDecisionBranch.SHOULD_CONFIGURE_PROXY, Boolean.TRUE.toString()),
                    Pair.of(BlackDuckServerDecisionBranch.SET_PROXY_HOST, EXPECTED_PROXY_HOST),
                    Pair.of(BlackDuckServerDecisionBranch.SET_PROXY_PORT, EXPECTED_PROXY_PORT),
                    Pair.of(BlackDuckServerDecisionBranch.SET_PROXY_USERNAME, EXPECTED_PROXY_USERNAME),
                    Pair.of(BlackDuckServerDecisionBranch.SHOULD_SET_PROXY_PASSWORD, Boolean.TRUE.toString()),
                    Pair.of(BlackDuckServerDecisionBranch.SET_PROXY_PASSWORD, EXPECTED_PROXY_PASSWORD),
                    Pair.of(BlackDuckServerDecisionBranch.SHOULD_SET_PROXY_NTLM, Boolean.TRUE.toString()),
                    Pair.of(BlackDuckServerDecisionBranch.SET_PROXY_NTLM_DOMAIN, EXPECTED_PROXY_NTLM_DOMAIN),
                    Pair.of(BlackDuckServerDecisionBranch.SET_PROXY_NTLM_WORKSTATION, EXPECTED_PROXY_NTLM_WORKSTATION),
                    Pair.of(BlackDuckConnectionDecisionBranch.SHOULD_TEST_CONNECTION, Boolean.FALSE.toString()),
                    Pair.of(InteractiveModeDecisionTree.SHOULD_RUN_CLI_SCAN, Boolean.FALSE.toString()),
                    Pair.of(InteractiveModeDecisionTree.SHOULD_SAVE_TO_APPLICATION_PROPERTIES, Boolean.FALSE.toString())
                ),
                Bds.mapOf(
                    Pair.of(DetectProperties.BLACKDUCK_URL, EXPECTED_BLACKDUCK_SERVER_URL),
                    Pair.of(DetectProperties.BLACKDUCK_API_TOKEN, EXPECTED_BLACKDUCK_API_TOKEN),
                    Pair.of(DetectProperties.BLACKDUCK_TRUST_CERT, Boolean.TRUE.toString()),
                    Pair.of(DetectProperties.BLACKDUCK_PROXY_HOST, EXPECTED_PROXY_HOST),
                    Pair.of(DetectProperties.BLACKDUCK_PROXY_PORT, EXPECTED_PROXY_PORT),
                    Pair.of(DetectProperties.BLACKDUCK_PROXY_USERNAME, EXPECTED_PROXY_USERNAME),
                    Pair.of(DetectProperties.BLACKDUCK_PROXY_PASSWORD, EXPECTED_PROXY_PASSWORD),
                    Pair.of(DetectProperties.BLACKDUCK_PROXY_NTLM_DOMAIN, EXPECTED_PROXY_NTLM_DOMAIN),
                    Pair.of(DetectProperties.BLACKDUCK_PROXY_NTLM_WORKSTATION, EXPECTED_PROXY_NTLM_WORKSTATION),
                    Pair.of(DetectProperties.DETECT_TOOLS_EXCLUDED, DetectTool.SIGNATURE_SCAN.toString())
                )
            )
        );
    }

    @ParameterizedTest
    @MethodSource("getTraversalData")
    public void testTraverse(Map<String, String> callToResponse, Map<DetectProperty<?>, String> expectedProperties) {
        InteractiveModeDecisionTree decisionTree = new InteractiveModeDecisionTree(new ArrayList<>());

        InteractiveWriter mockWriter = mockWriter(callToResponse);
        InteractivePropertySourceBuilder propertySourceBuilder = new InteractivePropertySourceBuilder(mockWriter);

        decisionTree.traverse(propertySourceBuilder, mockWriter);

        MapPropertySource actualPropertySource = propertySourceBuilder.build();

        assertEquals(expectedProperties.keySet().size(), actualPropertySource.getKeys().size());

        expectedProperties.forEach((key, value) -> assertHasPropertyWithValue(actualPropertySource, key, value));
    }

    private InteractiveWriter mockWriter(Map<String, String> callToResponse) {
        InteractiveWriter mockWriter = Mockito.mock(InteractiveWriter.class);

        // All askYesOrNo call askYesOrNoWithMessage, no sense in stubbing out both -- rotte OCT 2020
        Mockito.when(mockWriter.askYesOrNo(Mockito.any())).thenCallRealMethod();

        callToResponse.forEach((k, v) -> {
            Mockito.when(mockWriter.askYesOrNoWithMessage(Mockito.contains(k), Mockito.any())).thenReturn(Boolean.valueOf(v));
            Mockito.when(mockWriter.askQuestion(k)).thenReturn(v);
            Mockito.when(mockWriter.askSecretQuestion(k)).thenReturn(v);
        });

        return mockWriter;
    }

    private void assertHasPropertyWithValue(MapPropertySource mapPropertySource, DetectProperty<?> detectProperty, String value) {
        String detectPropertyKey = detectProperty.getProperty().getKey();
        assertTrue(mapPropertySource.hasKey(detectPropertyKey));
        assertEquals(mapPropertySource.getValue(detectPropertyKey), value);
    }

}
