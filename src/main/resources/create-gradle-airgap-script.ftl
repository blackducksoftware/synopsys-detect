String repoUrl = "https://repo.blackduck.com/bds-integration-public-cache/"

try {
    new URL(repoUrl).text
} catch (Exception e) {
    repoUrl = "https://sig-repo.synopsys.com/bds-integration-public-cache/"
}

repositories {
    maven {
        url repoUrl
    }
}

configurations {
    airGap
}

dependencies {
    airGap 'com.synopsys.integration:integration-common:26.0.6'
}

task installDependencies(type: Copy) {
    from configurations.airGap
    include '*.jar'
    into "${gradleOutput}"
}
