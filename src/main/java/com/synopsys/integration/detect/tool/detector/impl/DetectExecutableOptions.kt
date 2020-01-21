package com.synopsys.integration.detect.tool.detector.impl

import java.nio.file.Path

class DetectExecutableOptions(
        val bashUserPath: Path?,
        val bazelUserPath: Path?,
        val condaUserPath: Path?,
        val cpanUserPath: Path?,
        val cpanmUserPath: Path?,
        val gradleUserPath: Path?,
        val mavenUserPath: Path?,
        val npmUserPath: Path?,
        val pearUserPath: Path?,
        val pipenvUserPath: Path?,
        val pythonUserPath: Path?,
        val rebarUserPath: Path?,
        val javaUserPath: Path?,
        val dockerUserPath: Path?,
        val dotnetUserPath: Path?,
        val gitUserPath: Path?,
        val goUserPath: Path?,
        val swiftUserPath: Path?
)