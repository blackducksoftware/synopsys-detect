package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluator;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.StandardExecutableRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.StandardExecutableRequirement.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluator;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;

@Component
public class StandardExecutableRequirementEvaluator extends RequirementEvaluator<StandardExecutableRequirement> {

    @Autowired
    public DetectFileManager detectFileManager;

    @Autowired
    public ExecutableManager executableManager;

    @Autowired
    protected DetectConfiguration detectConfiguration;

    @Override
    public RequirementEvaluation<File> evaluate(final StandardExecutableRequirement requirement, final EvaluationContext context) {
        try {
            final StandardExecutableInfo info = createInfo(requirement.executableType);
            if (info == null) {
                return RequirementEvaluation.failed(null, "Unknown executable type: " + requirement.executableType.toString());
            }

            final File exe = executableManager.getExecutable(info.detectExecutableType, true, info.override);
            if (exe != null) {
                return RequirementEvaluation.passed( exe);
            } else {
                return RequirementEvaluation.failed(null, "No " + executableManager.getExecutableName(info.detectExecutableType) + " executable was found.");
            }
        }catch (final Exception e) {
            return RequirementEvaluation.error(null);
        }
    }

    public StandardExecutableInfo createInfo(final StandardExecutableType type) {
        switch (type) {
        case CONDA:
            return new StandardExecutableInfo(ExecutableType.CONDA, detectConfiguration.getCondaPath());
        case CPAN:
            return new StandardExecutableInfo(ExecutableType.CPAN, detectConfiguration.getCpanPath());
        case CPANM:
            return new StandardExecutableInfo(ExecutableType.CPANM, detectConfiguration.getCpanmPath());
        case DOCKER:
            return new StandardExecutableInfo(ExecutableType.DOCKER, detectConfiguration.getDockerPath());
        case BASH:
            return new StandardExecutableInfo(ExecutableType.BASH, detectConfiguration.getBashPath());
        case GO:
            return new StandardExecutableInfo(ExecutableType.GO, null);
        case REBAR3:
            return new StandardExecutableInfo(ExecutableType.REBAR3, detectConfiguration.getHexRebar3Path());
        case PEAR:
            return new StandardExecutableInfo(ExecutableType.PEAR, detectConfiguration.getPearPath());

        }
        return null;
    }

    private class StandardExecutableInfo {
        public ExecutableType detectExecutableType;
        public String override;

        public StandardExecutableInfo(final ExecutableType detectExecutableType, final String override) {
            this.detectExecutableType = detectExecutableType;
            this.override = override;
        }
    }

    @Override
    public Class getRequirementClass() {
        return StandardExecutableRequirement.class;
    }
}
