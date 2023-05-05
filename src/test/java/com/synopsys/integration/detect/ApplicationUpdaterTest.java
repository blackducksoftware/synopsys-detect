package com.synopsys.integration.detect;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.response.Response;

public class ApplicationUpdaterTest {
    
    private final String[] args = new String[] {
            "-jar",
            "/fake/path/to/synopsys-detect-n.n.n.jar",
            "--blackduck.url=https://synopsys.com", 
            "--blackduck.trust.cert=true", 
            "--blackduck.api.token=dummyToken",
            "--detect.tools=DETECTOR"};

    @Disabled
    @Test
    public void testSelfUpdate() throws IntegrationException {
        Request request = Mockito.mock(Request.class);
        IntHttpClient intHttpClient = Mockito.mock(IntHttpClient.class);
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.getStatusCode()).thenReturn(200);
        Mockito.when(response.getHeaderValue(ApplicationUpdater.DOWNLOAD_VERSION_HEADER)).thenReturn("8.10.0");
        Mockito.when(intHttpClient.execute(request)).thenReturn(response);
        
        DetectInfo detectInfo = Mockito.mock(DetectInfo.class);
        Mockito.when(detectInfo.getDetectVersion()).thenReturn("8.9.0");
        ApplicationUpdaterUtility applicationUpdaterUtility = Mockito.mock(ApplicationUpdaterUtility.class);
        ApplicationUpdater applicationUpdater = new ApplicationUpdater(applicationUpdaterUtility, args);
        Mockito.when(applicationUpdaterUtility.getIntHttpClient()).thenReturn(intHttpClient);
        
        boolean selfUpdated = applicationUpdater.selfUpdate();

        Assertions.assertTrue(selfUpdated);
    }
}
