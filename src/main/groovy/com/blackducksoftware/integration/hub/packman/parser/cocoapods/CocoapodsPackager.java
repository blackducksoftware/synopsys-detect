package com.blackducksoftware.integration.hub.packman.parser.cocoapods;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.blackducksoftware.integration.hub.packman.parser.model.Package;
import com.blackducksoftware.integration.hub.packman.parser.model.Packager;

public class CocoapodsPackager implements Packager {

    private final InputStream podfileStream;

    private final InputStream podlockStream;

    public CocoapodsPackager(final InputStream podfileStream, final InputStream podlockStream) {
        this.podfileStream = podfileStream;
        this.podlockStream = podlockStream;
    }

    @Override
    public Package makePackage() {
        final Package cocoapodsPackage = new Package();
        cocoapodsPackage.name = "dummy-name";
        cocoapodsPackage.version = "1.0.0";

        final PodLockParser podLockParser = new PodLockParser();
        final PodLock podLock = podLockParser.parse(podlockStream);
        cocoapodsPackage.dependencies = getDependencies(podLock);

        return cocoapodsPackage;
    }

    public List<Package> getDependencies(final PodLock podLock) {
        final List<Package> dependencies = new ArrayList<>();

        final Map<String, Package> allPods = new HashMap<>();
        for (final Package pod : podLock.pods) {
            allPods.put(pod.name, pod);
        }

        // Fix pods dependencies
        for (final Entry<String, Package> pod : allPods.entrySet()) {
            final List<Package> pod_deps = new ArrayList<>();
            for (final Package dependency : pod.getValue().dependencies) {
                pod_deps.add(allPods.get(dependency.name));
            }
            pod.getValue().dependencies = pod_deps;
        }

        for (final Package declaredDependency : podLock.dependencies) {
            final Package pod = allPods.get(declaredDependency.name);
            dependencies.add(pod);
        }
        return dependencies;
    }

}
