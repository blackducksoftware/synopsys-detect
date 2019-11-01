plugins {
    `kotlin-dsl`
}
// Required since Gradle 4.10+.
repositories {
    jcenter()
}

dependencies {
    implementation(gradleApi())
    implementation("org.freemarker:freemarker:2.3.26-incubating")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("commons-io:commons-io:2.6")
    implementation("org.apache.commons:commons-lang3:3.0")
}