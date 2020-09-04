package com.rocketzly.lintplugin.task

import com.android.build.gradle.internal.dsl.LintOptions
import com.rocketzly.lintplugin.extension.ExtensionHelper.Companion.EXTENSION_LINT_CONFIG
import com.rocketzly.lintplugin.extension.LintConfigExtension
import org.gradle.api.Project
import java.io.File

/**
 * 修改lintOption配置
 * Created by rocketzly on 2020/8/30.
 */
class LintOptionsInjector {

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

        fun inject(project: Project, lintOptions: LintOptions) {
            lintOptions.apply {
                check = CHECK_LIST //设置只检查的类型
                xmlOutput = File(XML_OUTPUT_RELATIVE_PATH)//指定xml输出目录
                htmlOutput = File(HTML_OUTPUT_RELATIVE_PATH)//指定html输出目录
                isWarningsAsErrors = false//返回lint是否应将所有警告视为错误
                isAbortOnError = false//发生错误停止task执行 默认true
                if ((project.extensions.getByName(EXTENSION_LINT_CONFIG) as LintConfigExtension).baseline) {
                    baselineFile = project.file(BASELINE_RELATIVE_PATH)//创建警告基准
                }
            }
        }
    }
}