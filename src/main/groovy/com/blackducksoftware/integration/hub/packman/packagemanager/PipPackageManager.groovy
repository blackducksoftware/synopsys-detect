package com.blackducksoftware.integration.hub.packman.packagemanager

import javax.annotation.PostConstruct

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.PackmanProperties
import com.blackducksoftware.integration.hub.packman.packagemanager.pip.PipPackager
import com.blackducksoftware.integration.hub.packman.packagemanager.pip.PipShowMapParser
import com.blackducksoftware.integration.hub.packman.type.PackageManagerType
import com.blackducksoftware.integration.hub.packman.util.FileFinder
import com.blackducksoftware.integration.hub.packman.util.command.Command
import com.blackducksoftware.integration.hub.packman.util.command.CommandOutput
import com.blackducksoftware.integration.hub.packman.util.command.CommandRunner
import com.blackducksoftware.integration.hub.packman.util.command.CommandRunnerException
import com.blackducksoftware.integration.hub.packman.util.command.Executable

@Component
class PipPackageManager extends PackageManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass())

    static final String SETUP_FILENAME = 'setup.py'

    @Autowired
    PipPackager pipPackager

    @Autowired
    FileFinder fileFinder

    @Autowired
    PackmanProperties packmanProperties

    @Value('${packman.pip.createVirtualEnv}')
    boolean createVirtualEnv

    @Value('${packman.pip.pip3}')
    boolean pipThreeOverride

    def executables = [
        pip: ['pip.exe', 'pip'],
        python: ['python.exe', 'python']]

    def py3Executables = [
        pip: ['pip3.exe', 'pip3'],
        python: ['python3.exe', 'python3']]

    def folders = [
        bin: ['Scripts', 'bin']]

    @PostConstruct
    void init() {
        if(pipThreeOverride) {
            executables['pip'] = py3Executables['pip']
            executables['python'] = py3Executables['python']
        } else {
            executables['pip'] = executables['pip'] + py3Executables['pip']
            executables['python'] = executables['python'] + py3Executables['python']
        }
    }

    PackageManagerType getPackageManagerType() {
        return PackageManagerType.PIP
    }

    boolean isPackageManagerApplicable(String sourcePath) {
        def foundExectables = fileFinder.canFindAllExecutables(executables)
        def foundFiles = fileFinder.containsAllFiles(sourcePath, SETUP_FILENAME)
        return foundExectables && foundFiles
    }

    List<DependencyNode> extractDependencyNodes(String sourcePath) {
        try {
            Map<String, Executable> foundExecutables = setupEnvironment(sourcePath, executables)
            return pipPackager.makeDependencyNodes(sourcePath, foundExecutables)
        } catch (CommandRunnerException e) {
            def message = 'An error occured when trying to extract python dependencies'
            logger.warn(message, e)
        }
        return null
    }

    private Map<String, Executable> setupEnvironment(String sourcePath, Map<String, List<String>> executables) throws CommandRunnerException {
        final File sourceDirectory = new File(sourcePath)

        Map<String, Executable> foundExecutables = fileFinder.findExecutables(executables)
        final Executable python = foundExecutables['python']
        final Executable pip = foundExecutables['pip']

        CommandRunner commandRunner = new CommandRunner(logger, sourceDirectory, ["PYTHONIOENCODING":"UTF-8"])
        final Command installVirtualenvPackage = new Command(pip, 'install', 'virtualenv')

        final File virtualEnv = new File(packmanProperties.getOutputDirectoryPath(), 'blackduck_virtualenv')
        final String virtualEnvBin = getVirtualEnvBin(virtualEnv)

        if (createVirtualEnv) {
            commandRunner.execute(installVirtualenvPackage)
            String showPackage = getPackageLocation(commandRunner, pip, 'virtualenv')
            def createVirtualEnvCommand = new Command(python, "${showPackage}/virtualenv.py", virtualEnv.getAbsolutePath())
            commandRunner.execute(createVirtualEnvCommand)
            foundExecutables = fileFinder.findExecutables(executables, getVirtualEnvBin(virtualEnv))
        }
        return foundExecutables
    }

    private void installVirtualEnv() {
    }

    private String getVirtualEnvBin(File virtualEnvironmentPath) {
        if(virtualEnvironmentPath.exists() && virtualEnvironmentPath.isDirectory()) {
            Map<String, String> folders = fileFinder.findFolders(this.folders, virtualEnvironmentPath.getAbsolutePath())
            return folders['bin']
        }
        null
    }

    private String getPackageLocation(CommandRunner commandRunner, Executable pip, String packageName) throws CommandRunnerException {
        def showPackage = new Command(pip, 'show', packageName)
        CommandOutput pipShowResults = commandRunner.executeQuietly(showPackage)
        def pipShowParser = new PipShowMapParser()
        Map<String, String> map = pipShowParser.parse(pipShowResults.output)
        return map['Location'].trim()
    }
}