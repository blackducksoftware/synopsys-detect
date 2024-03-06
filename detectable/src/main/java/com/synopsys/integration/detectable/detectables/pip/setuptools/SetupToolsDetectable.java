package com.synopsys.integration.detectable.detectables.pip.setuptools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.tomlj.Toml;
import org.tomlj.TomlArray;
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
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectable.result.SetupToolsRequiresNotFoundDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detectable.util.CycleDetectedException;
import com.synopsys.integration.executable.ExecutableRunnerException;

@DetectableInfo(name = "Setuptools", language = "Python", forge = "Pypi", accuracy = DetectableAccuracyType.LOW, requirementsMarkdown = "A pyproject.toml file.")
public class SetupToolsDetectable extends Detectable {
    
    public static final String PY_PROJECT_TOML = "pyproject.toml";
    public static final String BUILD_KEY = "build-system.requires";
    public static final String REQUIRED_KEY = "setuptools";
    
    private final FileFinder fileFinder;
    private final SetupToolsExtractor setupToolsExtractor;

    private File projectToml;
    private TomlParseResult parsedToml;
    
    public SetupToolsDetectable(DetectableEnvironment environment, FileFinder fileFinder, SetupToolsExtractor setupToolsExtractor) {
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
            String projectTomlText = FileUtils.readFileToString(projectToml, StandardCharsets.UTF_8);

            parsedToml = Toml.parse(projectTomlText);

            if (parsedToml != null) {
                TomlArray buildRequires = parsedToml.getArray(BUILD_KEY);

                if (buildRequires != null) {
                    for (int i = 0; i < buildRequires.size(); i++) {
                        String requires = buildRequires.getString(i);

                        if (requires.equals(REQUIRED_KEY)) {
                            return new PassedDetectableResult();
                        }
                    }
                }
            }

            return new SetupToolsRequiresNotFoundDetectableResult();
        } catch (Exception e) {
            return new ExceptionDetectableResult(e);
        }
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableRunnerException,
            ExecutableFailedException, IOException, JsonSyntaxException, CycleDetectedException, DetectableException,
            MissingExternalIdException, ParserConfigurationException, SAXException {
        return setupToolsExtractor.extract(projectToml);
    }
}
