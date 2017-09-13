package com.blackducksoftware.integration.hub.detect

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.junit.Assert
import org.junit.Test

public class DetectProjectManagerTest {
    @Test
    void testGettingDefaultProjectVersions() {
        def detectProperties = new DetectProperties()
        detectProperties.projectVersionName = null
        detectProperties.defaultProjectVersionScheme = 'timestamp'
        detectProperties.defaultProjectVersionTimeformat = 'yyyy-MM-dd\'T\'HH:mm:ss.SSS'
        detectProperties.defaultProjectVersionText = 'default'

        def detectConfiguration = new DetectConfiguration()
        detectConfiguration.detectProperties = detectProperties

        def detectProjectManager = new DetectProjectManager()
        detectProjectManager.detectConfiguration = detectConfiguration

        String timeString = DateTimeFormat.forPattern('yyyy-MM-dd\'T\'HH:mm:ss.SSS').withZoneUTC().print(DateTime.now().withZone(DateTimeZone.UTC))
        String defaultTimestampVersion = detectProjectManager.getDefaultProjectVersionName()
        Assert.assertTrue(defaultTimestampVersion.startsWith(timeString[0..10]))

        detectProperties.defaultProjectVersionScheme = 'text'
        String defaultTextVersion = detectProjectManager.getDefaultProjectVersionName()
        Assert.assertEquals('default', defaultTextVersion)

        detectProperties.projectVersionName = 'actual'
        String version = detectProjectManager.getDefaultProjectVersionName()
        Assert.assertEquals('actual', version)
    }
}
