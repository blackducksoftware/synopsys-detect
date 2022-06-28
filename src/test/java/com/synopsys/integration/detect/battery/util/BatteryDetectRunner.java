package com.synopsys.integration.detect.battery.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.Application;
import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;
import com.synopsys.integration.executable.ProcessBuilderRunner;
import com.synopsys.integration.log.Slf4jIntLogger;

public class BatteryDetectRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final File outputDirectory;
    private final File scriptDirectory;
    private final String detectVersion;

    public BatteryDetectRunner(File outputDirectory, File scriptDirectory, String detectVersion) {
        this.outputDirectory = outputDirectory;
        this.scriptDirectory = scriptDirectory;
        this.detectVersion = detectVersion;
    }

    public List<String> runDetect(List<String> detectArguments, boolean forceScript) throws IOException, ExecutableRunnerException {
        if (forceScript) {
            logger.info("Executing as script.");
            return executeDetectScript(detectArguments);
        }

        Optional<DetectJar> detectJar = DetectJar.locateJar();
        if (detectJar.isPresent()) {
            logger.info("Executed as jar.");
            return executeDetectJar(detectJar.get(), detectArguments);
        }

        logger.info("Executed as static.");
        return executeDetectStatic(detectArguments);
    }

    private List<String> executeDetectStatic(List<String> detectArguments) {
        BatterySysOutCapture capture = new BatterySysOutCapture();

        boolean previous = Application.shouldExit();
        Application.setShouldExit(false);

        capture.startCapture();
        Application.main(detectArguments.toArray(ArrayUtils.EMPTY_STRING_ARRAY));
        List<String> results = capture.stopCapture();

        Application.setShouldExit(previous);
        return results;
    }

    private ExecutableOutput downloadDetectBash(File target) throws ExecutableRunnerException {
        List<String> shellArguments = new ArrayList<>();
        shellArguments.add("-s");
        shellArguments.add("-L");
        shellArguments.add("https://detect.synopsys.com/detect.sh");
        shellArguments.add("-o");
        shellArguments.add(target.toString());

        Executable executable = Executable.create(outputDirectory, new HashMap<>(), "curl", shellArguments);
        ProcessBuilderRunner executableRunner = new ProcessBuilderRunner(new Slf4jIntLogger(logger));
        return executableRunner.execute(executable);
    }

    private List<String> executeDetectScript(List<String> detectArguments) throws ExecutableRunnerException {
        List<String> shellArguments = new ArrayList<>();
        String target = "";
        if (SystemUtils.IS_OS_WINDOWS) {
            target = "powershell";
            shellArguments.add("\"[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect.ps1?$(Get-Random) | iex; detect\"");
        } else {
            File scriptTarget = new File(scriptDirectory, "detect.sh");
            if (scriptTarget.exists()) {
                Assertions.assertTrue(scriptTarget.delete(), "Failed to cleanup an existing detect shell script. This file is cleaned up to ensure latest script is always used.");
            }
            ExecutableOutput downloadOutput = downloadDetectBash(scriptTarget);
            Assertions.assertTrue(downloadOutput.getReturnCode() == 0 && scriptTarget.exists(), "Something went wrong downloading the detect script.");
            Assertions.assertTrue(scriptTarget.setExecutable(true), "Failed to change script permissions to execute. The downloaded detect script must be executable.");
            target = scriptTarget.toString();
        }
        shellArguments.addAll(detectArguments);

        Map<String, String> environmentVariables = new HashMap<>();

        if (StringUtils.isNotBlank(detectVersion)) {
            environmentVariables.put("DETECT_LATEST_RELEASE_VERSION", detectVersion);
        }

        Executable executable = Executable.create(outputDirectory, environmentVariables, target, shellArguments);
        ProcessBuilderRunner executableRunner = new ProcessBuilderRunner(new Slf4jIntLogger(logger));
        ExecutableOutput result = executableRunner.execute(executable);

        List<String> lines = result.getStandardOutputAsList();

        Assertions.assertTrue(lines.size() > 0, "Detect wrote nothing to standard out.");

        return result.getStandardOutputAsList();
    }

    private List<String> executeDetectJar(DetectJar detectJar, List<String> detectArguments) throws ExecutableRunnerException {
        List<String> javaArguments = new ArrayList<>();
        javaArguments.add("-jar");
        javaArguments.add(detectJar.getJar());
        javaArguments.addAll(detectArguments);

        ProcessBuilderRunner executableRunner = new ProcessBuilderRunner(new Slf4jIntLogger(logger));
        ExecutableOutput result = executableRunner.execute(Executable.create(outputDirectory, detectJar.getJava(), javaArguments));

        List<String> lines = result.getStandardOutputAsList();

        Assertions.assertTrue(lines.size() > 0, "Detect wrote nothing to standard out.");

        return result.getStandardOutputAsList();
    }
}
