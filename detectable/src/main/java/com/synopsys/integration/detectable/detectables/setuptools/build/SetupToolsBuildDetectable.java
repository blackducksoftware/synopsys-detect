package com.synopsys.integration.detectable.detectables.setuptools.build;

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
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.executable.resolver.PipResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExceptionDetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectable.result.SetupToolsNoDependenciesDetectableResult;
import com.synopsys.integration.detectable.detectable.result.SetupToolsRequiresNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectables.setuptools.SetupToolsExtractUtils;
import com.synopsys.integration.detectable.detectables.setuptools.SetupToolsExtractor;
import com.synopsys.integration.detectable.detectables.setuptools.parse.SetupToolsParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detectable.util.CycleDetectedException;
import com.synopsys.integration.executable.ExecutableRunnerException;

@DetectableInfo(name = "Setuptools Pip", language = "Python", forge = "Pypi", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "A pyproject.toml file and the pip executable.")
public class SetupToolsBuildDetectable extends Detectable {
    
    private static final String PY_PROJECT_TOML = "pyproject.toml";
    
    private final FileFinder fileFinder;
    private final SetupToolsExtractor setupToolsExtractor;
    private final PipResolver pipResolver;

    private TomlParseResult parsedToml;
    
    private ExecutableTarget pipExe;
    
    private SetupToolsParser setupToolsParser;
    
    public SetupToolsBuildDetectable(DetectableEnvironment environment, FileFinder fileFinder, PipResolver pipResolver, SetupToolsExtractor setupToolsExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.setupToolsExtractor = setupToolsExtractor;
        this.pipResolver = pipResolver;
    }

    @Override
    public DetectableResult applicable() {
        // Ensure there is a pyproject.toml
        Requirements fileResolver = new Requirements(fileFinder, environment);
        File projectToml = fileResolver.file(PY_PROJECT_TOML);
        
        if (fileResolver.isAlreadyFailed()) {
            return fileResolver.result();
        }
        
        try {
            parsedToml = SetupToolsExtractUtils.extractToml(projectToml);

            // Ensure the pyproject.toml file has a requires setuptools line.
            if (parsedToml == null || !SetupToolsExtractUtils.checkTomlRequiresSetupTools(parsedToml)) {
                return new SetupToolsRequiresNotFoundDetectableResult();
            }
        } catch (IOException e){
            return new ExceptionDetectableResult(e);
        }
        
        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        try {
            // Ensure pip is installed and accessible.
            pipExe = pipResolver.resolvePip();
            if (pipExe == null) {
                return new ExecutableNotFoundDetectableResult("pip");
            }
            
            // Ensure dependencies/requirements are specified in a toml, cfg, or py file.
            setupToolsParser = SetupToolsExtractUtils.resolveSetupToolsParser(parsedToml, fileFinder, environment);
            
            if (setupToolsParser == null) {
               return new SetupToolsNoDependenciesDetectableResult(); 
            }
            
            return new PassedDetectableResult();
        } catch (Exception e) {
            return new ExceptionDetectableResult(e);
        }
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableRunnerException,
            ExecutableFailedException, IOException, JsonSyntaxException, CycleDetectedException, DetectableException,
            MissingExternalIdException, ParserConfigurationException, SAXException {
        return setupToolsExtractor.extract(setupToolsParser, pipExe);
    }
}
