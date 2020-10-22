package com.synopsys.integration.polaris.common.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import com.google.gson.Gson;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.log.PrintStreamIntLogger;
import com.synopsys.integration.polaris.common.api.auth.model.group.GroupResource;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfigBuilder;

public class GroupServiceTest {
    @Test
    public void callGetAllGroupsAndGetNameTest() throws IntegrationException {
        final PolarisServerConfigBuilder polarisServerConfigBuilder = PolarisServerConfig.newBuilder();
        polarisServerConfigBuilder.setUrl(System.getenv("POLARIS_URL"));
        polarisServerConfigBuilder.setAccessToken(System.getenv("POLARIS_ACCESS_TOKEN"));
        polarisServerConfigBuilder.setGson(new Gson());

        assumeTrue(StringUtils.isNotBlank(polarisServerConfigBuilder.getUrl()));
        assumeTrue(StringUtils.isNotBlank(polarisServerConfigBuilder.getAccessToken()));

        final PolarisServerConfig polarisServerConfig = polarisServerConfigBuilder.build();
        final IntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.INFO);
        final PolarisServicesFactory polarisServicesFactory = polarisServerConfig.createPolarisServicesFactory(logger);

        final GroupService groupService = polarisServicesFactory.createGroupService();
        final List<GroupResource> groups = groupService.getAllGroups();
        if (!groups.isEmpty()) {
            final Optional<GroupResource> optionalGroup = groups
                                                              .stream()
                                                              .findAny();
            if (optionalGroup.isPresent()) {
                final GroupResource randomGroup = optionalGroup.get();
                final String groupName = randomGroup.getAttributes().getGroupname();
                final Optional<GroupResource> optionalGroupByName = groupService.getGroupByName(groupName);
                assertTrue(optionalGroupByName::isPresent);
                assertEquals(randomGroup.getId(), optionalGroupByName.map(GroupResource::getId).get());
            }
        }
    }

}
