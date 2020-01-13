package com.synopsys.integration.detectable.detectables.pip.parser

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.synopsys.integration.detectable.detectables.pip.model.PipenvGraph
import com.synopsys.integration.detectable.detectables.pip.model.PipenvGraphDependency
import com.synopsys.integration.detectable.detectables.pip.model.PipenvGraphEntry

// TODO: We convert a list of PipEnvPackage to a PipenvGraph to work with the existing transformer. If json-tree is implemented, the transformer should be modified to take in a List<PipEnvPackage>.
class PipEnvJsonGraphParser(private val gson: Gson) {
    inline fun <reified T> Gson.fromJson(json: String): T = this.fromJson<T>(json, object : TypeToken<T>() {}.type)
    data class PipEnvPackage(private val key: String?, val package_name: String?, val installed_version: String?, val dependencies: List<PipEnvPackage?>) // TODO: Use serialized name and refactor variable names.

    fun parse(pipenvGraphOutput: String): PipenvGraph {
        val entries = gson.fromJson<List<PipEnvPackage>>(pipenvGraphOutput)
        val pipEnvEntries = entries.map(this::convertToPipEnvEntry)
        return PipenvGraph(pipEnvEntries)
    }

    private fun convertToPipEnvEntry(pipEnvPackage: PipEnvPackage?): PipenvGraphEntry? {
        val dependencies = pipEnvPackage?.dependencies?.map(this::convertToPipEnvDependency)
        return PipenvGraphEntry(pipEnvPackage?.package_name, pipEnvPackage?.installed_version, dependencies)
    }

    private fun convertToPipEnvDependency(pipEnvPackage: PipEnvPackage?): PipenvGraphDependency? {
        val dependencies = pipEnvPackage?.dependencies?.map(this::convertToPipEnvDependency)
        return PipenvGraphDependency(pipEnvPackage?.package_name, pipEnvPackage?.installed_version, dependencies)
    }
}