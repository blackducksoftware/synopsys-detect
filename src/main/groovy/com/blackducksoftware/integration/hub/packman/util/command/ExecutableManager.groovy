package com.blackducksoftware.integration.hub.packman.util.command

import javax.annotation.PostConstruct

import org.apache.commons.lang3.SystemUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.packman.type.ExecutableType
import com.blackducksoftware.integration.hub.packman.type.OperatingSystemType
import com.blackducksoftware.integration.hub.packman.util.FileFinder

@Component
class ExecutableManager {
    private final Logger logger = LoggerFactory.getLogger(ExecutableManager.class)

    @Autowired
    FileFinder fileFinder

    OperatingSystemType currentOs

    @PostConstruct
    void init() {
        if (SystemUtils.IS_OS_LINUX) {
            currentOs = OperatingSystemType.LINUX
        } else if (SystemUtils.IS_OS_MAC) {
            currentOs = OperatingSystemType.MAC
        } else if (SystemUtils.IS_OS_WINDOWS) {
            currentOs = OperatingSystemType.WINDOWS
        }

        if (!currentOs) {
            logger.warn("Your operating system is not supported. Linux will be assumed.")
            currentOs = OperatingSystemType.LINUX
        } else {
            logger.info("You seem to be running in a ${currentOs} operating system.")
        }
    }

    String getPathOfCommand(ExecutableType commandType) {
        File commandFile = getCommand(commandType)

        null == commandFile ? null : commandFile.absolutePath
    }

    File getCommand(ExecutableType commandType) {
        String command = commandType.getCommand(currentOs)
        File commandFile = findExecutableFile(command)

        commandFile
    }

    String getPathOfCommand(String path, ExecutableType commandType) {
        File commandFile = getCommand(path, commandType)

        null == commandFile ? null : commandFile.absolutePath
    }

    File getCommand(String path, ExecutableType commandType) {
        String command = commandType.getCommand(currentOs)
        File commandFile = findExecutableFile(path, command)

        commandFile
    }

    private File findExecutableFile(final String command) {
        String systemPath = System.getenv("PATH")
        return findExecutableFile(systemPath, command)
    }

    private File findExecutableFile(final String path, String command) {
        for (String pathPiece : path.split(File.pathSeparator)) {
            File foundFile = fileFinder.findFile(pathPiece, command)
            if (foundFile && foundFile.canExecute()) {
                return foundFile
            }
        }
        null
    }
}
