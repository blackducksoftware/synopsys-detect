package com.blackducksoftware.integration.hub.detect.workflow.boot;

import javax.xml.parsers.DocumentBuilder;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.PhoneHomeManager;
import com.blackducksoftware.integration.hub.detect.workflow.report.ReportManager;
import com.google.gson.Gson;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

import freemarker.template.Configuration;

//This replaces BeanContext in that these are essentially System Wide dependencies of detect. Things like Configuration, and File Manager. If it shouldn't be available everywhere it shouldn't be here.
public class DetectContext {
    public Gson gson;
    public DocumentBuilder documentBuilder;
    public Configuration configuration;
    public DetectConfiguration detectConfiguration;
    public DetectFileManager detectFileManager;
    public DetectFileFinder detectFileFinder;
    public ExecutableManager executableManager;
    public ExecutableRunner executableRunner;
    public ExternalIdFactory externalIdFactory;
    public ReportManager reportManager;
    public PhoneHomeManager phoneHomeManager;
}
