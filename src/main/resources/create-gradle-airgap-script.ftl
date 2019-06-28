repositories {
    maven {
        url 'https://repo.blackducksoftware.com:443/artifactory/bds-integration-public-cache'
    }
}

configurations {
    airGap
}

dependencies {
    airGap 'com.blackducksoftware.integration:integration-gradle-inspector:${gradleVersion}'
}

task installDependencies(type: Copy) {
    from configurations.airGap
    include '*.jar'
    into "${gradleOutput}"
}