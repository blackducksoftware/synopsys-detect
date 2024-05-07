package com.synopsys.integration.detectable.detectables.setuptools.buildless;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.tomlj.TomlParseResult;
import org.xml.sax.SAXException;

import com.google.gson.JsonSyntaxException;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExceptionDetectableResult;
import com.synopsys.integration.detectable.detectables.setuptools.SetupToolsExtractUtils;
import com.synopsys.integration.detectable.detectables.setuptools.SetupToolsExtractor;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detectable.util.CycleDetectedException;
import com.synopsys.integration.executable.ExecutableRunnerException;

@DetectableInfo(name = "Setuptools", language = "Python", forge = "Pypi", accuracy = DetectableAccuracyType.LOW, requirementsMarkdown = "A pyproject.toml file.")
public class SetupToolsBuildlessDetectable extends Detectable {
    
    private final FileFinder fileFinder;
    private final SetupToolsExtractor setupToolsExtractor;
    
    private static final String PY_PROJECT_TOML = "pyproject.toml";
    
    private File projectToml;
    private TomlParseResult parsedToml;

    public SetupToolsBuildlessDetectable(DetectableEnvironment environment, FileFinder fileFinder, SetupToolsExtractor setupToolsExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.setupToolsExtractor = setupToolsExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        projectToml = requirements.file(PY_PROJECT_TOML);
        
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        try {
            parsedToml = SetupToolsExtractUtils.extractToml(projectToml);

            return SetupToolsExtractUtils.checkTomlRequiresSetupTools(parsedToml);
        } catch (Exception e) {
            return new ExceptionDetectableResult(e);
        }
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableRunnerException,
            ExecutableFailedException, IOException, JsonSyntaxException, CycleDetectedException, DetectableException,
            MissingExternalIdException, ParserConfigurationException, SAXException {
        return setupToolsExtractor.extract(environment.getDirectory(), projectToml, null);
    }
}
