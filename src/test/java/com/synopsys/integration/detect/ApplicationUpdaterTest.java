package com.synopsys.integration.detect;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.response.Response;

@Tag("integration")
public class ApplicationUpdaterTest {
    
    private final String[] args = new String[] {
            "-jar",
            "/fake/path/to/synopsys-detect-n.n.n.jar",
            "--blackduck.url=https://not.real.url.of.synopsys.com", 
            "--blackduck.trust.cert=true", 
            "--blackduck.api.token=dummyToken",
            "--detect.tools=DETECTOR"};
    
    @Disabled
    @Test
    public void testSelfUpdate() throws IntegrationException {

        DetectInfo detectInfo = Mockito.mock(DetectInfo.class);
        Mockito.when(detectInfo.getDetectVersion()).thenReturn("8.9.0");
        
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.getStatusCode()).thenReturn(200);
        Mockito.when(response.getHeaderValue(ApplicationUpdater.DOWNLOAD_VERSION_HEADER)).thenReturn("8.10.0");
        
        Request request = Mockito.mock(Request.class);
        IntHttpClient intHttpClient = Mockito.mock(IntHttpClient.class);
        Mockito.when(intHttpClient.execute(request)).thenReturn(response);
        
        ApplicationUpdater applicationUpdater = new ApplicationUpdater(args);
        boolean selfUpdated = applicationUpdater.selfUpdate();

        Assertions.assertTrue(selfUpdated);
    }
}
