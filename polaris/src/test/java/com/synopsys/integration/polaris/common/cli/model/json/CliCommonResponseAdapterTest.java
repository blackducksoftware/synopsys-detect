package com.synopsys.integration.polaris.common.cli.model.json;

import com.google.gson.Gson;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.polaris.common.cli.PolarisCliResponseUtility;
import com.synopsys.integration.polaris.common.cli.model.CliCommonResponseModel;
import com.synopsys.integration.polaris.common.exception.PolarisIntegrationException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class CliCommonResponseAdapterTest {
    @Test
    public void testCreatingCommonFromV1WithoutIssueSummary() throws PolarisIntegrationException {
        String v1ResponseWithoutLicenseSummary = getContents("/cli-scanv1.json");
        PolarisCliResponseUtility polarisCliResponseUtility = new PolarisCliResponseUtility(new SilentIntLogger(), new Gson());

        CliCommonResponseModel cliCommonResponseModel = polarisCliResponseUtility.getPolarisCliResponseModelFromString(v1ResponseWithoutLicenseSummary);

        assertFalse(cliCommonResponseModel.getIssueSummary().isPresent());

        assertEquals("c6a77ec3-fed4-491d-ad80-912df6a1ed96", cliCommonResponseModel.getProjectInfo().getProjectId());
        assertEquals("6401f1fa-9c92-458a-bd24-eaaa820707f2", cliCommonResponseModel.getProjectInfo().getBranchId());
        assertEquals("67d50579-90fc-467a-b3dc-48b70f0c08d7", cliCommonResponseModel.getProjectInfo().getRevisionId());

        assertEquals("1.5.5527", cliCommonResponseModel.getScanInfo().getCliVersion());
        assertEquals("2020-04-01T14:36:11Z", cliCommonResponseModel.getScanInfo().getScanTime());
        assertEquals("https://jenkinsint.dev.polaris.synopsys.com/api/query/v0/roll-up-counts?branch-id=6401f1fa-9c92-458a-bd24-eaaa820707f2&filter%5Bissue%5D%5Bstatus%5D%5B%24eq%5D=opened&for-child-paths=false&group-by=%5Bissue%5D%5Btaxonomy%5D%5Bid%5D%5Bd0e3d4a1-c815-459f-b0b1-0b3d6d8773e7%5D&include%5Bissue%5D%5B%5D=severity&page%5Blimit%5D=25&page%5Boffset%5D=0&project-id=c6a77ec3-fed4-491d-ad80-912df6a1ed96", cliCommonResponseModel.getScanInfo().getIssueApiUrl());

        assertEquals(1, cliCommonResponseModel.getTools().size());
        assertEquals("Coverity", cliCommonResponseModel.getTools().get(0).getToolName());
        assertEquals("2019.12-2", cliCommonResponseModel.getTools().get(0).getToolVersion());
        assertEquals("p10t3j6grt67pabjgp89djvln4", cliCommonResponseModel.getTools().get(0).getJobId());
        assertEquals("https://jenkinsint.dev.polaris.synopsys.com/api/jobs/jobs/p10t3j6grt67pabjgp89djvln4", cliCommonResponseModel.getTools().get(0).getJobStatusUrl());
        assertEquals("QUEUED", cliCommonResponseModel.getTools().get(0).getJobStatus());
        assertFalse(cliCommonResponseModel.getTools().get(0).getIssueApiUrl().isPresent());
    }

    @Test
    public void testCreatingCommonFromV1WithIssueSummary() throws PolarisIntegrationException {
        String v1ResponseWithLicenseSummary = getContents("/cli-scanv1-w.json");
        PolarisCliResponseUtility polarisCliResponseUtility = new PolarisCliResponseUtility(new SilentIntLogger(), new Gson());

        CliCommonResponseModel cliCommonResponseModel = polarisCliResponseUtility.getPolarisCliResponseModelFromString(v1ResponseWithLicenseSummary);

        assertTrue(cliCommonResponseModel.getIssueSummary().isPresent());
        assertEquals(3, cliCommonResponseModel.getIssueSummary().get().getIssuesBySeverity().keySet().size());
        assertEquals(1, cliCommonResponseModel.getIssueSummary().get().getIssuesBySeverity().get("high").intValue());
        assertEquals(1, cliCommonResponseModel.getIssueSummary().get().getIssuesBySeverity().get("medium").intValue());
        assertEquals(7, cliCommonResponseModel.getIssueSummary().get().getIssuesBySeverity().get("low").intValue());
        assertEquals(9, cliCommonResponseModel.getIssueSummary().get().getTotalIssueCount().intValue());
        assertEquals("https://jenkinsint.dev.polaris.synopsys.com/projects/cb9e7146-b2ee-499d-9811-c3ced5174211/branches/e67e1663-343a-4ac5-bdcf-b49c68bdeff1", cliCommonResponseModel.getIssueSummary().get().getSummaryUrl());

        assertEquals("cb9e7146-b2ee-499d-9811-c3ced5174211", cliCommonResponseModel.getProjectInfo().getProjectId());
        assertEquals("e67e1663-343a-4ac5-bdcf-b49c68bdeff1", cliCommonResponseModel.getProjectInfo().getBranchId());
        assertEquals("1ff3e120-c4ad-43af-9613-b05af489f10e", cliCommonResponseModel.getProjectInfo().getRevisionId());

        assertEquals("1.5.5527", cliCommonResponseModel.getScanInfo().getCliVersion());
        assertEquals("2020-04-03T19:43:43Z", cliCommonResponseModel.getScanInfo().getScanTime());
        assertEquals("https://jenkinsint.dev.polaris.synopsys.com/api/query/v0/roll-up-counts?branch-id=e67e1663-343a-4ac5-bdcf-b49c68bdeff1&filter%5Bissue%5D%5Bstatus%5D%5B%24eq%5D=opened&for-child-paths=false&group-by=%5Bissue%5D%5Btaxonomy%5D%5Bid%5D%5Bd0e3d4a1-c815-459f-b0b1-0b3d6d8773e7%5D&include%5Bissue%5D%5B%5D=severity&page%5Blimit%5D=25&page%5Boffset%5D=0&project-id=cb9e7146-b2ee-499d-9811-c3ced5174211", cliCommonResponseModel.getScanInfo().getIssueApiUrl());

        assertEquals(1, cliCommonResponseModel.getTools().size());
        assertEquals("Coverity", cliCommonResponseModel.getTools().get(0).getToolName());
        assertEquals("2019.12-2", cliCommonResponseModel.getTools().get(0).getToolVersion());
        assertEquals("te8edjdkph5rl0hegh89s46294", cliCommonResponseModel.getTools().get(0).getJobId());
        assertEquals("https://jenkinsint.dev.polaris.synopsys.com/api/jobs/jobs/te8edjdkph5rl0hegh89s46294", cliCommonResponseModel.getTools().get(0).getJobStatusUrl());
        assertEquals("COMPLETED", cliCommonResponseModel.getTools().get(0).getJobStatus());
        assertFalse(cliCommonResponseModel.getTools().get(0).getIssueApiUrl().isPresent());
    }

    @Test
    public void testCreatingCommonFromV2WithoutIssueSummary() throws PolarisIntegrationException {
        String v1ResponseWithoutLicenseSummary = getContents("/cli-scanv2.json");
        PolarisCliResponseUtility polarisCliResponseUtility = new PolarisCliResponseUtility(new SilentIntLogger(), new Gson());

        CliCommonResponseModel cliCommonResponseModel = polarisCliResponseUtility.getPolarisCliResponseModelFromString(v1ResponseWithoutLicenseSummary);

        assertFalse(cliCommonResponseModel.getIssueSummary().isPresent());

        assertEquals("8a955054-b985-4a03-8c41-afbce93a87d4", cliCommonResponseModel.getProjectInfo().getProjectId());
        assertEquals("c474804c-c4a6-4523-af63-1084b73a737a", cliCommonResponseModel.getProjectInfo().getBranchId());
        assertEquals("885fc677-b9bb-4c3e-b947-5a617ae5cc17", cliCommonResponseModel.getProjectInfo().getRevisionId());

        assertEquals("1.6.99", cliCommonResponseModel.getScanInfo().getCliVersion());
        assertEquals("2020-04-03T20:29:57Z", cliCommonResponseModel.getScanInfo().getScanTime());
        assertEquals("https://dev01.dev.polaris.synopsys.com/api/query/v0/roll-up-counts?branch-id=c474804c-c4a6-4523-af63-1084b73a737a&filter%5Bissue%5D%5Bstatus%5D%5B%24eq%5D=opened&for-child-paths=false&group-by=%5Bissue%5D%5Btaxonomy%5D%5Bid%5D%5B20c02271-d628-41cf-8383-666a81d7cabc%5D&include%5Bissue%5D%5B%5D=severity&page%5Blimit%5D=25&page%5Boffset%5D=0&project-id=8a955054-b985-4a03-8c41-afbce93a87d4", cliCommonResponseModel.getScanInfo().getIssueApiUrl());

        assertEquals(1, cliCommonResponseModel.getTools().size());
        assertEquals("Coverity", cliCommonResponseModel.getTools().get(0).getToolName());
        assertEquals("2020.03", cliCommonResponseModel.getTools().get(0).getToolVersion());
        assertEquals("rrelrv3mf931p22jt60ud2lveo", cliCommonResponseModel.getTools().get(0).getJobId());
        assertEquals("https://dev01.dev.polaris.synopsys.com/api/jobs/jobs/rrelrv3mf931p22jt60ud2lveo", cliCommonResponseModel.getTools().get(0).getJobStatusUrl());
        assertEquals("FAILED", cliCommonResponseModel.getTools().get(0).getJobStatus());

        assertTrue(cliCommonResponseModel.getTools().get(0).getIssueApiUrl().isPresent());
        assertEquals("https://dev01.dev.polaris.synopsys.com/api/query/v0/roll-up-counts?branch-id=c474804c-c4a6-4523-af63-1084b73a737a&filter%5Bissue%5D%5Bstatus%5D%5B%24eq%5D=opened&filter%5Bissue%5D%5Btool%5D%5Bid%5D%5B%24eq%5D=a9d7fc0b-027f-4188-a068-4b4afdc63fa7&for-child-paths=false&group-by=%5Bissue%5D%5Btaxonomy%5D%5Bid%5D%5B20c02271-d628-41cf-8383-666a81d7cabc%5D&include%5Bissue%5D%5B%5D=severity&page%5Blimit%5D=25&page%5Boffset%5D=0&project-id=8a955054-b985-4a03-8c41-afbce93a87d4", cliCommonResponseModel.getTools().get(0).getIssueApiUrl().get());
    }

    @Test
    public void testCreatingCommonFromV2WithIssueSummary() throws PolarisIntegrationException {
        String v1ResponseWithoutLicenseSummary = getContents("/cli-scanv2-w.json");
        PolarisCliResponseUtility polarisCliResponseUtility = new PolarisCliResponseUtility(new SilentIntLogger(), new Gson());

        CliCommonResponseModel cliCommonResponseModel = polarisCliResponseUtility.getPolarisCliResponseModelFromString(v1ResponseWithoutLicenseSummary);

        assertTrue(cliCommonResponseModel.getIssueSummary().isPresent());
        assertEquals(4, cliCommonResponseModel.getIssueSummary().get().getIssuesBySeverity().keySet().size());
        assertEquals(0, cliCommonResponseModel.getIssueSummary().get().getIssuesBySeverity().get("critical").intValue());
        assertEquals(1, cliCommonResponseModel.getIssueSummary().get().getIssuesBySeverity().get("high").intValue());
        assertEquals(1, cliCommonResponseModel.getIssueSummary().get().getIssuesBySeverity().get("medium").intValue());
        assertEquals(7, cliCommonResponseModel.getIssueSummary().get().getIssuesBySeverity().get("low").intValue());
        assertEquals(9, cliCommonResponseModel.getIssueSummary().get().getTotalIssueCount().intValue());
        assertEquals("https://dev01.dev.polaris.synopsys.com/projects/8a955054-b985-4a03-8c41-afbce93a87d4/branches/c474804c-c4a6-4523-af63-1084b73a737a", cliCommonResponseModel.getIssueSummary().get().getSummaryUrl());

        assertEquals("8a955054-b985-4a03-8c41-afbce93a87d4", cliCommonResponseModel.getProjectInfo().getProjectId());
        assertEquals("c474804c-c4a6-4523-af63-1084b73a737a", cliCommonResponseModel.getProjectInfo().getBranchId());
        assertEquals("885fc677-b9bb-4c3e-b947-5a617ae5cc17", cliCommonResponseModel.getProjectInfo().getRevisionId());

        assertEquals("1.6.99", cliCommonResponseModel.getScanInfo().getCliVersion());
        assertEquals("2020-04-03T20:55:03Z", cliCommonResponseModel.getScanInfo().getScanTime());
        assertEquals("https://dev01.dev.polaris.synopsys.com/api/query/v0/roll-up-counts?branch-id=c474804c-c4a6-4523-af63-1084b73a737a&filter%5Bissue%5D%5Bstatus%5D%5B%24eq%5D=opened&for-child-paths=false&group-by=%5Bissue%5D%5Btaxonomy%5D%5Bid%5D%5B20c02271-d628-41cf-8383-666a81d7cabc%5D&include%5Bissue%5D%5B%5D=severity&page%5Blimit%5D=25&page%5Boffset%5D=0&project-id=8a955054-b985-4a03-8c41-afbce93a87d4", cliCommonResponseModel.getScanInfo().getIssueApiUrl());

        assertEquals(1, cliCommonResponseModel.getTools().size());
        assertEquals("Coverity", cliCommonResponseModel.getTools().get(0).getToolName());
        assertEquals("2020.03", cliCommonResponseModel.getTools().get(0).getToolVersion());
        assertEquals("n2u8nionlh2lb7eavdtrva7ukk", cliCommonResponseModel.getTools().get(0).getJobId());
        assertEquals("https://dev01.dev.polaris.synopsys.com/api/jobs/jobs/n2u8nionlh2lb7eavdtrva7ukk", cliCommonResponseModel.getTools().get(0).getJobStatusUrl());
        assertEquals("COMPLETED", cliCommonResponseModel.getTools().get(0).getJobStatus());

        assertTrue(cliCommonResponseModel.getTools().get(0).getIssueApiUrl().isPresent());
        assertEquals("https://dev01.dev.polaris.synopsys.com/api/query/v0/roll-up-counts?branch-id=c474804c-c4a6-4523-af63-1084b73a737a&filter%5Bissue%5D%5Bstatus%5D%5B%24eq%5D=opened&filter%5Bissue%5D%5Btool%5D%5Bid%5D%5B%24eq%5D=a9d7fc0b-027f-4188-a068-4b4afdc63fa7&for-child-paths=false&group-by=%5Bissue%5D%5Btaxonomy%5D%5Bid%5D%5B20c02271-d628-41cf-8383-666a81d7cabc%5D&include%5Bissue%5D%5B%5D=severity&page%5Blimit%5D=25&page%5Boffset%5D=0&project-id=8a955054-b985-4a03-8c41-afbce93a87d4", cliCommonResponseModel.getTools().get(0).getIssueApiUrl().get());
    }

    private String getContents(String resourceName) {
        try {
            return IOUtils.toString(this.getClass().getResourceAsStream(resourceName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            fail(String.format("Could not get the resource: %s", resourceName), e);
        }

        return null;
    }

}
