package com.blackducksoftware.integration.hub.detect.lifecycle.boot;

import javax.xml.parsers.DocumentBuilder;

import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.hub.HubServiceManager;
import com.blackducksoftware.integration.hub.detect.lifecycle.shutdown.ExitCodeManager;
import com.blackducksoftware.integration.hub.detect.workflow.DetectRun;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.DiagnosticManager;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.blackducksoftware.integration.hub.detect.workflow.phonehome.PhoneHomeManager;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.synopsys.integration.util.IntegrationEscapeUtil;

import freemarker.template.Configuration;

//This replaces BeanContext in that these are essentially System Wide dependencies of detect. Things like Configuration, and File Manager. If it shouldn't be available everywhere it shouldn't be here.
public class DetectRunDependencies {
    public DetectRun detectRun;
    public DetectInfo detectInfo;
    public DetectConfiguration detectConfiguration;
    public DirectoryManager directoryManager;
    public PhoneHomeManager phoneHomeManager;
    public DiagnosticManager diagnosticManager;
    public HubServiceManager hubServiceManager;
    public EventSystem eventSystem;
    public ExitCodeManager exitCodeManager;
    //shared resources;
    public Gson gson;
    public JsonParser jsonParser;
    public Configuration configuration;
    public DocumentBuilder documentBuilder;
    public IntegrationEscapeUtil integrationEscapeUtil;
}
