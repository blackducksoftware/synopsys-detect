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
import com.blackducksoftware.integration.hub.packman.help.ValueDescription
import com.blackducksoftware.integration.hub.packman.packagemanager.pip.PipPackager
import com.blackducksoftware.integration.hub.packman.packagemanager.pip.PipShowMapParser
import com.blackducksoftware.integration.hub.packman.type.CommandType
import com.blackducksoftware.integration.hub.packman.type.PackageManagerType
import com.blackducksoftware.integration.hub.packman.util.FileFinder
import com.blackducksoftware.integration.hub.packman.util.command.Command
import com.blackducksoftware.integration.hub.packman.util.command.CommandManager
import com.blackducksoftware.integration.hub.packman.util.command.CommandOutput
import com.blackducksoftware.integration.hub.packman.util.command.CommandRunner
import com.blackducksoftware.integration.hub.packman.util.command.CommandRunnerException

@Component
class PipPackageManager extends PackageManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass())

    static final String SETUP_FILENAME = 'setup.py'
    static final Map<String, String> WINDOWS_ENV_VARIABLES = ["PYTHONIOENCODING":"utf8"]

    @Autowired
    PipPackager pipPackager

    @Autowired
    CommandManager commandManager

    @Autowired
    CommandRunner commandRunner

    @Autowired
    FileFinder fileFinder

    @Autowired
    PackmanProperties packmanProperties

    @ValueDescription(description="If true creates a temporary Python virtual environment")
    @Value('${packman.pip.createVirtualEnv}')
    boolean createVirtualEnv

    @ValueDescription(description="If true will use pip3 if available on class path")
    @Value('${packman.pip.pip3}')
    boolean pipThreeOverride

    @ValueDescription(description="The path of the Python executable")
    @Value('${packman.python.path}')
    String pythonPath

    @ValueDescription(description="The path of the Pip executable")
    @Value('${packman.pip.path}')
    String pipPath

    CommandType pipCommandType
    CommandType pythonCommandType

    String pythonCommand
    String pipCommand
    String binFolderName
    Map<String, String> envVariables = [:]

    @PostConstruct
    void init() {
        if(pipThreeOverride) {
            pythonCommandType = CommandType.PYTHON3
            pipCommandType = CommandType.PIP3
        } else {
            pythonCommandType = CommandType.PYTHON
            pipCommandType = CommandType.PIP
        }
        pythonCommand = findPythonCommand(null)
        pipCommand = findPipCommand(null)
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
        } catch (CommandRunnerException e) {
            def message = 'An error occured when trying to extract python dependencies'
            logger.warn(message, e)
        }
        return null
    }

    private void setupEnvironment(String sourcePath) throws CommandRunnerException {
        File sourceDirectory = new File(sourcePath)
        CommandRunner commandRunner = new CommandRunner()
        Command installVirtualenvPackage = new Command(sourceDirectory, pipCommand, Arrays.asList('install', 'virtualenv'))

        File virtualEnv = new File(packmanProperties.getOutputDirectoryPath(), 'blackduck_virtualenv')
        String virtualEnvBin = new File(virtualEnv, binFolderName).absolutePath

        if (createVirtualEnv) {
            commandRunner.executeLoudly(installVirtualenvPackage)
            String virtualEnvLocation = getPackageLocation(sourceDirectory, 'virtualenv')
            List<String> commandArgs = [
                "${virtualEnvLocation}/virtualenv.py",
                virtualEnv.absolutePath
            ]
            def createVirtualEnvCommand = new Command(sourceDirectory, pythonCommand, commandArgs)
            commandRunner.executeLoudly(createVirtualEnvCommand)
            pythonCommand = findPythonCommand(virtualEnvBin)
            pipCommand = findPipCommand(virtualEnvBin)
        }
    }

    String getPackageLocation(File sourceDirectory, String packageName) throws CommandRunnerException {
        def showPackage = new Command(sourceDirectory, envVariables, pipCommand, Arrays.asList('show', packageName))
        CommandOutput pipShowResults = commandRunner.executeQuietly(showPackage)
        def pipShowParser = new PipShowMapParser()
        Map<String, String> map = pipShowParser.parse(pipShowResults.getStandardOutput())
        return map['Location'].trim()
    }

    private String findPythonCommand(String path) {
        if (StringUtils.isNotBlank(pythonPath)) {
            pythonPath
        } else {
            if(StringUtils.isBlank(path)){
                commandManager.getPathOfCommand(pythonCommandType)
            } else {
                commandManager.getPathOfCommand(path, pythonCommandType)
            }
        }
    }

    private String findPipCommand(String path) {
        if (StringUtils.isNotBlank(pipPath)) {
            pipPath
        } else {
            if(StringUtils.isBlank(path)){
                commandManager.getPathOfCommand(pipCommandType)
            } else {
                commandManager.getPathOfCommand(path, pipCommandType)
            }
        }
    }
}