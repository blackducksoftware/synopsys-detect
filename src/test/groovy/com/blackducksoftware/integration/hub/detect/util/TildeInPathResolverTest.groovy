package com.blackducksoftware.integration.hub.detect.util

import org.junit.Assert
import org.junit.Test

import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.type.OperatingSystemType

class TildeInPathResolverTest {
    @Test
    void testResolvingTilde() {
        DetectConfiguration detectConfiguration = new DetectConfiguration()
        detectConfiguration.sourcePath = '~/Documents/source/integration/hub-detect'

        TildeInPathResolver resolver = new TildeInPathResolver()
        resolver.resolveTildeInAllPathFields(OperatingSystemType.LINUX, '/Users/ekerwin', detectConfiguration)

        Assert.assertEquals('/Users/ekerwin/Documents/source/integration/hub-detect', detectConfiguration.sourcePath)
    }

    @Test
    void testResolvingTildeInWindows() {
        DetectConfiguration detectConfiguration = new DetectConfiguration()
        detectConfiguration.sourcePath = '~/Documents/source/integration/hub-detect'

        TildeInPathResolver resolver = new TildeInPathResolver()
        resolver.resolveTildeInAllPathFields(OperatingSystemType.WINDOWS, '/Users/ekerwin', detectConfiguration)

        Assert.assertEquals('~/Documents/source/integration/hub-detect', detectConfiguration.sourcePath)
    }

    @Test
    void testResolvingTildeInTheMiddleOfAPath() {
        DetectConfiguration detectConfiguration = new DetectConfiguration()
        detectConfiguration.sourcePath = '/Documents/~source/~/integration/hub-detect'

        TildeInPathResolver resolver = new TildeInPathResolver()
        resolver.resolveTildeInAllPathFields(OperatingSystemType.LINUX, '/Users/ekerwin', detectConfiguration)

        Assert.assertEquals('/Documents/~source/~/integration/hub-detect', detectConfiguration.sourcePath)
    }
}
