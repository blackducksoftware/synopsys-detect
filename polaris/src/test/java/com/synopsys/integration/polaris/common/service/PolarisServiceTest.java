package com.synopsys.integration.polaris.common.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.opentest4j.AssertionFailedError;

import com.google.gson.Gson;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.log.PrintStreamIntLogger;
import com.synopsys.integration.polaris.common.api.PolarisResources;
import com.synopsys.integration.polaris.common.api.PolarisResourcesPagination;
import com.synopsys.integration.polaris.common.api.common.model.branch.BranchV0Resource;
import com.synopsys.integration.polaris.common.api.common.model.branch.BranchV0Resources;
import com.synopsys.integration.polaris.common.api.common.model.project.ProjectV0Resource;
import com.synopsys.integration.polaris.common.api.common.model.project.ProjectV0Resources;
import com.synopsys.integration.polaris.common.request.PolarisPagedRequestCreator;
import com.synopsys.integration.polaris.common.request.PolarisPagedRequestWrapper;
import com.synopsys.integration.polaris.common.request.PolarisRequestFactory;
import com.synopsys.integration.polaris.common.rest.AccessTokenPolarisHttpClient;
import com.synopsys.integration.polaris.common.rest.AccessTokenPolarisHttpClientTestIT;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.response.Response;
import com.synopsys.integration.rest.support.AuthenticationSupport;

public class PolarisServiceTest {
    public static String PAGE_ONE_OFFSET = "0";
    public static String PAGE_TWO_OFFSET = "25";
    public static String PAGE_THREE_OFFSET = "50";

    private HttpUrl BASE_URL = new HttpUrl("https://google.com");

    public PolarisServiceTest() throws IntegrationException {
    }

    @Test
    public void createDefaultPolarisGetRequestTest() throws IntegrationException {
        Request request = PolarisRequestFactory.createDefaultPolarisPagedGetRequest(BASE_URL.string());
        assertNotNull(request);
    }

    @Test
    public void executeGetRequestTestIT() throws IntegrationException {
        String baseUrl = System.getenv(AccessTokenPolarisHttpClientTestIT.ENV_POLARIS_URL);
        String accessToken = System.getenv(AccessTokenPolarisHttpClientTestIT.ENV_POLARIS_ACCESS_TOKEN);

        assumeTrue(StringUtils.isNotBlank(baseUrl));
        assumeTrue(StringUtils.isNotBlank(accessToken));

        Gson gson = new Gson();
        IntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.INFO);
        AccessTokenPolarisHttpClient httpClient = new AccessTokenPolarisHttpClient(logger, 100, true, ProxyInfo.NO_PROXY_INFO, new HttpUrl(baseUrl), accessToken, gson, new AuthenticationSupport());

        PolarisService polarisService = new PolarisService(httpClient, new PolarisJsonTransformer(gson, logger), PolarisRequestFactory.DEFAULT_LIMIT);

        String requestUri = baseUrl + "/api/common/v0/branches";
        Request request = PolarisRequestFactory.createDefaultPolarisPagedGetRequest(requestUri);

        PolarisResources<BranchV0Resource> branchV0Resources = polarisService.get(BranchV0Resources.class, request);
        List<BranchV0Resource> branchV0ResourceList = branchV0Resources.getData();
        assertNotNull(branchV0ResourceList);
        PolarisResourcesPagination meta = branchV0Resources.getMeta();
        assertNotNull(meta);
    }

    @ParameterizedTest
    @MethodSource("createGetAllMockData")
    public void testGetAll(Map<String, String> offsetsToResults, int expectedTotal) throws IntegrationException {
        final HttpUrl requestUri = BASE_URL.appendRelativeUrl(PolarisService.PROJECT_API_SPEC);

        AccessTokenPolarisHttpClient polarisHttpClient = Mockito.mock(AccessTokenPolarisHttpClient.class);
        mockClientBehavior(polarisHttpClient, requestUri, offsetsToResults, "projects_no_more_results.json");

        PolarisJsonTransformer polarisJsonTransformer = new PolarisJsonTransformer(PolarisServicesFactory.createDefaultGson(), new PrintStreamIntLogger(System.out, LogLevel.INFO));

        PolarisPagedRequestCreator requestCreator = (limit, offset) -> PolarisRequestFactory.createDefaultPagedRequestBuilder(limit, offset)
                                                                           .url(requestUri)
                                                                           .build();

        PolarisPagedRequestWrapper pagedRequestWrapper = new PolarisPagedRequestWrapper(requestCreator, ProjectV0Resources.class);

        PolarisService polarisService = new PolarisService(polarisHttpClient, polarisJsonTransformer, PolarisRequestFactory.DEFAULT_LIMIT);
        try {
            List<ProjectV0Resource> allPagesResponse = polarisService.getAllResponses(pagedRequestWrapper);
            assertEquals(expectedTotal, allPagesResponse.size());
        } catch (IntegrationException e) {
            fail("Mocked response caused PolarisService::GetAllResponses to throw an unexpected IntegrationException, which should never happen in this test.", e);
        }
    }

    private static Stream<Arguments> createGetAllMockData() {
        Map<String, String> getAllOnOnePageMap = new HashMap<>();
        getAllOnOnePageMap.put(PAGE_ONE_OFFSET, "projects_all_on_one_page.json");

        Map<String, String> getAllMultiPageMap = new HashMap<>();
        getAllMultiPageMap.put(PAGE_ONE_OFFSET, "projects_page_1_of_3.json");
        getAllMultiPageMap.put(PAGE_TWO_OFFSET, "projects_page_2_of_3.json");
        getAllMultiPageMap.put(PAGE_THREE_OFFSET, "projects_page_3_of_3.json");

        Map<String, String> lessProjectsThanExpectedMap = new HashMap<>(getAllMultiPageMap);
        lessProjectsThanExpectedMap.remove(PAGE_THREE_OFFSET);

        Map<String, String> changingTotalMap = new HashMap<>(getAllMultiPageMap);
        changingTotalMap.put(PAGE_TWO_OFFSET, "projects_page_2_of_2.json");

        Map<String, String> duplicatedDataMap = new HashMap<>(getAllMultiPageMap);
        duplicatedDataMap.put(PAGE_TWO_OFFSET, "projects_page_1_of_3.json");

        return Stream.of(
            Arguments.of(getAllOnOnePageMap, 16),
            Arguments.of(getAllMultiPageMap, 66),
            Arguments.of(lessProjectsThanExpectedMap, 50),
            Arguments.of(changingTotalMap, 66),
            Arguments.of(duplicatedDataMap, 66)
        );
    }

    private void mockClientBehavior(AccessTokenPolarisHttpClient polarisHttpClient, HttpUrl url, Map<String, String> offsetsToResults, String emptyResultsPage) {
        try {
            for (Map.Entry<String, String> entry : offsetsToResults.entrySet()) {
                Response response = Mockito.mock(Response.class);
                Mockito.when(response.getContentString()).thenReturn(getPreparedContentStringFrom(entry.getValue()));

                ArgumentMatcher<Request> isMockedRequest = request -> requestMatches(request, url, entry.getKey());
                Mockito.when(polarisHttpClient.execute(Mockito.argThat(isMockedRequest))).thenReturn(response);
            }

            Response emptyResponse = Mockito.mock(Response.class);
            Mockito.when(emptyResponse.getContentString()).thenReturn(getPreparedContentStringFrom(emptyResultsPage));
            ArgumentMatcher<Request> isOutOfBounds = request -> requestOffsetOutOfBounds(request, url, offsetsToResults);

            Mockito.when(polarisHttpClient.execute(Mockito.argThat(isOutOfBounds))).thenReturn(emptyResponse)
                .thenThrow(new AssertionFailedError("Client requested more pages after getting back a page of empty results."));
        } catch (IOException | IntegrationException e) {
            fail("Unexpected " + e.getClass() + " was thrown while mocking client behavior. Please check the test for errors.", e);
        }
    }

    private Boolean requestMatches(Request request, HttpUrl url, String offset) {
        if (null != request && request.getUrl().equals(url)) {
            return request.getQueryParameters()
                       .get(PolarisRequestFactory.OFFSET_PARAMETER)
                       .stream()
                       .allMatch(requestOffset -> requestOffset.equals(offset));
        }
        return false;
    }

    private Boolean requestOffsetOutOfBounds(Request request, HttpUrl url, Map<String, String> offsetsToResults) {
        if (null != request && request.getUrl().equals(url)) {
            return request.getQueryParameters()
                       .get(PolarisRequestFactory.OFFSET_PARAMETER)
                       .stream()
                       .noneMatch(offsetsToResults::containsKey);
        }
        return false;
    }

    private String getPreparedContentStringFrom(String resourceName) throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/PolarisService/" + resourceName), StandardCharsets.UTF_8);
    }

}
