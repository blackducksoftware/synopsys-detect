package com.blackducksoftware.integration.hub.detect.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;

import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigWrapper;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.type.OperatingSystemType;

public class TildeInPathResolverTest {
    @Test
    public void testResolvingTilde() throws IllegalArgumentException, IllegalAccessException {
        final DetectConfigWrapper detectConfigWrapper = new DetectConfigWrapper(null);
        detectConfigWrapper.setDetectProperty(DetectProperty.DETECT_SOURCE_PATH, "~/Documents/source/integration/hub-detect");

        final DetectInfo detectInfo = mock(DetectInfo.class);
        when(detectInfo.getCurrentOs()).thenReturn(OperatingSystemType.LINUX);

        final TildeInPathResolver resolver = new TildeInPathResolver(detectInfo);

        resolver.resolveTildeInAllPathFields("/Users/ekerwin", detectConfigWrapper);

        Assert.assertEquals("/Users/ekerwin/Documents/source/integration/hub-detect", detectConfigWrapper.getProperty(DetectProperty.DETECT_SOURCE_PATH));
    }

    @Test
    public void testResolvingTildeInWindows() throws IllegalArgumentException, IllegalAccessException {
        final DetectConfigWrapper detectConfigWrapper = new DetectConfigWrapper(null);
        detectConfigWrapper.setDetectProperty(DetectProperty.DETECT_SOURCE_PATH, "~/Documents/source/integration/hub-detect");

        final DetectInfo detectInfo = mock(DetectInfo.class);
        when(detectInfo.getCurrentOs()).thenReturn(OperatingSystemType.WINDOWS);

        final TildeInPathResolver resolver = new TildeInPathResolver(detectInfo);

        resolver.resolveTildeInAllPathFields("/Users/ekerwin", detectConfigWrapper);

        Assert.assertEquals("~/Documents/source/integration/hub-detect", detectConfigWrapper.getProperty(DetectProperty.DETECT_SOURCE_PATH));
    }

    @Test
    public void testResolvingTildeInTheMiddleOfAPath() throws IllegalArgumentException, IllegalAccessException {
        final DetectConfigWrapper detectConfigWrapper = new DetectConfigWrapper(null);
        detectConfigWrapper.setDetectProperty(DetectProperty.DETECT_SOURCE_PATH, "/Documents/~source/~/integration/hub-detect");

        final DetectInfo detectInfo = mock(DetectInfo.class);
        when(detectInfo.getCurrentOs()).thenReturn(OperatingSystemType.LINUX);

        final TildeInPathResolver resolver = new TildeInPathResolver(detectInfo);

        resolver.resolveTildeInAllPathFields("/Users/ekerwin", detectConfigWrapper);

        Assert.assertEquals("/Documents/~source/~/integration/hub-detect", detectConfigWrapper.getProperty(DetectProperty.DETECT_SOURCE_PATH));
    }
}
