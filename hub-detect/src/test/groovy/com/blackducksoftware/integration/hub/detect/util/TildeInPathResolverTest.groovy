package com.blackducksoftware.integration.hub.detect.util

import com.blackducksoftware.integration.hub.detect.DetectInfo
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigWrapper
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty
import com.blackducksoftware.integration.hub.detect.type.OperatingSystemType
import org.junit.Assert
import org.junit.Test

class TildeInPathResolverTest {
    @Test
    void testResolvingTilde() {
        DetectConfigWrapper detectConfigWrapper = new DetectConfigWrapper(null)
        detectConfigWrapper.setDetectProperty(DetectProperty.DETECT_SOURCE_PATH, '~/Documents/source/integration/hub-detect')

        DetectInfo detectInfo = new DetectInfo(null)
        detectInfo.currentOs = OperatingSystemType.LINUX
        TildeInPathResolver resolver = new TildeInPathResolver(detectInfo)

        resolver.resolveTildeInAllPathFields('/Users/ekerwin', detectConfigWrapper)

        Assert.assertEquals('/Users/ekerwin/Documents/source/integration/hub-detect', detectConfigWrapper.getProperty(DetectProperty.DETECT_SOURCE_PATH))
    }

    @Test
    void testResolvingTildeInWindows() {
        DetectConfigWrapper detectConfigWrapper = new DetectConfigWrapper(null)
        detectConfigWrapper.setDetectProperty(DetectProperty.DETECT_SOURCE_PATH, '~/Documents/source/integration/hub-detect')

        DetectInfo detectInfo = new DetectInfo(null)
        detectInfo.currentOs = OperatingSystemType.WINDOWS
        println detectInfo.getCurrentOs()
        TildeInPathResolver resolver = new TildeInPathResolver(detectInfo)

        resolver.resolveTildeInAllPathFields('/Users/ekerwin', detectConfigWrapper)

        Assert.assertEquals('~/Documents/source/integration/hub-detect', detectConfigWrapper.getProperty(DetectProperty.DETECT_SOURCE_PATH))
    }

    @Test
    void testResolvingTildeInTheMiddleOfAPath() {
        DetectConfigWrapper detectConfigWrapper = new DetectConfigWrapper(null)
        detectConfigWrapper.setDetectProperty(DetectProperty.DETECT_SOURCE_PATH, '/Documents/~source/~/integration/hub-detect')

        DetectInfo detectInfo = new DetectInfo(null)
        detectInfo.currentOs = OperatingSystemType.LINUX
        TildeInPathResolver resolver = new TildeInPathResolver(detectInfo)

        resolver.resolveTildeInAllPathFields('/Users/ekerwin', detectConfigWrapper)

        Assert.assertEquals('/Documents/~source/~/integration/hub-detect', detectConfigWrapper.getProperty(DetectProperty.DETECT_SOURCE_PATH))
    }
}
