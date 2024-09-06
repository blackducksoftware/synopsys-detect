package com.synopsys.integration.detect.lifecycle.run.step;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.gson.Gson;
import com.synopsys.blackduck.upload.client.uploaders.ContainerUploader;
import com.synopsys.blackduck.upload.client.uploaders.UploaderFactory;
import com.synopsys.blackduck.upload.rest.status.DefaultUploadStatus;
import com.synopsys.integration.blackduck.version.BlackDuckVersion;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationRunner;
import com.synopsys.integration.detect.workflow.blackduck.project.options.ProjectGroupOptions;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ContainerScanStepRunnerTest {
    @Mock
    private ContainerUploader mockContainerUploader;
    
    @Mock
    private File mockContainerImage;
    
    private ContainerScanStepRunner instance;
    
    @BeforeAll
    public void initMocks() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        NameVersion mockNameVersion = mock(NameVersion.class);
        OperationRunner mockOperationRunner = mock(OperationRunner.class);
        DirectoryManager mockDirectoryManager = mock(DirectoryManager.class);
        File mockBinaryOutputDirectory = mock(File.class);
        ProjectGroupOptions mockProjectGroupOptions = mock(ProjectGroupOptions.class);
        BlackDuckRunData mockBlackDuckRunData = mock(BlackDuckRunData.class);
        BlackDuckVersion mockBlackDuckVersion = mock(BlackDuckVersion.class);
        UploaderFactory mockUploadFactory = mock(UploaderFactory.class);

        when(mockOperationRunner.calculateProjectGroupOptions()).thenReturn(mockProjectGroupOptions);
        String expectedProjectGroupName = "expectedProjectGroupName";
        when(mockProjectGroupOptions.getProjectGroup()).thenReturn(expectedProjectGroupName);
        when(mockOperationRunner.getDirectoryManager()).thenReturn(mockDirectoryManager);
        when(mockDirectoryManager.getBinaryOutputDirectory()).thenReturn(mockBinaryOutputDirectory);
        when(mockBinaryOutputDirectory.exists()).thenReturn(true);
        when(mockBlackDuckRunData.getBlackDuckServerVersion()).thenReturn(Optional.of(mockBlackDuckVersion));
        
        // We are going to say false here to avoid mocking and testing a whole bunch of library specific init
        // code that the ContainerScanStepRunner constructor does.
        // We'll call the method we are after directly in the method.invoke call below.
        when(mockBlackDuckVersion.isAtLeast(new BlackDuckVersion(2024, 10, 0))).thenReturn(false);
        
        when(mockUploadFactory.createContainerUploader(Mockito.anyString())).thenReturn(mockContainerUploader);
        
        when(mockOperationRunner.getContainerScanImage(Mockito.any(), Mockito.any())).thenReturn(mockContainerImage);
     
        instance = new ContainerScanStepRunner(mockOperationRunner, mockNameVersion, mockBlackDuckRunData, new Gson());
        
        Field fieldScan = ContainerScanStepRunner.class.getDeclaredField("scanId");
        fieldScan.setAccessible(true);
        UUID scanId = UUID.randomUUID();
        fieldScan.set(instance, scanId);

        Field fieldFactory = ContainerScanStepRunner.class.getDeclaredField("uploadFactory");
        fieldFactory.setAccessible(true);
        fieldFactory.set(instance, mockUploadFactory);
    }

    @Test
    public void testContainerScanSuccessResult() throws Exception {        
        DefaultUploadStatus expectedStatus = new DefaultUploadStatus(204, "", null);
        when(mockContainerUploader.upload(mockContainerImage.toPath())).thenReturn(expectedStatus);

        Method method = ContainerScanStepRunner.class.getDeclaredMethod("multiPartUploadImage");
        method.setAccessible(true);

        DefaultUploadStatus status = (DefaultUploadStatus) method.invoke(instance);

        assertEquals(204, status.getStatusCode());
    }
    
    @Test
    public void testContainerScanFailureResult() throws Exception {
        IntegrationException exception = new IntegrationException("Upload failed.");
        DefaultUploadStatus expectedStatus = new DefaultUploadStatus(500, "", exception);
        when(mockContainerUploader.upload(mockContainerImage.toPath())).thenReturn(expectedStatus);
        
        Method method = ContainerScanStepRunner.class.getDeclaredMethod("multiPartUploadImage");
        method.setAccessible(true);

        try {
            method.invoke(instance);
        } catch (Exception e) {
            assertEquals("Upload failed.", e.getCause().getMessage());  
        }        
    }
    
}
