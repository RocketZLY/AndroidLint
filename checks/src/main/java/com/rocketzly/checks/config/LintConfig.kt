package com.rocketzly.checks.config

import com.android.tools.lint.detector.api.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.rocketzly.checks.config.bean.AvoidUsageApi
import com.rocketzly.checks.config.bean.AvoidUsageMethod
import com.rocketzly.checks.config.bean.HandleException
import java.io.File

/**
 * 读取配置信息类
 * User: Rocket
 * Date: 2020/5/27
 * Time: 4:10 PM
 */
class LintConfig private constructor(context: Context) {

    var parser: ConfigParser

    companion object {
        const val IS_DEBUG = false
        const val CONFIG_FILE_NAME = "custom_lint_config.json"

        private var instance: LintConfig? = null
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
                if (IS_DEBUG) "./src/test/java/com/rocketzly/checks/config"
                else context.project.dir.absolutePath + "/../",
                CONFIG_FILE_NAME
            )
        parser = ConfigParser(configFile)
    }

    /**
     * 避免使用的api包含 方法、构造方法、字段等
     */
    val avoidUsageApi by lazy {
        parser.getAvoidUsageApi()
    }

    /**
     * 调用指定API时，需要加try-catch处理指定类型的异常
     */
    val handleException: HandleException by lazy {
        parser.getHandleException()
    }

}