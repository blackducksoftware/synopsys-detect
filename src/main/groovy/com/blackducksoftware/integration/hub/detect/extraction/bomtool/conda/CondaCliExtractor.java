package com.blackducksoftware.integration.hub.detect.extraction.bomtool.conda;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.conda.parse.CondaListParser;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class CondaCliExtractor extends Extractor<CondaCliContext>{
    private final Logger logger = LoggerFactory.getLogger(CondaCliExtractor.class);

    @Autowired
    CondaListParser condaListParser;

    @Autowired
    protected ExternalIdFactory externalIdFactory;

    @Autowired
    protected ExecutableRunner executableRunner;

    @Autowired
    protected DetectConfiguration detectConfiguration;

    @Override
    public Extraction extract(final CondaCliContext context) {
        try {
            final List<String> condaListOptions = new ArrayList<>();
            condaListOptions.add("list");
            if (StringUtils.isNotBlank(detectConfiguration.getCondaEnvironmentName())) {
                condaListOptions.add("-n");
                condaListOptions.add(detectConfiguration.getCondaEnvironmentName());
            }
            condaListOptions.add("--json");
            final Executable condaListExecutable = new Executable(context.directory, context.condaExe, condaListOptions);
            final ExecutableOutput condaListOutput = executableRunner.execute(condaListExecutable);

            final String listJsonText = condaListOutput.getStandardOutput();

            final ExecutableOutput condaInfoOutput = executableRunner.runExe(context.condaExe, "info", "--json");
            final String infoJsonText = condaInfoOutput.getStandardOutput();

            final DependencyGraph dependencyGraph = condaListParser.parse(listJsonText, infoJsonText);
            final ExternalId externalId = externalIdFactory.createPathExternalId(Forge.ANACONDA, context.directory.toString());
            final DetectCodeLocation detectCodeLocation = new DetectCodeLocation.Builder(BomToolType.CONDA, context.directory.toString(), externalId, dependencyGraph).build();

            return new Extraction.Builder().success(detectCodeLocation).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
