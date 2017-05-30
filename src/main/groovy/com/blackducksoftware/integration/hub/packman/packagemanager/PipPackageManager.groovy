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
import com.blackducksoftware.integration.hub.packman.util.command.Executable
import com.blackducksoftware.integration.hub.packman.util.command.ExecutableManager
import com.blackducksoftware.integration.hub.packman.util.command.ExecutableOutput
import com.blackducksoftware.integration.hub.packman.util.command.ExecutableRunner
import com.blackducksoftware.integration.hub.packman.util.command.ExecutableRunnerException

@Component
class PipPackageManager extends PackageManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass())

    static final String SETUP_FILENAME = 'setup.py'
    static final Map<String, String> WINDOWS_ENV_VARIABLES = ["PYTHONIOENCODING":"utf8"]

    @Autowired
    PipPackager pipPackager

    @Autowired
    ExecutableManager commandManager

    @Autowired
    ExecutableRunner commandRunner

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

    ExecutableType pipCommandType
    ExecutableType pythonCommandType

    String pythonCommand
    String pipCommand
    String binFolderName
    Map<String, String> envVariables = [:]

    @PostConstruct
    void init() {
        if(pipThreeOverride) {
            pythonCommandType = ExecutableType.PYTHON3
            pipCommandType = ExecutableType.PIP3
        } else {
            pythonCommandType = ExecutableType.PYTHON
            pipCommandType = ExecutableType.PIP
        }
        pythonCommand = findCommand(null, pythonPath, pythonCommandType)
        pipCommand = findCommand(null, pipPath, pipCommandType)
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
        def foundExectables = pythonCommand && pipCommand
        def foundFiles = fileFinder.containsAllFiles(sourcePath, SETUP_FILENAME)
        foundExectables && foundFiles
    }

    List<DependencyNode> extractDependencyNodes(String sourcePath) {
        try {
            setupEnvironment(sourcePath)
            return pipPackager.makeDependencyNodes(sourcePath, pipCommand, pythonCommand, envVariables)
        } catch (ExecutableRunnerException e) {
            def message = 'An error occured when trying to extract python dependencies'
            logger.warn(message, e)
        }
        return null
    }

    private void setupEnvironment(String sourcePath) throws ExecutableRunnerException {
        File sourceDirectory = new File(sourcePath)
        ExecutableRunner commandRunner = new ExecutableRunner()
        Executable installVirtualenvPackage = new Executable(sourceDirectory, pipCommand, Arrays.asList('install', 'virtualenv'))

        File virtualEnv = new File(packmanProperties.getOutputDirectoryPath(), 'blackduck_virtualenv')
        String virtualEnvBin = new File(virtualEnv, binFolderName).absolutePath

        if (createVirtualEnv) {
            commandRunner.executeLoudly(installVirtualenvPackage)
            String virtualEnvLocation = getPackageLocation(sourceDirectory, 'virtualenv')
            List<String> commandArgs = [
                "${virtualEnvLocation}/virtualenv.py",
                virtualEnv.absolutePath
            ]
            def createVirtualEnvCommand = new Executable(sourceDirectory, pythonCommand, commandArgs)
            commandRunner.executeLoudly(createVirtualEnvCommand)
            pythonCommand = findCommand(virtualEnvBin, pythonPath, pythonCommandType)
            pipCommand = findCommand(virtualEnvBin, pipPath, pipCommandType)
        }
    }

    String getPackageLocation(File sourceDirectory, String packageName) throws ExecutableRunnerException {
        def showPackage = new Executable(sourceDirectory, envVariables, pipCommand, Arrays.asList('show', packageName))
        ExecutableOutput pipShowResults = commandRunner.executeQuietly(showPackage)
        def pipShowParser = new PipShowMapParser()
        Map<String, String> map = pipShowParser.parse(pipShowResults.getStandardOutput())
        return map['Location'].trim()
    }

    private String findCommand(String path, String executablePath, String commandType) {
        if (StringUtils.isNotBlank(executablePath)) {
            executablePath
        } else {
            if(StringUtils.isBlank(path)){
                commandManager.getPathOfCommand(commandType)
            } else {
                commandManager.getPathOfCommand(path, commandType)
            }
        }
    }
}