package com.blackducksoftware.integration.hub.detect;

import javax.xml.parsers.DocumentBuilder;

import com.blackducksoftware.integration.hub.detect.configuration.ConnectionManager;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.lifecycle.run.RunDependencies;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.AirGapManager;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

import freemarker.template.Configuration;

public class BomToolDependenciesBuilder {
    private Gson gson;
    private JsonParser jsonParser;
    private Configuration configuration;
    private DocumentBuilder documentBuilder;
    private ExecutableRunner executableRunner;
    private AirGapManager airGapManager;
    private ExecutableManager executableManager;
    private ExternalIdFactory externalIdFactory;
    private DetectFileFinder detectFileFinder;
    private DirectoryManager directoryManager;
    private DetectConfiguration detectConfiguration;
    private ConnectionManager connectionManager;
    private StandardExecutableFinder standardExecutableFinder;

    public BomToolDependenciesBuilder fromRunDependencies(RunDependencies runDependencies) {
        setGson(runDependencies.gson);
        setConfiguration(runDependencies.configuration);
        setDocumentBuilder(runDependencies.documentBuilder);
        setDirectoryManager(runDependencies.directoryManager);

        setDetectConfiguration(runDependencies.detectConfiguration);
        return this;
    }

    public BomToolDependenciesBuilder fromDefaults() {
        setExternalIdFactory(new ExternalIdFactory());
        return this;
    }

    public BomToolDependenciesBuilder setGson(final Gson gson) {
        this.gson = gson;
        return this;
    }

    public BomToolDependenciesBuilder setJsonParser(final JsonParser jsonParser) {
        this.jsonParser = jsonParser;
        return this;
    }

    public BomToolDependenciesBuilder setConfiguration(final Configuration configuration) {
        this.configuration = configuration;
        return this;
    }

    public BomToolDependenciesBuilder setDocumentBuilder(final DocumentBuilder documentBuilder) {
        this.documentBuilder = documentBuilder;
        return this;
    }

    public BomToolDependenciesBuilder setExecutableRunner(final ExecutableRunner executableRunner) {
        this.executableRunner = executableRunner;
        return this;
    }

    public BomToolDependenciesBuilder setAirGapManager(final AirGapManager airGapManager) {
        this.airGapManager = airGapManager;
        return this;
    }

    public BomToolDependenciesBuilder setExecutableManager(final ExecutableManager executableManager) {
        this.executableManager = executableManager;
        return this;
    }

    public BomToolDependenciesBuilder setExternalIdFactory(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
        return this;
    }

    public BomToolDependenciesBuilder setDetectFileFinder(final DetectFileFinder detectFileFinder) {
        this.detectFileFinder = detectFileFinder;
        return this;
    }

    public BomToolDependenciesBuilder setDirectoryManager(final DirectoryManager directoryManager) {
        this.directoryManager = directoryManager;
        return this;
    }

    public BomToolDependenciesBuilder setDetectConfiguration(final DetectConfiguration detectConfiguration) {
        this.detectConfiguration = detectConfiguration;
        return this;
    }

    public BomToolDependenciesBuilder setConnectionManager(final ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        return this;
    }

    public BomToolDependenciesBuilder setStandardExecutableFinder(final StandardExecutableFinder standardExecutableFinder) {
        this.standardExecutableFinder = standardExecutableFinder;
        return this;
    }

    public BomToolDependencies build() {
        return new BomToolDependencies(gson, jsonParser, configuration, documentBuilder, executableRunner, airGapManager, executableManager, externalIdFactory, detectFileFinder, directoryManager, detectConfiguration, connectionManager,
            standardExecutableFinder);
    }
}