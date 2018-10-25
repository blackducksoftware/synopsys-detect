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
    public Gson gson;
    public JsonParser jsonParser;
    public Configuration configuration;
    public DocumentBuilder documentBuilder;

    public ExecutableRunner executableRunner;
    public AirGapManager airGapManager;
    public ExecutableManager executableManager;
    public ExternalIdFactory externalIdFactory;
    public DetectFileFinder detectFileFinder;
    public DirectoryManager directoryManager;
    public DetectConfiguration detectConfiguration;
    public ConnectionManager connectionManager;
    public StandardExecutableFinder standardExecutableFinder;
}
