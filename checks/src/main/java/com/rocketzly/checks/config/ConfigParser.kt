package com.rocketzly.checks.config

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.rocketzly.checks.config.bean.AvoidUsageApi
import com.rocketzly.checks.config.bean.AvoidUsageMethod
import com.rocketzly.checks.config.bean.HandleException
import com.rocketzly.checks.config.bean.HandleExceptionMethod
import java.io.File

/**
 * lint配置解析器
 * User: Rocket
 * Date: 2020/6/10
 * Time: 7:33 PM
 */
class ConfigParser(configFile: File) {

    var configJson = JsonObject()

    companion object {
        const val KEY_AVOID_USAGE_API = "avoid_usage_api"
        const val KEY_HANDLE_EXCEPTION = "handle_exception_method"
    }

    init {
        if (configFile.exists() && configFile.isFile) {
            configJson = Gson().fromJson(configFile.bufferedReader(), JsonObject::class.java)
        }
    }

    fun getAvoidUsageApi(): AvoidUsageApi {
        val ret = AvoidUsageApi()

        val avoidUsageApiJson = configJson.getAsJsonObject(KEY_AVOID_USAGE_API) ?: return ret

        //解析避免调用的方法
        avoidUsageApiJson.get("method")?.asJsonArray?.forEach {
            ret.avoidUsageMethodList.add(
                AvoidUsageMethod(
                    getMemberName(it.asJsonObject.get("name").asString),
                    it.asJsonObject.get("message").asString,
                    it.asJsonObject.get("severity").asString,
                    getClassName(it.asJsonObject.get("name").asString)
                )
            )
        }

        return ret
    }

    fun getHandleException(): HandleException {
        val ret = HandleException()

        configJson.get(KEY_HANDLE_EXCEPTION)?.asJsonArray?.forEach {
            ret.method.add(
                HandleExceptionMethod(
                    getMemberName(it.asJsonObject.get("name").asString),
                    it.asJsonObject.get("exception").asString,
                    it.asJsonObject.get("message").asString,
                    it.asJsonObject.get("severity").asString,
                    getClassName(it.asJsonObject.get("name").asString)
                )
            )
        }

        return ret
    }

    /**
     * 获取方法名、字段名
     */
    private fun getMemberName(canonicalName: String): String {
        if (!canonicalName.contains(".")) {//不是全路径名直接使用
            return canonicalName
        }
        return canonicalName.substring(canonicalName.lastIndexOf(".") + 1)
    }

    /**
     * 获取成员所在类名
     */
    private fun getClassName(canonicalName: String): String {
        if (canonicalName.contains(".")) {
            return canonicalName.substring(0, canonicalName.lastIndexOf("."))
        }
        return ""
    }
}