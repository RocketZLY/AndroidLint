package com.rocketzly.lintplugin.task

import com.rocketzly.gradle.api.bean.LintOptionData
import com.rocketzly.lintplugin.extension.LintConfigExtension
import org.gradle.api.Project
import java.io.File

/**
 * LintOptionsData数据转换器
 * Created by rocketzly on 2020/8/30.
 */
class LintOptionsDataAdapter {

    companion object {
        private val CHECK_LIST = setOf(
            "HandleExceptionCheck",
            "AvoidUsageApiCheck",
            "DependencyApiCheck",
            "ResourceNameCheck"
        )
        const val XML_OUTPUT_RELATIVE_PATH = "build/reports/lint-results.xml"
        const val HTML_OUTPUT_RELATIVE_PATH = "build/reports/lint-results.html"
        const val BASELINE_RELATIVE_PATH = "lint-baseline.xml"

        fun adapter(project: Project, configExtension: LintConfigExtension): LintOptionData {
            return LintOptionData().apply {
                if (configExtension.onlyCheckCustomIssue) {
                    check = CHECK_LIST //设置只检查自定义issue
                }
                xmlOutput = File(XML_OUTPUT_RELATIVE_PATH)//指定xml输出目录
                htmlOutput = File(HTML_OUTPUT_RELATIVE_PATH)//指定html输出目录
                abortOnError = false//发生错误停止task执行 默认true
                if (configExtension.baseline) {
                    baselineFile = project.file(BASELINE_RELATIVE_PATH)//创建警告基准
                }
            }
        }
    }
}