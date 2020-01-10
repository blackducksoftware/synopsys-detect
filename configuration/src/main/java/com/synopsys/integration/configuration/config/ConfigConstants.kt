package com.synopsys.integration.configuration.config

import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.memberProperties

class Category(val name: String) {
    companion object {
        val Advanced = Category("advanced")
        val Simple = Category("simple")
    }
}

class Group(val name: String, val superGroup: Group? = null) {
    companion object {
        //Super Groups
        val Detectors = Group("Detectors")

        //Recommended Primary Groups
        val Artifactory = Group("artifactory")
        val BlackduckServer = Group("blackduck server")
        val Cleanup = Group("cleanup")
        val CodeLocation = Group("codelocation")
        val General = Group("general")
        val Logging = Group("logging")
        val Paths = Group("paths")
        val PolicyCheck = Group("policy check")
        val Project = Group("project")
        val ProjectInfo = Group("project info")
        val Proxy = Group("proxy")
        val Report = Group("report")
        val SourceScan = Group("source scan")
        val SourcePath = Group("source path")

        //Tool Groups
        val Detector = Group("detector")
        val Polaris = Group("polaris")
        val SignatureScanner = Group("signature scanner")

        //Detector Groups
        val Bazel = Group("bazel", Detectors)
        val Bitbake = Group("bitbake", Detectors)
        val Conda = Group("conda", Detectors)
        val Cpan = Group("cpan", Detectors)
        val Docker = Group("docker", Detectors)
        val Go = Group("go", Detectors)
        val Gradle = Group("gradle", Detectors)
        val Hex = Group("hex", Detectors)
        val Maven = Group("maven", Detectors)
        val Npm = Group("npm", Detectors)
        val Nuget = Group("nuget", Detectors)
        val Packagist = Group("packagist", Detectors)
        val Pear = Group("pear", Detectors)
        val Pip = Group("pip", Detectors)
        val Python = Group("python", Detectors)
        val Ruby = Group("ruby", Detectors)
        val Sbt = Group("sbt", Detectors)
        val Yarn = Group("yarn", Detectors)

        //Additional groups (should not be used as a primary group
        val Blackduck = Group("blackduck")
        val Debug = Group("debug")
        val Global = Group("global")
        val Offline = Group("offline")
        val Policy = Group("policy")
        val ProjectSetting = Group("project setting")
        val ReportSetting = Group("report setting")
        val Search = Group("search")
        val Default = Group("default");

        fun values(): List<Group> {
            val clazz = Group::class
            val companionClass = clazz.companionObject!!
            val companion = clazz.companionObjectInstance!!
            val members = mutableListOf<Group>()
            for (member in companionClass.memberProperties) {
                when (val value = member.getter.call(companion)) {
                    is Group -> members.add(value)
                }
            }
            return members;
        }

        fun map(): Map<String, String> {
            val clazz = Group::class
            val companionClass = clazz.companionObject!!
            val companion = clazz.companionObjectInstance!!
            val members = mutableMapOf<String, String>()
            for (member in companionClass.memberProperties) {
                when (val value = member.getter.call(companion)) {
                    is Group -> members[value.name] = member.name
                }
            }
            return members;
        }
    }
}