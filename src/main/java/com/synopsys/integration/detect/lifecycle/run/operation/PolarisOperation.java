package com.synopsys.integration.detect.lifecycle.run.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableRunner;
import com.synopsys.integration.detect.tool.polaris.PolarisTool;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;

public class PolarisOperation extends Operation<Void, Void> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private ProductRunData productRunData;
    private PropertyConfiguration detectConfiguration;
    private DirectoryManager directoryManager;
    private DetectToolFilter detectToolFilter;
    private EventSystem eventSystem;

    public PolarisOperation(ProductRunData productRunData, PropertyConfiguration detectConfiguration, DirectoryManager directoryManager, DetectToolFilter detectToolFilter, EventSystem eventSystem) {
        this.productRunData = productRunData;
        this.detectConfiguration = detectConfiguration;
        this.directoryManager = directoryManager;
        this.detectToolFilter = detectToolFilter;
        this.eventSystem = eventSystem;
    }

    @Override
    protected boolean shouldExecute() {
        return productRunData.shouldUsePolarisProduct() && detectToolFilter.shouldInclude(DetectTool.POLARIS);
    }

    @Override
    public String getOperationName() {
        return "Polaris";
    }

    @Override
    protected OperationResult<Void> executeOperation(Void input) {
        PolarisServerConfig polarisServerConfig = productRunData.getPolarisRunData().getPolarisServerConfig();
        DetectableExecutableRunner polarisExecutableRunner = DetectExecutableRunner.newInfo(eventSystem);
        PolarisTool polarisTool = new PolarisTool(eventSystem, directoryManager, polarisExecutableRunner, detectConfiguration, polarisServerConfig);
        polarisTool.runPolaris(new Slf4jIntLogger(logger), directoryManager.getSourceDirectory());
        return OperationResult.success();
    }
}
