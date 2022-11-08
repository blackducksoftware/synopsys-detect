package com.synopsys.integration.detect.workflow.airgap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.resolver.GradleResolver;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

import ch.qos.logback.core.util.FileUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class GradleAirGapCreator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GradleResolver gradleResolver;
    private final DetectableExecutableRunner executableRunner;
    private final Configuration configuration;

    public GradleAirGapCreator(GradleResolver gradleResolver, DetectableExecutableRunner executableRunner, Configuration configuration) {
        this.gradleResolver = gradleResolver;
        this.executableRunner = executableRunner;
        this.configuration = configuration;
    }

    public void installGradleDependencies(File gradleTemp, File gradleTarget) throws DetectUserFriendlyException {
        logger.info("Checking for gradle on the path.");
        ExecutableTarget gradle;
        try {
            gradle = gradleResolver.resolveGradle(new DetectableEnvironment(gradleTemp));
            if (gradle == null) {
                throw new DetectUserFriendlyException("Gradle must be on the path to make an Air Gap zip.", ExitCodeType.FAILURE_CONFIGURATION);
            }
        } catch (DetectableException e) {
            throw new DetectUserFriendlyException("An error occurred while finding Gradle which is needed to make an Air Gap zip.", e, ExitCodeType.FAILURE_CONFIGURATION);
        }

        File gradleOutput = new File(gradleTemp, "dependencies");
        logger.info("Using temporary gradle dependency directory: " + gradleOutput);

        File buildGradle = new File(gradleTemp, "build.gradle");
        File settingsGradle = new File(gradleTemp, "settings.gradle");
        logger.info("Using temporary gradle build file: " + buildGradle);
        logger.info("Using temporary gradle settings file: " + settingsGradle);
        FileUtil.createMissingParentDirectories(buildGradle);
        FileUtil.createMissingParentDirectories(settingsGradle);

        logger.info("Writing to temporary gradle build file.");
        try {
            Map<String, String> gradleScriptData = new HashMap<>();
            gradleScriptData.put("gradleOutput", StringEscapeUtils.escapeJava(gradleOutput.getCanonicalPath()));

            Template gradleScriptTemplate = configuration.getTemplate("create-gradle-airgap-script.ftl");
            try (Writer fileWriter = new FileWriter(buildGradle)) {
                gradleScriptTemplate.process(gradleScriptData, fileWriter);
            }
            FileUtils.writeStringToFile(settingsGradle, "", StandardCharsets.UTF_8);
        } catch (IOException | TemplateException e) {
            throw new DetectUserFriendlyException("An error occurred creating the temporary build.gradle while creating the Air Gap zip.", e, ExitCodeType.FAILURE_CONFIGURATION);
        }

        logger.info("Invoking gradle install on temporary directory.");
        try {
            ExecutableOutput executableOutput = executableRunner.execute(ExecutableUtils.createFromTarget(gradleTemp, gradle, "installDependencies"));
            if (executableOutput.getReturnCode() != 0) {
                throw new DetectUserFriendlyException("Gradle returned a non-zero exit code while installing Air Gap dependencies.", ExitCodeType.FAILURE_CONFIGURATION);
            }
        } catch (ExecutableRunnerException e) {
            throw new DetectUserFriendlyException("An error occurred using Gradle to make an Air Gap zip.", e, ExitCodeType.FAILURE_CONFIGURATION);
        }

        try {
            logger.info("Moving generated dependencies to final gradle folder: " + gradleTarget.getCanonicalPath());
            FileUtils.moveDirectory(gradleOutput, gradleTarget);
            FileUtils.deleteDirectory(gradleTemp);
        } catch (IOException e) {
            throw new DetectUserFriendlyException("An error occurred moving gradle dependencies to Air Gap folder.", ExitCodeType.FAILURE_CONFIGURATION);
        }
    }
}
