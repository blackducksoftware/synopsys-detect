package com.blackducksoftware.integration.hub.detect

import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.junit.Assert
import org.junit.Test

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

        String timeString = DateTimeFormat.forPattern('yyyy-MM-dd\'T\'HH:mm:ss.SSS').withZoneUTC().print(DateTime.now().withZone(DateTimeZone.UTC))
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
