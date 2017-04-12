package com.blackducksoftware.integration.hub.packman.parser.cocoapods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.hub.packman.parser.model.Package;

public class PodLock {

	public List<Package> pods = new ArrayList<Package>();
	public List<Package> dependencies = new ArrayList<Package>();
	public Map<String, String> specChecsums = new HashMap<String, String>();
	public String podfileChecksum;
	public String cococapodsVersion;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PODS:\n");
		for (Package p : pods) {
			builder.append("  - ");
			builder.append(p.name);
			builder.append(" (");
			builder.append(p.version);
			builder.append(")\n");
			for (Package dep : p.dependencies) {
				builder.append("    - ");
				builder.append(dep.name);
				builder.append(" (");
				builder.append(dep.version);
				builder.append(")\n");
			}
		}
		builder.append("\nDEPENDENCIES:\n");
		for (Package p : dependencies) {
			builder.append("  - ");
			builder.append(p.name);
			builder.append(" (");
			builder.append(p.version);
			builder.append(")\n");
		}
		builder.append("\nSPEC CHECKSUMS:\n");
		for (Package p : dependencies) {
			builder.append("  ");
			builder.append(p.name);
			builder.append(": ");
			builder.append(specChecsums.get(p.name));
			builder.append("\n");
		}
		builder.append("\nPODFILE CHECKSUM: " + podfileChecksum + "\n");
		builder.append("\nCOCOAPODS: " + cococapodsVersion + "\n");
		return builder.toString();
	}
}
