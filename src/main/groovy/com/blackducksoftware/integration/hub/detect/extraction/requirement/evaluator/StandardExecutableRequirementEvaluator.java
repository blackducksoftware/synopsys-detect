package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluator;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.StandardExecutableRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.StandardExecutableRequirement.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation.EvaluationResult;
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
                return new RequirementEvaluation<>(EvaluationResult.Failed, null);
            }

            final File exe = executableManager.getExecutable(info.detectExecutableType, true, info.override);
            if (exe != null) {
                return new RequirementEvaluation<>(EvaluationResult.Passed, exe);
            } else {
                return new RequirementEvaluation<>(EvaluationResult.Failed, null);
            }
        }catch (final Exception e) {
            return new RequirementEvaluation<>(EvaluationResult.Exception, null);
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
            return new StandardExecutableInfo(ExecutableType.GO, detectConfiguration.getBashPath());

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
