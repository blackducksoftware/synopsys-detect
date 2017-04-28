package com.blackducksoftware.integration.hub.packman.testutils;

import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class ExternalIdTypeAdapter extends TypeAdapter<ExternalId> {
    private static final EnumSet<Forge> nameVersionForges = EnumSet.of(Forge.cocoapods, Forge.npm, Forge.pypi, Forge.rubygems);

    private final Gson gson = new Gson();

    @Override
    public void write(final JsonWriter out, final ExternalId value) throws IOException {
        final TypeAdapter defaultAdapter = gson.getAdapter(value.getClass());
        defaultAdapter.write(out, value);
    }

    @Override
    public ExternalId read(final JsonReader in) throws IOException {
        Forge forge = null;
        final Map<String, String> otherProperties = new HashMap<>();
        in.beginObject();
        while (in.hasNext()) {
            final String fieldName = in.nextName();
            final String fieldValue = in.nextString();
            if (fieldName.equals("forge")) {
                forge = Forge.valueOf(fieldValue);
            } else {
                otherProperties.put(fieldName, fieldValue);
            }
        }
        in.endObject();

        if (Forge.maven == forge) {
            return new MavenExternalId(otherProperties.get("group"), otherProperties.get("name"), otherProperties.get("version"));
        } else if (nameVersionForges.contains(forge)) {
            return new NameVersionExternalId(forge, otherProperties.get("name"), otherProperties.get("version"));
        }
        return null;
    }

}
