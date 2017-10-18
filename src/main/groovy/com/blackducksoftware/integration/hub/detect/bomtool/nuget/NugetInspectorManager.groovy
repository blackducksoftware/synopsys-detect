package com.blackducksoftware.integration.hub.detect.bomtool.nuget

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner

import groovy.transform.TypeChecked

@Component
@TypeChecked
class NugetInspectorManager {
    private final Logger logger = LoggerFactory.getLogger(NugetInspectorManager.class)

    private String nugetInspectorExecutable
    private String inspectorVersion

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    ExecutableRunner executableRunner

    public String getNugetInspectorExecutablePath() {
        return nugetInspectorExecutable
    }

    public String getInspectorVersion(final String nugetExecutablePath) {
        if ('latest'.equalsIgnoreCase(detectConfiguration.getNugetInspectorPackageVersion())) {
            if (!inspectorVersion) {
                final def nugetOptions = [
                    'list',
                    detectConfiguration.getNugetInspectorPackageName()
                ]
                def airGapNugetInspectorDirectory = new File(detectConfiguration.getNugetInspectorAirGapPath())
                if (airGapNugetInspectorDirectory.exists()) {
                    logger.debug('Running in airgap mode. Resolving version from local path')
                    nugetOptions.addAll([
                        '-Source',
                        detectConfiguration.getNugetInspectorAirGapPath()
                    ])
                } else {
                    logger.debug('Running online. Resolving version through nuget')
                    nugetOptions.addAll([
                        '-Source',
                        detectConfiguration.getNugetPackagesRepoUrl()
                    ])
                }
                Executable getInspectorVersionExecutable = new Executable(detectConfiguration.sourceDirectory, nugetExecutablePath, nugetOptions)

                List<String> output = executableRunner.execute(getInspectorVersionExecutable).standardOutputAsList
                for (String line : output) {
                    String[] lineChunks = line.split(' ')
                    if (detectConfiguration.getNugetInspectorPackageName()?.equalsIgnoreCase(lineChunks[0])) {
                        return lineChunks[1]
                    }
                }
            }
        } else {
            inspectorVersion = detectConfiguration.getDockerInspectorVersion()
        }
        return inspectorVersion
    }

    private String installInspector(final String nugetExecutablePath, final File outputDirectory) {
        final File inspectorVersionDirectory = new File(outputDirectory, "${detectConfiguration.getNugetInspectorPackageName()}.${detectConfiguration.getNugetInspectorPackageVersion()}")
        final File toolsDirectory = new File(inspectorVersionDirectory, 'tools')
        final File inspectorExe = new File(toolsDirectory, "${detectConfiguration.getNugetInspectorPackageName()}.exe")

        final def nugetOptions = [
            'install',
            detectConfiguration.getNugetInspectorPackageName(),
            '-OutputDirectory',
            outputDirectory.getCanonicalPath()
        ]

        def airGapNugetInspectorDirectory = new File(detectConfiguration.getNugetInspectorAirGapPath())
        if (airGapNugetInspectorDirectory.exists()) {
            logger.debug('Running in airgap mode. Resolving from local path')
            nugetOptions.addAll([
                '-Source',
                detectConfiguration.getNugetInspectorAirGapPath()
            ])
        } else {
            logger.debug('Running online. Resolving through nuget')
            if (!'latest'.equalsIgnoreCase(detectConfiguration.getNugetInspectorPackageVersion())) {
                nugetOptions.addAll([
                    '-Version',
                    detectConfiguration.getNugetInspectorPackageVersion()
                ])
            }
            nugetOptions.addAll([
                '-Source',
                detectConfiguration.getNugetPackagesRepoUrl()
            ])
        }


        if (!inspectorExe.exists()) {
            Executable installInspectorExecutable = new Executable(detectConfiguration.sourceDirectory, nugetExecutablePath, nugetOptions)
            executableRunner.execute(installInspectorExecutable)
        } else {
            logger.info("Existing nuget inspector found at ${inspectorExe.getCanonicalPath()}")
        }

        if (!inspectorExe.exists()) {
            logger.warn("Could not find the ${detectConfiguration.getNugetInspectorPackageName()} version:${detectConfiguration.getNugetInspectorPackageVersion()} even after an install attempt.")
            return null
        }

        inspectorExe.getCanonicalPath()
    }
}
