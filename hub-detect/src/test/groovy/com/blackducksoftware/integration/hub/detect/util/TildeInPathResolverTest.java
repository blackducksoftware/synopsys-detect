package com.blackducksoftware.integration.hub.detect.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.Test;
import org.mockito.Mockito;

import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.type.OperatingSystemType;

public class TildeInPathResolverTest {
    @Test
    public void testResolvingTilde() throws IllegalArgumentException, IllegalAccessException {
        final DetectConfiguration detectConfiguration = mockConfiguration("~/Documents/source/integration/hub-detect");

        final DetectInfo detectInfo = mock(DetectInfo.class);
        when(detectInfo.getCurrentOs()).thenReturn(OperatingSystemType.LINUX);

        final TildeInPathResolver resolver = new TildeInPathResolver(detectInfo);

        resolver.resolveTildeInAllPathFields("/Users/ekerwin", detectConfiguration);

        Mockito.verify(detectConfiguration).setDetectProperty(DetectProperty.DETECT_SOURCE_PATH, "/Users/ekerwin/Documents/source/integration/hub-detect");
    }

    @Test
    public void testResolvingTildeInWindows() throws IllegalArgumentException, IllegalAccessException {
        final DetectConfiguration detectConfiguration = mockConfiguration("~/Documents/source/integration/hub-detect");

        final DetectInfo detectInfo = mock(DetectInfo.class);
        when(detectInfo.getCurrentOs()).thenReturn(OperatingSystemType.WINDOWS);

        final TildeInPathResolver resolver = new TildeInPathResolver(detectInfo);

        resolver.resolveTildeInAllPathFields("/Users/ekerwin", detectConfiguration);

        Mockito.verify(detectConfiguration, Mockito.never()).setDetectProperty(Mockito.any(), Mockito.any());
    }

    @Test
    public void testResolvingTildeInTheMiddleOfAPath() throws IllegalArgumentException, IllegalAccessException {
        final DetectConfiguration detectConfiguration = mockConfiguration("/Documents/~source/~/integration/hub-detect");

        final DetectInfo detectInfo = mock(DetectInfo.class);
        when(detectInfo.getCurrentOs()).thenReturn(OperatingSystemType.LINUX);

        final TildeInPathResolver resolver = new TildeInPathResolver(detectInfo);

        resolver.resolveTildeInAllPathFields("/Users/ekerwin", detectConfiguration);

        Mockito.verify(detectConfiguration, Mockito.never()).setDetectProperty(Mockito.any(), Mockito.any());
    }

    private DetectConfiguration mockConfiguration(final String path) {
        final HashMap<DetectProperty, Object> propertyMap = new HashMap<>();
        propertyMap.put(DetectProperty.DETECT_SOURCE_PATH, path);

        final DetectConfiguration detectConfiguration = Mockito.mock(DetectConfiguration.class);
        Mockito.when(detectConfiguration.getPropertyMap()).thenReturn(propertyMap);

        return detectConfiguration;
    }
}
