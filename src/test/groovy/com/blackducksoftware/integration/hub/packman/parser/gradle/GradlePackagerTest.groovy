package com.blackducksoftware.integration.hub.packman.parser.gradle

import org.junit.Test

class GradlePackagerTest {
    @Test
    public void testGradlePackager() {
        def sourcePath = '/Users/ekerwin/Documents/source/rest-backend/registration/registration.core'
        def gradlePackager = new GradlePackager(sourcePath)
        def dependencyNodes = gradlePackager.makeDependencyNodes()
        dependencyNodes.each { println "${it.name}/${it.version}: ${it.externalId.createDataId()}: ${it.externalId.createExternalId()}" }
    }
}
