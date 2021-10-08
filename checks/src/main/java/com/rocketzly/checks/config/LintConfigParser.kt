package com.rocketzly.checks.config

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.rocketzly.checks.config.bean.*
import java.io.File

/**
 * lint配置解析器
 * User: Rocket
 * Date: 2020/6/10
 * Time: 7:33 PM
 */
class LintConfigParser(configFile: File) {

    var configJson = JsonObject()

    init {
        if (configFile.exists() && configFile.isFile) {
            configJson = Gson().fromJson(configFile.bufferedReader(), JsonObject::class.java)
        }
    }

    inline fun <reified T> getObject(
        key: String,
        defaultValue: T = T::class.java.newInstance()
    ): T {
        return Gson().fromJson(
            configJson.getAsJsonObject(key),
            T::class.java
        ) ?: defaultValue
    }

    inline fun <reified T> getList(
        key: String,
        defaultValue: List<T> = listOf()
    ): List<T> {
        return Gson().fromJson(
            configJson.getAsJsonArray(key),
            object : TypeToken<List<T>>() {}.type
        ) ?: defaultValue
    }
}