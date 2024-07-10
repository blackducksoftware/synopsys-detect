repositories {
    maven {
        url 'https://sig-repo-dev.synopsys.com/bds-integration-public-cache/'
    }
}

configurations {
    airGap
}

dependencies {
    airGap 'com.synopsys.integration:integration-common:26.1.1'
}

task installDependencies(type: Copy) {
    from configurations.airGap
    include '*.jar'
    into "${gradleOutput}"
}
