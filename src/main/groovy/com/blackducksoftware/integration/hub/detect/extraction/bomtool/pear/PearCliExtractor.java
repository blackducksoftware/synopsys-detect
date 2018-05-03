package com.blackducksoftware.integration.hub.detect.extraction.bomtool.pear;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.pear.PearDependencyFinder;
import com.blackducksoftware.integration.hub.detect.bomtool.pear.PearParseResult;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class PearCliExtractor extends Extractor<PearCliContext> {

    static final String PACKAGE_XML_FILENAME = "package.xml";

    @Autowired
    protected DetectFileManager detectFileManager;

    @Autowired
    protected ExternalIdFactory externalIdFactory;

    @Autowired
    PearDependencyFinder pearDependencyFinder;

    @Autowired
    protected ExecutableRunner executableRunner;

    @Override
    public Extraction extract(final PearCliContext context) {
        try {
            final ExecutableOutput pearListing = executableRunner.runExe(context.pearExe, "list");
            final ExecutableOutput pearDependencies = executableRunner.runExe(context.pearExe, "package-dependencies", PACKAGE_XML_FILENAME);

            final File packageFile = detectFileManager.findFile(context.directory, PACKAGE_XML_FILENAME);

            final PearParseResult result = pearDependencyFinder.parse(packageFile, pearListing, pearDependencies);
            final ExternalId id = externalIdFactory.createNameVersionExternalId(Forge.PEAR, result.name, result.version);
            final DetectCodeLocation detectCodeLocation = new DetectCodeLocation.Builder(BomToolType.PEAR, context.directory.toString(), id, result.dependencyGraph)
                    .bomToolProjectName(result.name).bomToolProjectVersionName(result.version).build();


            return new Extraction(ExtractionResult.Success, detectCodeLocation);
        } catch (final Exception e) {
            return new Extraction(ExtractionResult.Failure, e);
        }
    }

}
