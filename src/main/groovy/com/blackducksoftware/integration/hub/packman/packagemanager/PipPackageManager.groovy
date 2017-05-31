package com.blackducksoftware.integration.hub.packman.packagemanager

import javax.annotation.PostConstruct

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.SystemUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.PackmanProperties
import com.blackducksoftware.integration.hub.packman.packagemanager.pip.PipPackager
import com.blackducksoftware.integration.hub.packman.packagemanager.pip.PipShowMapParser
import com.blackducksoftware.integration.hub.packman.type.ExecutableType
import com.blackducksoftware.integration.hub.packman.type.PackageManagerType
import com.blackducksoftware.integration.hub.packman.util.FileFinder
import com.blackducksoftware.integration.hub.packman.util.executable.Executable
import com.blackducksoftware.integration.hub.packman.util.executable.ExecutableManager
import com.blackducksoftware.integration.hub.packman.util.executable.ExecutableOutput
import com.blackducksoftware.integration.hub.packman.util.executable.ExecutableRunner
import com.blackducksoftware.integration.hub.packman.util.executable.ExecutableRunnerException

@Component
class PipPackageManager extends PackageManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass())

    static final String SETUP_FILENAME = 'setup.py'
    static final Map<String, String> WINDOWS_ENV_VARIABLES = ["PYTHONIOENCODING":"utf8"]

    @Autowired
    PipPackager pipPackager

    @Autowired
    ExecutableManager executableManager

    @Autowired
    ExecutableRunner executableRunner

    @Autowired
    FileFinder fileFinder

    @Autowired
    PackmanProperties packmanProperties

    @Value('${packman.pip.createVirtualEnv}')
    boolean createVirtualEnv

    @Value('${packman.pip.pip3}')
    boolean pipThreeOverride

    @Value('${packman.python.path}')
    String pythonPath

    @Value('${packman.pip.path}')
    String pipPath

    ExecutableType pipExecutableType
    ExecutableType pythonExecutableType

    String pythonExecutable
    String pipExecutable
    String binFolderName
    Map<String, String> envVariables = [:]

    @PostConstruct
    void init() {
        if(pipThreeOverride) {
            pythonExecutableType = ExecutableType.PYTHON3
            pipExecutableType = ExecutableType.PIP3
        } else {
            pythonExecutableType = ExecutableType.PYTHON
            pipExecutableType = ExecutableType.PIP
        }
        pythonExecutable = findExecutable(null, pythonPath, pythonExecutableType)
        pipExecutable = findExecutable(null, pipPath, pipExecutableType)
        if(SystemUtils.IS_OS_WINDOWS) {
            binFolderName = 'Scripts'
            envVariables.putAll(WINDOWS_ENV_VARIABLES)
        } else {
            binFolderName = 'bin'
        }
    }

    PackageManagerType getPackageManagerType() {
        return PackageManagerType.PIP
    }

    boolean isPackageManagerApplicable(String sourcePath) {
        def foundExectables = pythonExecutable && pipExecutable
        def foundFiles = fileFinder.containsAllFiles(sourcePath, SETUP_FILENAME)
        foundExectables && foundFiles
    }

    List<DependencyNode> extractDependencyNodes(String sourcePath) {
        try {
            setupEnvironment(sourcePath)
            return pipPackager.makeDependencyNodes(sourcePath, pipExecutable, pythonExecutable, envVariables)
        } catch (ExecutableRunnerException e) {
            def message = 'An error occured when trying to extract python dependencies'
            logger.warn(message, e)
        }
        return null
    }

    private void setupEnvironment(String sourcePath) throws ExecutableRunnerException {
        File sourceDirectory = new File(sourcePath)
        ExecutableRunner executableRunner = new ExecutableRunner()
        Executable installVirtualenvPackage = new Executable(sourceDirectory, pipExecutable, Arrays.asList('install', 'virtualenv'))

        File virtualEnv = new File(packmanProperties.getOutputDirectoryPath(), 'blackduck_virtualenv')
        String virtualEnvBin = new File(virtualEnv, binFolderName).absolutePath

        if (createVirtualEnv) {
            executableRunner.executeLoudly(installVirtualenvPackage)
            String virtualEnvLocation = getPackageLocation(sourceDirectory, 'virtualenv')
            List<String> commandArgs = [
                "${virtualEnvLocation}/virtualenv.py",
                virtualEnv.absolutePath
            ]
            def createVirtualEnvCommand = new Executable(sourceDirectory, pythonExecutable, commandArgs)
            executableRunner.executeLoudly(createVirtualEnvCommand)
            pythonExecutable = findExecutable(virtualEnvBin, pythonPath, pythonExecutableType)
            pipExecutable = findExecutable(virtualEnvBin, pipPath, pipExecutableType)
        }
    }

    String getPackageLocation(File sourceDirectory, String packageName) throws ExecutableRunnerException {
        def showPackage = new Executable(sourceDirectory, envVariables, pipExecutable, Arrays.asList('show', packageName))
        ExecutableOutput pipShowResults = executableRunner.executeQuietly(showPackage)
        def pipShowParser = new PipShowMapParser()
        Map<String, String> map = pipShowParser.parse(pipShowResults.getStandardOutput())
        return map['Location'].trim()
    }

    private String findExecutable(String path, String executablePath, ExecutableType commandType) {
        if (StringUtils.isNotBlank(executablePath)) {
            executablePath
        } else {
            if(StringUtils.isBlank(path)){
                executableManager.getPathOfExecutable(commandType)
            } else {
                executableManager.getPathOfExecutable(path, commandType)
            }
        }
    }
}