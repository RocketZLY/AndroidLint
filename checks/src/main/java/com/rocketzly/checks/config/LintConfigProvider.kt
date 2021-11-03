package com.rocketzly.checks.config

import com.android.tools.lint.detector.api.Context
import com.rocketzly.checks.config.LintParserKey.Companion.KEY_AVOID_USAGE_API
import com.rocketzly.checks.config.LintParserKey.Companion.KEY_DEPENDENCY_API
import com.rocketzly.checks.config.LintParserKey.Companion.KEY_HANDLE_EXCEPTION_METHOD
import com.rocketzly.checks.config.LintParserKey.Companion.KEY_RESOURCE_NAME
import com.rocketzly.checks.config.bean.AvoidUsageApi
import com.rocketzly.checks.config.bean.DependencyApi
import com.rocketzly.checks.config.bean.HandleExceptionMethod
import com.rocketzly.checks.config.bean.ResourceName
import java.io.File

/**
 * 配置信息提供类
 * User: Rocket
 * Date: 2020/5/27
 * Time: 4:10 PM
 */
class LintConfigProvider private constructor() {

    private lateinit var parser: LintConfigParser

    companion object {
        const val IS_DEBUG = true
        const val CONFIG_FILE_NAME = "custom_lint_config.json"

        //测试路径无法从project获得，因此只能写死
        const val DEBUG_PATH = "/Users/liyuan.zhu/Documents/AndroidLint"

        private var instance: LintConfigProvider? = null
        fun getInstance(context: Context): LintConfigProvider {
            if (instance == null) {
                instance = LintConfigProvider()
                instance!!.init(context)
            }
            return instance!!
        }
    }

    private fun init(context: Context) {
        val parent = if (IS_DEBUG) DEBUG_PATH else context.project.dir.absolutePath + "/../"
        val configFile = File(parent, CONFIG_FILE_NAME)
        parser = LintConfigParser(configFile)
    }

    /**
     * 避免使用的api包含 方法、构造方法、字段等
     */
    val avoidUsageApi by lazy {
        parser.getObject<AvoidUsageApi>(KEY_AVOID_USAGE_API)
    }

    /**
     * 调用指定API时，需要加try-catch处理指定类型的异常
     */
    val handleExceptionMethodList by lazy {
        parser.getList<HandleExceptionMethod>(KEY_HANDLE_EXCEPTION_METHOD)
    }

    /**
     * 有依赖关系的api
     */
    val dependencyApiList by lazy {
        parser.getList<DependencyApi>(KEY_DEPENDENCY_API)
    }

    /**
     * 获取资源命名
     */
    val resourceName by lazy {
        parser.getObject<ResourceName>(KEY_RESOURCE_NAME)
    }
}