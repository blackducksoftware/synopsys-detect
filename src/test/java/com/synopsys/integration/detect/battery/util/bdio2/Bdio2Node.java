package com.synopsys.integration.detect.battery.util.bdio2;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Bdio2Node {
    private final String id;
    private final String type;
    private final String identifier;
    private final String name;
    private final String version;
    private final String namespace;
    private final Set<String> dependencies;

    public Bdio2Node(JSONObject bdioNode) throws JSONException {
        id = getSafely(bdioNode, Bdio2Keys.id);
        type = getSafely(bdioNode, Bdio2Keys.type);
        identifier = getSafely(bdioNode, Bdio2Keys.hasIdentifier);
        name = getSafely(bdioNode, Bdio2Keys.hasName);
        version = getSafely(bdioNode, Bdio2Keys.hasVersion);
        namespace = getSafely(bdioNode, Bdio2Keys.hasNamespace);

        dependencies = new HashSet<>();
        if (bdioNode.has(Bdio2Keys.hasDependency)) {
            JSONArray children = bdioNode.optJSONArray(Bdio2Keys.hasDependency);
            if (children != null) {
                for (int i = 0; i < children.length(); i++) {
                    JSONObject child = children.getJSONObject(i);
                    if (child.has(Bdio2Keys.dependsOn)) {
                        JSONObject dep = child.getJSONObject(Bdio2Keys.dependsOn);
                        dependencies.add(getSafely(dep, Bdio2Keys.id));
                    }
                }
            }
            JSONObject singleChild = bdioNode.optJSONObject(Bdio2Keys.hasDependency);
            if (singleChild != null) {
                if (singleChild.has(Bdio2Keys.dependsOn)) {
                    JSONObject dep = singleChild.getJSONObject(Bdio2Keys.dependsOn);
                    dependencies.add(getSafely(dep, Bdio2Keys.id));
                }
            }
        }
    }

    private String getSafely(JSONObject obj, String key) {
        if (obj.has(key)) {
            try {
                return obj.getString(key);
            } catch (JSONException e) {
                return null;
            }
        }
        return null;
    }

    public String toDescription() {
        if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(version)) {
            return String.format("(%s, %s | identifier: %s)", name, version, identifier);
        } else if (StringUtils.isNotBlank(name)) {
            return String.format("(%s | identifier: %s)", name, identifier);
        } else {
            return String.format("(%s | identifier: %s)", id, identifier);
        }
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getNamespace() {
        return namespace;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }
}
