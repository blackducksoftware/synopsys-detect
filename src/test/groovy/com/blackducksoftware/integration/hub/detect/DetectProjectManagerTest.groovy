package com.blackducksoftware.integration.hub.detect

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

import org.junit.Assert
import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.manager.DetectProjectManager

@Ignore
public class DetectProjectManagerTest {
    private ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    @Test
    void testGettingDefaultProjectVersions() {
        def detectConfiguration = new DetectConfiguration()
        detectConfiguration.projectVersionName = null
        detectConfiguration.defaultProjectVersionScheme = 'timestamp'
        detectConfiguration.defaultProjectVersionTimeformat = 'yyyy-MM-dd\'T\'HH:mm:ss.SSS'
        detectConfiguration.defaultProjectVersionText = 'default'

        def detectProjectManager = new DetectProjectManager()
        detectProjectManager.detectConfiguration = detectConfiguration

        String timeString = DateTimeFormatter.ofPattern('yyyy-MM-dd\'T\'HH:mm:ss.SSS').withZone(ZoneOffset.UTC).format(Instant.now().atZone(ZoneOffset.UTC))
        String defaultTimestampVersion = detectProjectManager.getProjectVersionName(null)
        Assert.assertTrue(defaultTimestampVersion.startsWith(timeString[0..10]))

        detectConfiguration.defaultProjectVersionScheme = 'text'
        String defaultTextVersion = detectProjectManager.getProjectVersionName(detectConfiguration.defaultProjectVersionText)
        Assert.assertEquals('default', defaultTextVersion)

        detectConfiguration.projectVersionName = 'actual'
        String version = detectProjectManager.getProjectVersionName(detectConfiguration.defaultProjectVersionText)
        Assert.assertEquals('actual', version)
    }
}
