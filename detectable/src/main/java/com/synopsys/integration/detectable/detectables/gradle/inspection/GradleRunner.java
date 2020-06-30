package com.synopsys.integration.detectable.detectables.gradle.inspection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.rest.proxy.ProxyInfo;

public class GradleRunner {
    private final ExecutableRunner executableRunner;

    public GradleRunner(ExecutableRunner executableRunner) {
        this.executableRunner = executableRunner;
    }

    public ExecutableOutput runGradleDependencies(File directory, File gradleExe, File gradleInspector, @Nullable String gradleCommand, ProxyInfo proxyInfo, File outputDirectory) throws IOException, ExecutableRunnerException {
        List<String> arguments = new ArrayList<>();
        if (StringUtils.isNotBlank(gradleCommand)) {
            gradleCommand = gradleCommand.replace("dependencies", "").trim();
            Arrays.stream(gradleCommand.split(" "))
                .filter(StringUtils::isNotBlank)
                .forEach(arguments::add);
        }
        arguments.add("dependencies");
        arguments.add(String.format("--init-script=%s", gradleInspector));
        arguments.add(String.format("-DGRADLEEXTRACTIONDIR=%s", outputDirectory.getCanonicalPath()));
        arguments.add("--info");

        if (proxyInfo.getHost().isPresent()) {
            arguments.add("-Dhttps.proxyHost=" + proxyInfo.getHost().get());
        }
        if (proxyInfo.getPort() != 0) {
            arguments.add("-Dhttps.proxyPort=" + proxyInfo.getPort());
        }

        return executableRunner.execute(directory, gradleExe, arguments);
    }
}
