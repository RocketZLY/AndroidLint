package com.rocketzly.checks.config

import com.android.tools.lint.detector.api.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File

/**
 * 读取配置信息类
 * User: Rocket
 * Date: 2020/5/27
 * Time: 4:10 PM
 */
class LintConfig private constructor(context: Context) {

    var configJson = JsonObject()

    companion object {
        const val IS_DEBUG = false

        const val CONFIG_FILE_NAME = "custom_lint_config.json"
        const val KEY_AVOID_USAGE_API = "avoid_usage_api"


        var instance: LintConfig? = null
        fun getInstance(context: Context): LintConfig {
            if (instance == null) {
                instance = LintConfig(context)
            }
            return instance!!
        }
    }

    init {
        val configFile =
            File(
                if (IS_DEBUG) "./src/test/java/com/rocketzly/checks" else context.project.dir.absolutePath + "/../",
                CONFIG_FILE_NAME
            )
        if (configFile.exists() && configFile.isFile) {
            configJson = Gson().fromJson(configFile.bufferedReader(), JsonObject::class.java)
        }
    }

    val avoidUsageApi by lazy {
        val ret = AvoidUsageApi()

        val avoidUsageApiJson = configJson.getAsJsonObject(KEY_AVOID_USAGE_API)

        avoidUsageApiJson.getAsJsonArray("method").forEach {
            ret.avoidUsageMethodList.add(Gson().fromJson(it, AvoidUsageMethod::class.java))
        }

        ret
    }

}