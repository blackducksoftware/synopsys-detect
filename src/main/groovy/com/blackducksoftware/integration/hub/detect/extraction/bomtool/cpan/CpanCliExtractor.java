package com.blackducksoftware.integration.hub.detect.extraction.bomtool.cpan;

import java.util.List;

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
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.cpan.parse.CpanPackager;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class CpanCliExtractor extends Extractor<CpanCliContext>{
    private final Logger logger = LoggerFactory.getLogger(CpanCliExtractor.class);

    @Autowired
    CpanPackager cpanPackager;

    @Autowired
    protected ExternalIdFactory externalIdFactory;

    @Autowired
    protected ExecutableRunner executableRunner;

    @Autowired
    protected DetectConfiguration detectConfiguration;

    @Override
    public Extraction extract(final CpanCliContext context) {
        try {
            final ExecutableOutput cpanListOutput = executableRunner.runExe(context.cpanExe, "-l");
            final List<String> listText = cpanListOutput.getStandardOutputAsList();

            final ExecutableOutput showdepsOutput = executableRunner.runExe(context.cpanmExe, "--showdeps", ".");
            final List<String> showdeps = showdepsOutput.getStandardOutputAsList();

            final DependencyGraph dependencyGraph = cpanPackager.makeDependencyGraph(listText, showdeps);
            final ExternalId externalId = externalIdFactory.createPathExternalId(Forge.CPAN, context.directory.toString());
            final DetectCodeLocation detectCodeLocation = new DetectCodeLocation.Builder(BomToolType.CPAN, context.directory.toString(), externalId, dependencyGraph).build();
            return new Extraction(ExtractionResult.Success, detectCodeLocation);
        } catch (final Exception e) {
            return new Extraction(ExtractionResult.Failure, e);
        }
    }
}