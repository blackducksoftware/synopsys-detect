package com.synopsys.integration.detect.lifecycle.run.step;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.synopsys.integration.blackduck.version.BlackDuckVersion;
import com.synopsys.integration.detect.lifecycle.OperationException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationRunner;
import com.synopsys.integration.detect.workflow.blackduck.project.options.ProjectGroupOptions;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.response.Response;
import com.synopsys.integration.util.NameVersion;

public class ThreatIntelScanStepRunnerTest {
    
    @Mock
    private OperationRunner operationRunner;
    
    @Mock
    private BlackDuckRunData blackDuckRunData;
    
    @Mock
    private NameVersion projectNameVersion;
    
    private ThreatIntelScanStepRunner threatIntelRunner;
    
    private File rootDirectory;
    private File scannableFile;
    
    @BeforeEach
    public void setup() throws IOException {
        MockitoAnnotations.openMocks(this);
        
        rootDirectory = Files.createTempDirectory("ThreatIntelRunnerTest").toFile();      
        scannableFile = new File(rootDirectory.toString() + "/path");
        scannableFile.createNewFile();
        DirectoryManager directoryManager = mock(DirectoryManager.class);
        when(operationRunner.getDirectoryManager()).thenReturn(directoryManager); 
        when(directoryManager.getThreatIntelOutputDirectory()).thenReturn(rootDirectory); 
        
        when(operationRunner.calculateProjectGroupOptions()).thenReturn(mock(ProjectGroupOptions.class));
        
        threatIntelRunner = new ThreatIntelScanStepRunner(operationRunner, blackDuckRunData, projectNameVersion);
    }
    
    @AfterEach
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(rootDirectory);
    }

    @Test
    public void testNoScanFilePath() throws IOException {        
        when(operationRunner.getThreatIntelScanFilePath()).thenReturn(Optional.empty());
        
        Optional<UUID> scanId = threatIntelRunner.invokeThreatIntelWorkflow();
        
        assertEquals(Optional.empty(), scanId);
    }
    
    @Test
    public void testBadBlackDuckVersion() {
        when(operationRunner.getThreatIntelScanFilePath()).thenReturn(Optional.of(scannableFile.toString()));
        
        // Needs to be 2024, 4, 0 or later
        when(blackDuckRunData.getBlackDuckServerVersion()).thenReturn(Optional.of(new BlackDuckVersion(2023, 4, 0)));
        
        Optional<UUID> scanId = threatIntelRunner.invokeThreatIntelWorkflow();
        
        assertEquals(Optional.empty(), scanId);
        
        ArgumentCaptor<Exception> argument = ArgumentCaptor.forClass(Exception.class);
        verify(operationRunner).publishThreatIntelFailure(argument.capture());
        
        String failure = "Threat Intel scan is only supported with BlackDuck version 2024.4.0 or greater. Threat Intel scan could not be run.";
        
        assertEquals(failure, argument.getValue().getMessage());
    }
    
    @Test
    public void testThreatIntelScanSuccess() throws OperationException, IntegrationException {
        when(operationRunner.getThreatIntelScanFilePath()).thenReturn(Optional.of(scannableFile.toString()));
        when(blackDuckRunData.getBlackDuckServerVersion()).thenReturn(Optional.of(new BlackDuckVersion(2024, 4, 0)));
        when(operationRunner.getCodeLocationNameManager()).thenReturn(mock(CodeLocationNameManager.class));
        when(operationRunner.uploadBdioHeaderToInitiateScan(any(), any(), any())).thenReturn(UUID.randomUUID());
       
        Response mockResponse = mock(Response.class);
        when(mockResponse.isStatusCodeSuccess()).thenReturn(true);
       
        when(operationRunner.uploadFileToStorageServiceWithHeaders(any(), any(), any(), any(), any(), any())).thenReturn(mockResponse);
        
        Optional<UUID> scanId = threatIntelRunner.invokeThreatIntelWorkflow();
        
        assertTrue(scanId.isPresent());    
    }
}
