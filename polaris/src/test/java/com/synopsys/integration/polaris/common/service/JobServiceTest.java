package com.synopsys.integration.polaris.common.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.log.PrintStreamIntLogger;
import com.synopsys.integration.polaris.common.api.job.model.JobResource;
import com.synopsys.integration.polaris.common.api.job.model.JobStatus;
import com.synopsys.integration.polaris.common.request.PolarisRequestFactory;
import com.synopsys.integration.polaris.common.rest.AccessTokenPolarisHttpClient;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.response.Response;

public class JobServiceTest {
    @Test
    public void testGetJobByUrl() throws IntegrationException {
        AccessTokenPolarisHttpClient polarisHttpClient = Mockito.mock(AccessTokenPolarisHttpClient.class);
        HttpUrl jobsApi = new HttpUrl("https://polaris.synopsys.example.com/api/jobs/jobs/p10t3j6grt67pabjgp89djvln4");
        mockClientBehavior(polarisHttpClient, jobsApi, "jobservice_status.json");

        PolarisJsonTransformer polarisJsonTransformer = new PolarisJsonTransformer(PolarisServicesFactory.createDefaultGson(), new PrintStreamIntLogger(System.out, LogLevel.INFO));
        PolarisService polarisService = new PolarisService(polarisHttpClient, polarisJsonTransformer, PolarisRequestFactory.DEFAULT_LIMIT);

        JobService jobService = new JobService(polarisHttpClient, polarisService);
        JobResource jobResource = jobService.getJobByUrl(jobsApi);
        JobStatus jobStatus = jobResource.getData().getAttributes().getStatus();

        assertEquals(Integer.valueOf(100), jobStatus.getProgress());
        assertEquals(JobStatus.StateEnum.COMPLETED, jobStatus.getState());
    }

    private void mockClientBehavior(AccessTokenPolarisHttpClient polarisHttpClient, HttpUrl uri, String results) {
        try {
            Response response = Mockito.mock(Response.class);
            Mockito.when(response.getContentString()).thenReturn(getPreparedContentStringFrom(results));

            ArgumentMatcher<Request> isMockedRequest = request -> null != request && request.getUrl().equals(uri);
            Mockito.when(polarisHttpClient.execute(Mockito.argThat(isMockedRequest))).thenReturn(response);
        } catch (IOException | IntegrationException e) {
            fail("Unexpected " + e.getClass() + " was thrown while mocking client behavior. Please check the test for errors.", e);
        }
    }

    private String getPreparedContentStringFrom(String resourceName) throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/JobService/" + resourceName), StandardCharsets.UTF_8);
    }
}
