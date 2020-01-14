package com.synopsys.integration.detectable.detectables.pip.parser

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.synopsys.integration.detectable.detectables.pip.model.PipenvGraph
import com.synopsys.integration.detectable.detectables.pip.model.PipenvGraphEntry

class PipEnvJsonGraphParser(private val gson: Gson) {
    inline fun <reified T> Gson.fromJson(json: String): T = this.fromJson<T>(json, object : TypeToken<T>() {}.type)

    fun parse(pipenvGraphOutput: String): PipenvGraph {
        val entries = gson.fromJson<List<PipenvGraphEntry>>(pipenvGraphOutput)
        return PipenvGraph(entries)
    }
}