package com.blackducksoftware.integration.hub.packman.util.executable

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

    String getPathOfExecutable(ExecutableType executableType) {
        File executableFile = getExecutable(executableType)

        null == executableFile ? null : executableFile.absolutePath
    }

    File getExecutable(ExecutableType executableType) {
        String executable = executableType.getExecutable(currentOs)
        File executableFile = findExecutableFile(executable)

        executableFile
    }

    String getPathOfExecutable(String path, ExecutableType executableType) {
        File executableFile = getExecutable(path, executableType)

        null == executableFile ? null : executableFile.absolutePath
    }

    File getExecutable(String path, ExecutableType executableType) {
        String executable = executableType.getExecutable(currentOs)
        File executableFile = findExecutableFile(path, executable)

        executableFile
    }

    private File findExecutableFile(final String executable) {
        String systemPath = System.getenv("PATH")
        return findExecutableFile(systemPath, executable)
    }

    private File findExecutableFile(final String path, String executable) {
        for (String pathPiece : path.split(File.pathSeparator)) {
            File foundFile = fileFinder.findFile(pathPiece, executable)
            if (foundFile && foundFile.canExecute()) {
                return foundFile
            }
        }
        null
    }
}
