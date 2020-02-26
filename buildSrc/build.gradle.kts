buildscript {
    repositories {
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
    }
    dependencies {
        "classpath"(group = "com.synopsys.integration", name = "common-gradle-plugin", version = "1.2.3")
    }
}

plugins {
    `kotlin-dsl`
}

version = "1.0.0-SNAPSHOT"
apply(plugin = "com.synopsys.integration.simple")

repositories {
    mavenCentral()
    maven(url = "https://plugins.gradle.org/m2/")
}

dependencies {
    implementation(gradleApi())
    implementation("org.freemarker:freemarker:2.3.26-incubating")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("commons-io:commons-io:2.6")
    implementation("org.apache.commons:commons-lang3:3.0")
    implementation("com.synopsys.integration:integration-common:20.0.0")
    implementation("com.synopsys.integration:common-gradle-plugin:1.2.3")
}