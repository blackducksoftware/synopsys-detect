package com.synopsys.integration.polaris.common.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.log.PrintStreamIntLogger;
import com.synopsys.integration.polaris.common.api.auth.model.group.GroupResource;
import com.synopsys.integration.polaris.common.api.auth.model.user.UserResource;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfigBuilder;

public class UserServiceTest {
    @Test
    public void callGetAllUsersAndGetEmailTest() throws IntegrationException {
        final PolarisServerConfigBuilder polarisServerConfigBuilder = PolarisServerConfig.newBuilder();
        polarisServerConfigBuilder.setUrl(System.getenv("POLARIS_URL"));
        polarisServerConfigBuilder.setAccessToken(System.getenv("POLARIS_ACCESS_TOKEN"));
        polarisServerConfigBuilder.setGson(new Gson());

        assumeTrue(StringUtils.isNotBlank(polarisServerConfigBuilder.getUrl()));
        assumeTrue(StringUtils.isNotBlank(polarisServerConfigBuilder.getAccessToken()));

        final PolarisServerConfig polarisServerConfig = polarisServerConfigBuilder.build();
        final IntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.INFO);
        final PolarisServicesFactory polarisServicesFactory = polarisServerConfig.createPolarisServicesFactory(logger);

        final UserService userService = polarisServicesFactory.createUserService();
        final List<UserResource> users = userService.getAllUsers();

        if (!users.isEmpty()) {
            final UserResource user = users
                                          .stream()
                                          .findAny()
                                          .orElseThrow(() -> new AssertionError("Missing list element"));
            final Optional<String> optionalEmail = userService.getEmailForUser(user);
            optionalEmail.ifPresent(email -> assertTrue(StringUtils.isNotBlank(email), "Expected email not to be blank"));
        }
    }

    @Test
    public void callGetUsersForGroupTest() throws IntegrationException {
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

        final GroupResource groupResource;
        try {
            final Optional<GroupResource> optionalGroupResource = groupService.getGroupByName("IntegrationsTeam");
            if (optionalGroupResource.isPresent()) {
                groupResource = optionalGroupResource.get();
            } else {
                groupResource = groupService.getAllGroups()
                                    .stream()
                                    .findAny()
                                    .orElseThrow(IntegrationException::new);
            }
        } catch (final IntegrationException e) {
            assumeTrue(e != null, "Something went wrong while retrieving groups, but this test is not for the group service");
            return;
        }

        final UserService userService = polarisServicesFactory.createUserService();
        userService.getUsersForGroup(groupResource);
    }

}
