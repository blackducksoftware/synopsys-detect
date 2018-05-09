package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.NpmExecutableRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluator;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

@Component
public class NpmExecutableRequirementEvaluator extends RequirementEvaluator<NpmExecutableRequirement> {

    @Autowired
    public DetectFileManager detectFileManager;

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public ExecutableManager executableManager;

    @Autowired
    public ExecutableRunner executableRunner;

    private String foundNpm = null;
    private boolean hasLookedForNpm = false;

    @Override
    public RequirementEvaluation<String> evaluate(final NpmExecutableRequirement requirement, final EvaluationContext context) {
        try {
            if (!hasLookedForNpm) {
                foundNpm = findNpm();
                hasLookedForNpm = true;
            }
            if (foundNpm != null) {
                return RequirementEvaluation.passed( foundNpm);
            }else {
                return RequirementEvaluation.failed(null, "No Npm executable was found.");
            }
        }catch (final Exception e) {
            return RequirementEvaluation.error(null);
        }
    }

    String findNpm() {
        final String npm = executableManager.getExecutablePathOrOverride(ExecutableType.NPM, true, detectConfiguration.getSourcePath(), detectConfiguration.getNpmPath());
        if (validateNpm(null, npm)) {
            return npm;
        }
        return null;
    }

    boolean validateNpm(final File directoryToSearch, final String npmExePath) {
        if (StringUtils.isNotBlank(npmExePath)) {
            Executable npmVersionExe = null;
            final List<String> arguments = new ArrayList<>();
            arguments.add("-version");

            String npmNodePath = detectConfiguration.getNpmNodePath();
            if (StringUtils.isNotBlank(npmNodePath)) {
                final int lastSlashIndex = npmNodePath.lastIndexOf("/");
                if (lastSlashIndex >= 0) {
                    npmNodePath = npmNodePath.substring(0, lastSlashIndex);
                }
                final Map<String, String> environmentVariables = new HashMap<>();
                environmentVariables.put("PATH", npmNodePath);

                npmVersionExe = new Executable(directoryToSearch, environmentVariables, npmExePath, arguments);
            } else {
                npmVersionExe = new Executable(directoryToSearch, npmExePath, arguments);
            }
            try {
                final String npmVersion = executableRunner.execute(npmVersionExe).getStandardOutput();
                //logger.debug(String.format("Npm version %s", npmVersion));
                return true;
            } catch (final ExecutableRunnerException e) {
                //logger.error(String.format("Could not run npm to get the version: %s", e.getMessage()));
            }
        }
        return false;
    }

    @Override
    public Class getRequirementClass() {
        return NpmExecutableRequirement.class;
    }
}
