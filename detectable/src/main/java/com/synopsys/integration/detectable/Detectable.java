package com.synopsys.integration.detectable;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.google.gson.JsonSyntaxException;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detectable.util.CycleDetectedException;
import com.synopsys.integration.executable.ExecutableRunnerException;

public abstract class Detectable {
    protected DetectableEnvironment environment;

    protected Detectable(DetectableEnvironment environment) {
        this.environment = environment;
    }

    /*
     * Applicable should be light-weight and should never throw an exception. Look for files, check properties, short and sweet.
     */
    public abstract DetectableResult applicable();

    /*
     * Extractable may be as heavy as needed, and may (and sometimes should) fail. Make web requests, install inspectors or run executables.
     */
    public abstract DetectableResult extractable() throws DetectableException;

    /*
     * Perform the extraction and in case of error ideally return an Extraction with the exception and additional context such as failure description.
     * If there is no additional context, Detect can catch exceptions listed below. Feel free to add your own.
     */
    public abstract Extraction extract(ExtractionEnvironment extractionEnvironment) throws
        ExecutableRunnerException,
        ExecutableFailedException,
        IOException,
        JsonSyntaxException,
        CycleDetectedException,
        DetectableException,
        MissingExternalIdException,
        ParserConfigurationException,
        SAXException;
}
