package com.rocketzly.lintplugin.lintconfig

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.rocketzly.lintplugin.LintPluginManager
import com.rocketzly.lintplugin.extension.ExtensionHelper.Companion.EXTENSION_NAME
import com.rocketzly.lintplugin.extension.LintConfigExtension
import org.gradle.api.Project
import java.io.File

/**
 * 增加lintOption配置
 * Created by rocketzly on 2020/8/30.
 */
class LintConfigHelper : LintPluginManager.LintHelper {

    companion object {
        val CHECK_LIST = setOf(
            "SerializableClassCheck",
            "HandleExceptionCheck",
            "AvoidUsageApiCheck",
            "DependencyApiCheck",
            "ResourceNameCheck"
        )
        const val XML_OUTPUT_RELATIVE_PATH = "build/reports/lint-results.xml"
        const val HTML_OUTPUT_RELATIVE_PATH = "build/reports/lint-results.html"
        const val BASELINE_RELATIVE_PATH = "lint-baseline.xml"
    }

    override fun apply(project: Project) {
        (project.extensions.getByName("android") as? BaseAppModuleExtension)
            ?.lintOptions
            ?.apply {
                check = CHECK_LIST //设置只检查的类型
                isAbortOnError = true //是否发现错误，则停止构建
                xmlOutput = File(XML_OUTPUT_RELATIVE_PATH)//指定xml输出目录
                htmlOutput = File(HTML_OUTPUT_RELATIVE_PATH)//指定html输出目录
                isWarningsAsErrors = false//返回lint是否应将所有警告视为错误
                isAbortOnError = false//发生错误停止task执行 默认true
                project.afterEvaluate {
                    if ((project.extensions.getByName(EXTENSION_NAME) as LintConfigExtension).baseline) {
                        baselineFile = project.file(BASELINE_RELATIVE_PATH)//创建警告基准
                    }
                }
            }
    }
}