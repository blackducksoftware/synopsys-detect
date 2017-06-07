package com.blackducksoftware.integration.hub.detect;

import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class ExternalIdTypeAdapter extends TypeAdapter<ExternalId> {
    def forgeMap = [cocoapods: Forge.COCOAPODS, maven: Forge.MAVEN, npm: Forge.NPM, pypi: Forge.PYPI, rubygems: Forge.RUBYGEMS]
    def gson = new Gson()

    @Override
    void write(final JsonWriter jsonWriter, final ExternalId value) throws IOException {
        final TypeAdapter defaultAdapter = gson.getAdapter(value.getClass())
        defaultAdapter.write(jsonWriter, value)
    }

    @Override
    ExternalId read(final JsonReader jsonReader) throws IOException {
        String forgeName = null
        final Map<String, String> otherProperties = [:]
        jsonReader.beginObject()
        while (jsonReader.hasNext()) {
            final String fieldName = jsonReader.nextName()
            final String fieldValue = jsonReader.nextString()
            if (fieldName.equals("forge")) {
                forgeName = fieldValue
            } else {
                otherProperties.put(fieldName, fieldValue)
            }
        }
        jsonReader.endObject()

        Forge forge = forgeMap[forgeName]
        if (Forge.MAVEN.equals(forge)) {
            return new MavenExternalId(otherProperties.get("group"), otherProperties.get("name"), otherProperties.get("version"))
        } else if (forgeMap.containsKey(forgeName)) {
            return new NameVersionExternalId(forge, otherProperties.get("name"), otherProperties.get("version"))
        }
        return null
    }
}
