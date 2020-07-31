package com.rocketzly.checks.config.bean

import com.android.tools.lint.detector.api.Severity
import com.google.gson.annotations.SerializedName

/**
 * lint配置基础属性
 * User: Rocket
 * Date: 2020/6/12
 * Time: 4:15 PM
 */
open class BaseConfigProperty(
    val name: String = "",
    @SerializedName("name_regex")
    val nameRegex: String = "",
    val message: String = "",
    val exclude: List<String> = listOf(),
    @SerializedName("exclude_regex")
    val excludeRegex: String = "",
    private val severity: String? = "error"
) {
    val lintSeverity
        get() =
            when (severity) {
                "fatal" -> Severity.FATAL
                "error" -> Severity.ERROR
                "warning" -> Severity.WARNING
                "informational" -> Severity.INFORMATIONAL
                "ignore" -> Severity.IGNORE
                else -> Severity.ERROR
            }

}