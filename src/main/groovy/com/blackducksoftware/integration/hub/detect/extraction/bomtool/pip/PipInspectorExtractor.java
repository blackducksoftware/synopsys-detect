package com.blackducksoftware.integration.hub.detect.extraction.bomtool.pip;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.pear.PearDependencyFinder;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

@Component
public class PipInspectorExtractor extends Extractor<PipInspectorContext> {

    static final String PACKAGE_XML_FILENAME = "package.xml";

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    protected DetectFileManager detectFileManager;

    @Autowired
    protected ExternalIdFactory externalIdFactory;

    @Autowired
    PearDependencyFinder pearDependencyFinder;

    @Autowired
    protected ExecutableRunner executableRunner;

    @Autowired
    PipInspectorTreeParser pipInspectorTreeParser;

    @Override
    public Extraction extract(final PipInspectorContext context) {
        try {
            final String projectName = getProjectName(context);
            final String inspectorOutput = runInspector(context.directory, context.pythonExe.toString(), context.pipInspector, projectName, context.requirementFilePath);
            final DetectCodeLocation codeLocation = pipInspectorTreeParser.parse(inspectorOutput, context.directory.toString());

            return new Extraction(ExtractionResult.Success, codeLocation);
        } catch (final Exception e) {
            return new Extraction(ExtractionResult.Failure, e);
        }
    }

    private String runInspector(final File sourceDirectory, final String pythonPath, final File inspectorScript, final String projectName, final String requirementsFilePath) throws ExecutableRunnerException {
        final List<String> inspectorArguments = new ArrayList<>();
        inspectorArguments.add(inspectorScript.getAbsolutePath());

        if (StringUtils.isNotBlank(requirementsFilePath)) {
            final File requirementsFile = new File(requirementsFilePath);
            inspectorArguments.add(String.format("--requirements=%s", requirementsFile.getAbsolutePath()));
        }

        if (StringUtils.isNotBlank(projectName)) {
            inspectorArguments.add(String.format("--projectname=%s", projectName));
        }

        final Executable pipInspector = new Executable(sourceDirectory, pythonPath, inspectorArguments);
        return executableRunner.execute(pipInspector).getStandardOutput();
    }

    String getProjectName(final PipInspectorContext context) throws ExecutableRunnerException {
        String projectName = detectConfiguration.getPipProjectName();
        if (context.setupFile != null && context.setupFile.exists()) {
            if (StringUtils.isBlank(projectName)) {
                final Executable findProjectNameExecutable = new Executable(context.directory, context.pythonExe, Arrays.asList(
                        context.setupFile.getAbsolutePath(),
                        "--name"
                        ));
                final List<String> output = executableRunner.execute(findProjectNameExecutable).getStandardOutputAsList();
                projectName = output.get(output.size() - 1).replace('_', '-').trim();
            }
        }

        return projectName;
    }

}