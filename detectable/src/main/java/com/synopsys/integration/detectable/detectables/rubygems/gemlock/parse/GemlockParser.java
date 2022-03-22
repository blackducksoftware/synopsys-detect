package com.synopsys.integration.detectable.detectables.rubygems.gemlock.parse;

import static com.synopsys.integration.detectable.detectables.rubygems.gemlock.parse.GemlockParser.GemfileLockSection.BUNDLED_WITH;
import static com.synopsys.integration.detectable.detectables.rubygems.gemlock.parse.GemlockParser.GemfileLockSection.DEPENDENCIES;
import static com.synopsys.integration.detectable.detectables.rubygems.gemlock.parse.GemlockParser.GemfileLockSection.NONE;
import static com.synopsys.integration.detectable.detectables.rubygems.gemlock.parse.GemlockParser.GemfileLockSection.SPECS;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.synopsys.integration.bdio.graph.builder.LazyId;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.util.NameVersion;

public class GemlockParser {
    public static final String DEPENDENCIES_HEADER = "DEPENDENCIES";
    public static final String BUNDLED_WITH_HEADER = "BUNDLED WITH";
    public static final String SPECS_HEADER = "specs:";

    public static final String SPEC_RELATIONSHIP_PREFIX = "      ";
    public static final String SPEC_PACKAGE_PREFIX = "    ";

    public static final String VERSION_CHARACTERS = "()<>=~";
    public static final String FUZZY_VERSION_CHARACTERS = "<>";
    public static final String VERSION_PREFIX_PATTERN = " \\(";
    public static final String VERSION_SUFFIX = ")";

    private final Logger logger = LoggerFactory.getLogger(GemlockParser.class);

    private final ExternalIdFactory externalIdFactory;
    private LazyExternalIdDependencyGraphBuilder lazyBuilder;
    private LazyId currentParent;

    private GemfileLockSection currentSection = NONE;

    private Set<String> encounteredDependencies = new HashSet<>();
    private Map<String, NameVersion> resolvedDependencies = new HashMap<>();

    public GemlockParser(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph parseProjectDependencies(List<String> gemfileLockLines) throws MissingExternalIdException {
        encounteredDependencies = new HashSet<>();
        resolvedDependencies = new HashMap<>();
        lazyBuilder = new LazyExternalIdDependencyGraphBuilder();
        currentParent = null;

        for (String line : gemfileLockLines) {
            String trimmedLine = StringUtils.trimToEmpty(line);

            if (StringUtils.isBlank(trimmedLine)) {
                currentSection = NONE;
            } else if (SPECS_HEADER.equals(trimmedLine)) {
                currentSection = SPECS;
            } else if (DEPENDENCIES_HEADER.equals(trimmedLine)) {
                currentSection = DEPENDENCIES;
            } else if (BUNDLED_WITH_HEADER.equals(trimmedLine)) {
                currentSection = BUNDLED_WITH;
            } else if (BUNDLED_WITH.equals(currentSection)) {
                addBundlerDependency(trimmedLine);
            } else if (SPECS.equals(currentSection)) {
                parseSpecsSectionLine(line);
            } else if (DEPENDENCIES.equals(currentSection)) {
                parseDependencySectionLine(trimmedLine);
            }
        }

        List<String> missingDependencies = encounteredDependencies.stream().filter(it -> !resolvedDependencies.containsKey(it)).collect(Collectors.toList());
        for (String missingName : missingDependencies) {
            final String missingVersion = "";
            LazyId dependencyId = LazyId.fromName(missingName);
            ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, missingName, missingVersion);
            lazyBuilder.setDependencyInfo(dependencyId, missingName, missingVersion, externalId);
        }

        return lazyBuilder.build();
    }

    private void discoveredDependencyInfo(LazyId id, String dependencyName, String dependencyVersion) {
        LazyId nameOnlyId = LazyId.fromName(dependencyName);

        //regardless we found the external id for this specific dependency.
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, dependencyName, dependencyVersion);
        lazyBuilder.setDependencyInfo(id, dependencyName, dependencyVersion, externalId);

        if (!resolvedDependencies.containsKey(dependencyName)) { //if this is our first time encountering a dependency of this name, we become the 'version-less'
            resolvedDependencies.put(dependencyName, new NameVersion(dependencyName, dependencyVersion));
            lazyBuilder.setDependencyInfo(nameOnlyId, dependencyName, dependencyVersion, externalId);
        } else {//otherwise, add us as a child to the version-less
            if (resolvedDependencies.containsKey(dependencyName)) {
                NameVersion nameVersion = resolvedDependencies.get(dependencyName);
                // if the current processed version found is different than the resolved dependency version then add it.
                // do not add the same version again to itself in the relationships which creates a circular dependency.
                if (!nameVersion.getVersion().equals(dependencyVersion)) {
                    lazyBuilder.addChildWithParent(id, nameOnlyId);
                }
            }
        }
    }

    private void addBundlerDependency(String trimmedLine) {
        String name = "bundler";
        LazyId bundlerId = LazyId.fromNameAndVersion(name, trimmedLine);
        discoveredDependencyInfo(bundlerId, name, trimmedLine);
    }

    private void parseSpecsSectionLine(String untrimmedLine) {
        if (untrimmedLine.startsWith(SPEC_RELATIONSHIP_PREFIX)) {
            parseSpecRelationshipLine(untrimmedLine.trim());
        } else if (untrimmedLine.startsWith(SPEC_PACKAGE_PREFIX)) {
            parseSpecPackageLine(untrimmedLine.trim());
        } else {
            logger.error(String.format("Line in specs section can't be parsed: %s", untrimmedLine));
        }
    }

    private void parseSpecRelationshipLine(String trimmedLine) {
        if (currentParent == null) {
            logger.error(String.format("Trying to add a child without a parent: %s", trimmedLine));
        } else {
            NameVersion childNameVersion = parseNameVersion(trimmedLine);
            LazyId childId = processNameVersion(childNameVersion);
            lazyBuilder.addChildWithParent(childId, currentParent);
        }
    }

    private void parseSpecPackageLine(String trimmedLine) {
        NameVersion parentNameVersion = parseNameVersion(trimmedLine);
        if (StringUtils.isNotBlank(parentNameVersion.getVersion())) {
            currentParent = LazyId.fromName(parentNameVersion.getName());
            discoveredDependencyInfo(
                LazyId.fromNameAndVersion(parentNameVersion.getName(), parentNameVersion.getVersion()),
                parentNameVersion.getName(),
                parentNameVersion.getVersion()
            );
        } else {
            logger.error(String.format("An installed spec did not have a non-fuzzy version: %s", trimmedLine));
        }
    }

    //If you have Version, you know everything. Otherwise, you need to find this version later.
    //Generally each parse/process call should either call this or add to encountered.
    private LazyId processNameVersion(NameVersion nameVersion) {
        LazyId nameLazyId = LazyId.fromName(nameVersion.getName());
        if (StringUtils.isNotBlank(nameVersion.getVersion())) {
            LazyId nameVersionLazyId = LazyId.fromNameAndVersion(nameVersion.getName(), nameVersion.getVersion());
            discoveredDependencyInfo(nameVersionLazyId, nameVersion.getName(), nameVersion.getVersion());
        } else {
            encounteredDependencies.add(nameVersion.getName());
        }
        return nameLazyId;
    }

    private void parseDependencySectionLine(String trimmedLine) {
        NameVersion dependencyNameVersionNode = parseNameVersion(trimmedLine);
        if (dependencyNameVersionNode.getName() == null) {
            logger.error(String.format("Line in dependencies section can't be parsed: %s", trimmedLine));
        } else {
            LazyId dependencyId = processNameVersion(dependencyNameVersionNode);
            lazyBuilder.addChildToRoot(dependencyId);
        }
    }

    private NameVersion parseNameVersion(String trimmedLine) {
        String[] pieces = trimmedLine.split(VERSION_PREFIX_PATTERN);
        String name = pieces[0].trim();
        String version = "";

        if (pieces.length > 1) {
            Optional<String> validVersion = parseValidVersion(pieces[1].trim());
            version = validVersion.orElse("");
        }

        if (name.endsWith("!")) {
            name = name.substring(0, name.length() - 1);
        }

        return new NameVersion(name, version);
    }

    // a valid version looks like (###.###.###)
    private Optional<String> parseValidVersion(String version) {
        String validVersion = null;

        if (version.endsWith(VERSION_SUFFIX) && StringUtils.containsNone(version, FUZZY_VERSION_CHARACTERS)) {
            validVersion = StringUtils.replaceChars(version, VERSION_CHARACTERS, "").trim();
        }

        return Optional.ofNullable(validVersion);
    }

    enum GemfileLockSection {
        BUNDLED_WITH,
        DEPENDENCIES,
        NONE,
        SPECS
    }

}
