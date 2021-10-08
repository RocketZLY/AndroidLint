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
    /**
     * 精确匹配名字
     */
    val name: String = "",

    /**
     * 正则匹配名字
     */
    @SerializedName("name_regex")
    val nameRegex: String = "",

    /**
     * 提示文案
     */
    val message: String = "",

    /**
     * 精确匹配需要排除的
     */
    val exclude: List<String> = listOf(),

    /**
     * 正则匹配要排除的
     */
    @SerializedName("exclude_regex")
    val excludeRegex: String = "",

    /**
     * 错误等级，默认error
     */
    @SerializedName("severity")
    private val _severity: String? = "error"
) {
    val severity
        get() =
            when (_severity) {
                "fatal" -> Severity.FATAL
                "error" -> Severity.ERROR
                "warning" -> Severity.WARNING
                "informational" -> Severity.INFORMATIONAL
                "ignore" -> Severity.IGNORE
                else -> Severity.ERROR
            }
}