package com.blackducksoftware.integration.hub.detect;

import javax.xml.parsers.DocumentBuilder;

import com.blackducksoftware.integration.hub.detect.configuration.ConnectionManager;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
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

public class BomToolDependencies {
    private final Gson gson;
    private final JsonParser jsonParser;
    private final Configuration configuration;
    private final DocumentBuilder documentBuilder;

    private final ExecutableRunner executableRunner;
    private final AirGapManager airGapManager;
    private final ExecutableManager executableManager;
    private final ExternalIdFactory externalIdFactory;
    private final DetectFileFinder detectFileFinder;
    private final DirectoryManager directoryManager;
    private final DetectConfiguration detectConfiguration;
    private final ConnectionManager connectionManager;
    private final StandardExecutableFinder standardExecutableFinder;

    public BomToolDependencies(final Gson gson, final JsonParser jsonParser, final Configuration configuration, final DocumentBuilder documentBuilder,
        final ExecutableRunner executableRunner, final AirGapManager airGapManager, final ExecutableManager executableManager, final ExternalIdFactory externalIdFactory,
        final DetectFileFinder detectFileFinder, final DirectoryManager directoryManager, final DetectConfiguration detectConfiguration, final ConnectionManager connectionManager,
        final StandardExecutableFinder standardExecutableFinder) {
        this.gson = gson;
        this.jsonParser = jsonParser;
        this.configuration = configuration;
        this.documentBuilder = documentBuilder;
        this.executableRunner = executableRunner;
        this.airGapManager = airGapManager;
        this.executableManager = executableManager;
        this.externalIdFactory = externalIdFactory;
        this.detectFileFinder = detectFileFinder;
        this.directoryManager = directoryManager;
        this.detectConfiguration = detectConfiguration;
        this.connectionManager = connectionManager;
        this.standardExecutableFinder = standardExecutableFinder;
    }

    public Gson getGson() {
        return gson;
    }

    public JsonParser getJsonParser() {
        return jsonParser;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public DocumentBuilder getDocumentBuilder() {
        return documentBuilder;
    }

    public ExecutableRunner getExecutableRunner() {
        return executableRunner;
    }

    public AirGapManager getAirGapManager() {
        return airGapManager;
    }

    public ExecutableManager getExecutableManager() {
        return executableManager;
    }

    public ExternalIdFactory getExternalIdFactory() {
        return externalIdFactory;
    }

    public DetectFileFinder getDetectFileFinder() {
        return detectFileFinder;
    }

    public DirectoryManager getDirectoryManager() {
        return directoryManager;
    }

    public DetectConfiguration getDetectConfiguration() {
        return detectConfiguration;
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public StandardExecutableFinder getStandardExecutableFinder() {
        return standardExecutableFinder;
    }
}
