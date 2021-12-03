package com.synopsys.integration.detect.workflow.blackduck.project;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.generated.view.TagView;
import com.synopsys.integration.blackduck.service.dataservice.TagService;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.exception.IntegrationException;

public class AddTagsToProjectOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final TagService tagService;

    public AddTagsToProjectOperation(TagService tagService) {
        this.tagService = tagService;
    }

    public void addTagsToProject(ProjectVersionWrapper projectVersionWrapper, List<String> tags) throws IntegrationException {
        List<String> validTags = tags.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
        if (validTags.size() > 0) {
            List<TagView> currentTags = tagService.getAllTags(projectVersionWrapper.getProjectView());
            for (String tag : validTags) {
                boolean currentTagExists = currentTags.stream().anyMatch(tagView -> tagView.getName().equalsIgnoreCase(tag));
                if (!currentTagExists) {
                    logger.debug(String.format("Adding tag %s to project %s", tag, projectVersionWrapper.getProjectView().getName()));
                    TagView tagView = new TagView();
                    tagView.setName(tag);
                    tagService.createTag(projectVersionWrapper.getProjectView(), tagView);
                } else {
                    logger.debug(String.format("Skipping tag as it already exists %s", tag));
                }
            }
        }
    }
}
