package com.synopsys.integration.detectable.detectables.bazel.pipeline.step;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class FinalStepTransformGithubUrl implements FinalStep {
    public static final String HTTPS = "https";
    public static final String HTTP = "http";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;

    public FinalStepTransformGithubUrl(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    @Override
    public List<Dependency> finish(List<String> input) throws DetectableException {
        for (String githubUrl : input) {
            logger.info("githubUrl: {}", githubUrl);

        }
        return new ArrayList<>();
    }
    //        Dependency.FACTORY.createNameVersionDependency(Forge.GITHUB, declaration.getName(), declaration.getVersion());

    public String getRepoName(String remoteUrlString) throws MalformedURLException {
        String[] pieces = remoteUrlString.split("[/:]");
        if (pieces.length >= 2) {
            String organization = pieces[pieces.length - 2];
            String repo = pieces[pieces.length - 1];
            String name = String.format("%s/%s", organization, repo);
            return StringUtils.removeEnd(StringUtils.removeStart(name, "/"), ".git");
        } else {
            throw new MalformedURLException("Failed to extract repository name from url. Not logging url for security.");
        }
    }
}
