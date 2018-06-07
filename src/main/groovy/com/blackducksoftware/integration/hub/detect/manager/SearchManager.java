package com.blackducksoftware.integration.hub.detect.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.bomtool.search.report.SearchSummaryReporter;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.extraction.model.StrategyEvaluation;
import com.blackducksoftware.integration.hub.detect.manager.result.search.SearchResult;
import com.blackducksoftware.integration.hub.detect.manager.result.search.SearchResultBomToolFailed;
import com.blackducksoftware.integration.hub.detect.manager.result.search.SearchResultSuccess;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.search.BomToolFinder;
import com.blackducksoftware.integration.hub.detect.search.BomToolFinderOptions;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.StrategyManager;
import com.blackducksoftware.integration.util.ExcludedIncludedFilter;

@Component
public class SearchManager {
    private final Logger logger = LoggerFactory.getLogger(SearchManager.class);

    @Autowired
    private SearchSummaryReporter searchSummaryReporter;

    @Autowired
    private StrategyManager strategyManager;

    @Autowired
    private DetectConfiguration detectConfiguration;

    @Autowired
    private DetectPhoneHomeManager detectPhoneHomeManager;

    private List<StrategyEvaluation> findApplicableBomTools(final File directory) throws BomToolException, DetectUserFriendlyException {
        final List<Strategy> allStrategies = strategyManager.getAllStrategies();
        final List<String> excludedDirectories = detectConfiguration.getBomToolSearchDirectoryExclusions();
        final Boolean forceNestedSearch = detectConfiguration.getBomToolContinueSearch();
        final int maxDepth = detectConfiguration.getBomToolSearchDepth();
        final ExcludedIncludedFilter bomToolFilter = new ExcludedIncludedFilter(detectConfiguration.getExcludedBomToolTypes(), detectConfiguration.getIncludedBomToolTypes());
        final BomToolFinderOptions findOptions = new BomToolFinderOptions(excludedDirectories, forceNestedSearch, maxDepth, bomToolFilter);

        logger.info("Starting search for bom tools.");
        final BomToolFinder bomToolTreeWalker = new BomToolFinder();
        return bomToolTreeWalker.findApplicableBomTools(new HashSet<>(allStrategies), directory, findOptions);
    }

    public SearchResult performSearch() throws DetectUserFriendlyException {
        List<StrategyEvaluation> sourcePathResults = new ArrayList<>();
        try {
            sourcePathResults = findApplicableBomTools(new File(detectConfiguration.getSourcePath()));
        } catch (final BomToolException e) {
            return new SearchResultBomToolFailed(e);
        }

        searchSummaryReporter.print(sourcePathResults);

        final float appliedNotInSourceDirectory = sourcePathResults.stream()
                .filter(it -> it.isApplicable())
                .filter(it -> it.environment.getDepth() > 0)
                .count();

        if (appliedNotInSourceDirectory > 1) {
            if (StringUtils.isBlank(detectConfiguration.getProjectName())) {
                throw new DetectUserFriendlyException(
                        "Multiple bom tool types applied but no project name was supplied. Detect is unable to reasonably guess the project name and version. Please provide a project name and version with --detect.project.name and --detect.project.version",
                        ExitCodeType.FAILURE_CONFIGURATION);
            } else if (StringUtils.isBlank(detectConfiguration.getProjectVersionName())) {
                throw new DetectUserFriendlyException(
                        "Multiple bom tool types applied but no project version was supplied. Detect is unable to reasonably guess the project version. Please provide a project name with --detect.project.version",
                        ExitCodeType.FAILURE_CONFIGURATION);
            }
        }

        final Set<BomToolType> applicableBomTools = sourcePathResults.stream()
                .filter(it -> it.isApplicable())
                .map(it -> it.strategy.getBomToolType())
                .collect(Collectors.toSet());

        // we've gone through all applicable bom tools so we now have the complete metadata to phone home
        detectPhoneHomeManager.startPhoneHome(applicableBomTools);

        return new SearchResultSuccess(sourcePathResults);
    }
}
