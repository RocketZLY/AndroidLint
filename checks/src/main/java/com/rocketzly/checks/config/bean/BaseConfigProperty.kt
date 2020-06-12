package com.rocketzly.checks.config.bean

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
    val severity: String? = "error"
)