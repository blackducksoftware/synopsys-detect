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

	private InputStream podfileStream;
	private InputStream podlockStream;

	public CocoapodsPackager(InputStream podfileStream, InputStream podlockStream) {
		this.podfileStream = podfileStream;
		this.podlockStream = podlockStream;
	}

	@Override
	public Package makePackage() {
		Package cocoapodsPackage = new Package();
		cocoapodsPackage.name = "dummy-name";
		cocoapodsPackage.version = "1.0.0";

		PodLockParser podLockParser = new PodLockParser();
		PodLock podLock = podLockParser.parse(podlockStream);
		cocoapodsPackage.dependencies = getDependencies(podLock);

		return cocoapodsPackage;
	}

	public List<Package> getDependencies(PodLock podLock) {
		List<Package> dependencies = new ArrayList<Package>();

		Map<String, Package> allPods = new HashMap<String, Package>();
		for (Package pod : podLock.pods) {
			allPods.put(pod.name, pod);
		}

		// Fix pods dependencies
		for (Entry<String, Package> pod : allPods.entrySet()) {
			List<Package> pod_deps = new ArrayList<Package>();
			for (Package dependency : pod.getValue().dependencies) {
				pod_deps.add(allPods.get(dependency.name));
			}
			pod.getValue().dependencies = pod_deps;
		}

		for (Package declaredDependency : podLock.dependencies) {
			Package pod = allPods.get(declaredDependency.name);
			dependencies.add(pod);
		}
		return dependencies;
	}

}
