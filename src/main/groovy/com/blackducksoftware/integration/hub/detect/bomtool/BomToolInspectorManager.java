package com.blackducksoftware.integration.hub.detect.bomtool;

import org.springframework.beans.factory.annotation.Autowired;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

public abstract class BomToolInspectorManager {

    @Autowired
    protected DetectConfiguration detectConfiguration;

    @Autowired
    DetectFileManager detectFileManager;

    @Autowired
    protected ExecutableManager executableManager;

    @Autowired
    protected ExecutableRunner executableRunner;

    public abstract BomToolType getBomToolType();

    //Called once and only once if a Bom Tool of the same type applied to any directory.
    public abstract void install() throws DetectUserFriendlyException;

}
