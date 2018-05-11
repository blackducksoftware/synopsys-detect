package com.blackducksoftware.integration.hub.detect.extraction.bomtool.pear;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.Applicable;
import com.blackducksoftware.integration.hub.detect.extraction.Extractable;
import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class PearCliStrategy extends Strategy<PearCliContext, PearCliExtractor> {
    public static final String PACKAGE_XML_FILENAME= "package.xml";

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public StandardExecutableFinder standardExecutableFinder;

    @Autowired
    public DetectConfiguration detectConfiguration;

    public PearCliStrategy() {
        super("Pear Cli", BomToolType.PEAR, PearCliContext.class, PearCliExtractor.class);
    }

    public Applicable applicable(final EvaluationContext evaluation, final PearCliContext context) {
        final File PEAR= fileFinder.findFile(evaluation.getDirectory(), PACKAGE_XML_FILENAME);
        if (PEAR == null) {
            return Applicable.doesNotApply("No package.xml file was found with pattern: " + PACKAGE_XML_FILENAME);
        }

        return Applicable.doesApply();
    }

    public Extractable extractable(final EvaluationContext evaluation, final PearCliContext context){
        context.pearExe = standardExecutableFinder.getExecutable(StandardExecutableType.PEAR);

        if (context.pearExe == null) {
            return Extractable.canNotExtract("No pear executable was found.");
        }

        return Extractable.canExtract();
    }


}
