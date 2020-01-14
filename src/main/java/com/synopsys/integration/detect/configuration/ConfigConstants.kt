package com.synopsys.integration.detect.configuration

import com.synopsys.integration.configuration.property.Category
import com.synopsys.integration.configuration.property.Group
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.memberProperties

//TODO: Back to MAIN.
class DetectCategory(name: String) : Category(name) {
    companion object {
        val Advanced = DetectCategory("advanced")
        val Simple = DetectCategory("simple")
    }
}

class DetectGroup(name: String, superGroup: DetectGroup? = null) : Group(name, superGroup) {
    companion object {
        //Super Groups
        val Detectors = DetectGroup("Detectors")

        //Recommended Primary Groups
        val Artifactory = DetectGroup("artifactory")
        val BlackduckServer = DetectGroup("blackduck server")
        val Cleanup = DetectGroup("cleanup")
        val CodeLocation = DetectGroup("codelocation")
        val General = DetectGroup("general")
        val Logging = DetectGroup("logging")
        val Paths = DetectGroup("paths")
        val PolicyCheck = DetectGroup("policy check")
        val Project = DetectGroup("project")
        val ProjectInfo = DetectGroup("project info")
        val Proxy = DetectGroup("proxy")
        val Report = DetectGroup("report")
        val SourceScan = DetectGroup("source scan")
        val SourcePath = DetectGroup("source path")

        //Tool Groups
        val Detector = DetectGroup("detector")
        val Polaris = DetectGroup("polaris")
        val SignatureScanner = DetectGroup("signature scanner")

        //Detector Groups
        val Bazel = DetectGroup("bazel", Detectors)
        val Bitbake = DetectGroup("bitbake", Detectors)
        val Conda = DetectGroup("conda", Detectors)
        val Cpan = DetectGroup("cpan", Detectors)
        val Docker = DetectGroup("docker", Detectors)
        val Go = DetectGroup("go", Detectors)
        val Gradle = DetectGroup("gradle", Detectors)
        val Hex = DetectGroup("hex", Detectors)
        val Maven = DetectGroup("maven", Detectors)
        val Npm = DetectGroup("npm", Detectors)
        val Nuget = DetectGroup("nuget", Detectors)
        val Packagist = DetectGroup("packagist", Detectors)
        val Pear = DetectGroup("pear", Detectors)
        val Pip = DetectGroup("pip", Detectors)
        val Python = DetectGroup("python", Detectors)
        val Ruby = DetectGroup("ruby", Detectors)
        val Sbt = DetectGroup("sbt", Detectors)
        val Yarn = DetectGroup("yarn", Detectors)

        //Additional groups (should not be used as a primary group
        val Blackduck = DetectGroup("blackduck")
        val Debug = DetectGroup("debug")
        val Global = DetectGroup("global")
        val Offline = DetectGroup("offline")
        val Policy = DetectGroup("policy")
        val ProjectSetting = DetectGroup("project setting")
        val ReportSetting = DetectGroup("report setting")
        val Search = DetectGroup("search")
        val Default = DetectGroup("default");

        fun values(): List<DetectGroup> {
            val clazz = DetectGroup::class
            val companionClass = clazz.companionObject!!
            val companion = clazz.companionObjectInstance!!
            val members = mutableListOf<DetectGroup>()
            for (member in companionClass.memberProperties) {
                when (val value = member.getter.call(companion)) {
                    is DetectGroup -> members.add(value)
                }
            }
            return members;
        }

        fun map(): Map<String, String> {
            val clazz = DetectGroup::class
            val companionClass = clazz.companionObject!!
            val companion = clazz.companionObjectInstance!!
            val members = mutableMapOf<String, String>()
            for (member in companionClass.memberProperties) {
                when (val value = member.getter.call(companion)) {
                    is DetectGroup -> members[value.name] = member.name
                }
            }
            return members;
        }
    }
}