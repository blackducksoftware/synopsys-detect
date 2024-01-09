package com.synopsys.integration.detect.battery.docker;


import java.io.IOException;

import com.synopsys.integration.detect.battery.docker.integration.BlackDuckAssertions;
import com.synopsys.integration.detect.battery.docker.integration.BlackDuckTestConnection;
import com.synopsys.integration.exception.IntegrationException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.docker.provider.BuildDockerImageProvider;
import com.synopsys.integration.detect.battery.docker.util.DetectCommandBuilder;
import com.synopsys.integration.detect.battery.docker.util.DetectDockerTestRunner;
import com.synopsys.integration.detect.battery.docker.util.DockerAssertions;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detector.base.DetectorType;

//@Tag("integration")
public class NugetInspectorTests {

    private static final String PROJECT_NAME = "nuget-CPM-docker";

    @Test
    void nugetCPMStandardTest() throws IOException,IntegrationException {
        try(DetectDockerTestRunner test = new DetectDockerTestRunner("detect-nuget-inspector-CPM", "detect-dotnet-seven:1.0.1")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("CPM_standard.dockerfile"));

            String projectVersion = PROJECT_NAME + "-normal";
            BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
            BlackDuckAssertions blackduckAssertions = blackDuckTestConnection.projectVersionAssertions(PROJECT_NAME, projectVersion);
            blackduckAssertions.emptyOnBlackDuck();

            DetectCommandBuilder commandBuilder = new DetectCommandBuilder().defaults().defaultDirectories(test);
            commandBuilder.connectToBlackDuck(blackDuckTestConnection);
            commandBuilder.projectNameVersion(blackduckAssertions);
            commandBuilder.waitForResults();

            commandBuilder.property(DetectProperties.DETECT_TOOLS, "DETECTOR");
            commandBuilder.property(DetectProperties.DETECT_INCLUDED_DETECTOR_TYPES, DetectorType.NUGET.toString());
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.logContains("NuGet Solution Native Inspector: SUCCESS");
            dockerAssertions.atLeastOneBdioFile();

            blackduckAssertions.hasComponents("System.Text.Encodings.Web");
            blackduckAssertions.hasComponents("MessagePack");
            blackduckAssertions.hasComponents("Microsoft.CodeAnalysis.CSharp");
            blackduckAssertions.hasComponents("CSharpIsNullAnalyzer");
        }
    }

    @Test
    void nugetMultiplePropsFileTest() throws IOException, IntegrationException {
        try(DetectDockerTestRunner test = new DetectDockerTestRunner("detect-nuget-inspector-multiple-CPM", "detect-dotnet-seven:1.0.2")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("CPM_multiple_propsfile.dockerfile"));

            String projectVersion = PROJECT_NAME + "-multiple_props_file";
            BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
            BlackDuckAssertions blackduckAssertions = blackDuckTestConnection.projectVersionAssertions(PROJECT_NAME, projectVersion);
            blackduckAssertions.emptyOnBlackDuck();

            DetectCommandBuilder commandBuilder = new DetectCommandBuilder().defaults().defaultDirectories(test);
            commandBuilder.connectToBlackDuck(blackDuckTestConnection);
            commandBuilder.projectNameVersion(blackduckAssertions);
            commandBuilder.waitForResults();

            commandBuilder.property(DetectProperties.DETECT_TOOLS, "DETECTOR");
            commandBuilder.property(DetectProperties.DETECT_INCLUDED_DETECTOR_TYPES, DetectorType.NUGET.toString());
            commandBuilder.property(DetectProperties.DETECT_DETECTOR_SEARCH_DEPTH, String.valueOf(5));
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.logContains("NuGet Solution Native Inspector: SUCCESS");
            dockerAssertions.atLeastOneBdioFile();

            blackduckAssertions.hasComponents("Uno.Core.Extensions.Collections");
            blackduckAssertions.hasComponents("Xamarin.UITest");
            blackduckAssertions.hasComponents("Microsoft.NET.Test.Sdk");
            blackduckAssertions.hasComponents("Microsoft.WindowsAppSDK");
            blackduckAssertions.hasComponents("Microsoft.UI.Xaml");
        }
    }

    @Test
    void nugetExcludeDevDependencyTest() throws IOException, IntegrationException {
        try(DetectDockerTestRunner test = new DetectDockerTestRunner("detect-nuget-inspector-exclude-dependency","detect-dotnet-seven:1.0.3")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("ExcludeDevDependency.dockerfile"));

            String projectVersion = PROJECT_NAME + "-exclude_dev_dependency";
            BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
            BlackDuckAssertions blackduckAssertions = blackDuckTestConnection.projectVersionAssertions(PROJECT_NAME, projectVersion);
            blackduckAssertions.emptyOnBlackDuck();

            DetectCommandBuilder commandBuilder = new DetectCommandBuilder().defaults().defaultDirectories(test);
            commandBuilder.connectToBlackDuck(blackDuckTestConnection);
            commandBuilder.projectNameVersion(blackduckAssertions);
            commandBuilder.waitForResults();

            commandBuilder.property(DetectProperties.DETECT_TOOLS, "DETECTOR");
            commandBuilder.property(DetectProperties.DETECT_INCLUDED_DETECTOR_TYPES, DetectorType.NUGET.toString());
            commandBuilder.property(DetectProperties.DETECT_NUGET_DEPENDENCY_TYPES_EXCLUDED,"DEV");
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.logContains("NuGet Solution Native Inspector: SUCCESS");
            dockerAssertions.atLeastOneBdioFile();

            blackduckAssertions.doesNotHaveComponents("Microsoft.CodeAnalysis.NetAnalyzers");
            blackduckAssertions.doesNotHaveComponents("Microsoft.Windows.CsWin32");
        }
    }
}
