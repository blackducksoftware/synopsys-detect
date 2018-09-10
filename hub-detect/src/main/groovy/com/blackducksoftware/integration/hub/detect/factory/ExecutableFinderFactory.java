package com.blackducksoftware.integration.hub.detect.factory;

import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleExecutableFinder;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenExecutableFinder;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmCliParser;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmExecutableFinder;
import com.blackducksoftware.integration.hub.detect.bomtool.pear.PearParser;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PythonExecutableFinder;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.StandardExecutableFinder;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class ExecutableFinderFactory {

    private final ExecutableRunner executableRunner;
    private final DetectConfiguration detectConfiguration;
    private final ExecutableManager executableManager;

    public ExecutableFinderFactory(final ExecutableRunner executableRunner, final DetectConfiguration detectConfiguration, final ExecutableManager executableManager){
        this.executableRunner = executableRunner;
        this.detectConfiguration = detectConfiguration;
        this.executableManager = executableManager;
    }

    public StandardExecutableFinder standardExecutableFinder(){
        return  new StandardExecutableFinder(executableManager, detectConfiguration);
    }

    public GradleExecutableFinder gradleExecutableFinder() {
        return new GradleExecutableFinder(executableManager, detectConfiguration);
    }

    public MavenExecutableFinder mavenExecutableFinder() {
        return new MavenExecutableFinder(executableManager, detectConfiguration);
    }

    public NpmExecutableFinder npmExecutableFinder() {
        return new NpmExecutableFinder(executableManager, executableRunner, detectConfiguration);
    }

    public PythonExecutableFinder pythonExecutableFinder() {
        return new PythonExecutableFinder(executableManager, detectConfiguration);
    }

}
