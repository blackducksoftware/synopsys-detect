package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;

import com.google.gson.Gson;
import com.synopsys.integration.detect.Application;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationRunner;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.proxy.ProxyInfo;

public class RapidContainerScanStepRunner {

    private final OperationRunner operationRunner;

    public RapidContainerScanStepRunner(OperationRunner operationRunner, Gson gson, int timeoutInSeconds) throws DetectUserFriendlyException {
        this.operationRunner = operationRunner;

        IntHttpClient httpClient = new IntHttpClient(
            new SilentIntLogger(),
            gson,
            timeoutInSeconds,
            true,
            ProxyInfo.NO_PROXY_INFO
        );
    }

    public UUID submitScan(BlackDuckRunData blackDuckRunData) throws IOException, IntegrationException {
        File bdioHeaderFile = new File(Application.class.getResource("/test-inputs/bdio-header.pb").getPath()); // temporary
        try (
            FileReader fileReader = new FileReader(bdioHeaderFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            return operationRunner.uploadBdioHeaderToInitiateStatelessScan(blackDuckRunData, bdioHeaderFile);
        }
    }


}
