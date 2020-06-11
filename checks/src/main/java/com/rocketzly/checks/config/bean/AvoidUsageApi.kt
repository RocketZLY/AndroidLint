package com.rocketzly.checks.config.bean

import com.google.gson.annotations.SerializedName


/**
 * 避免调用api
 * User: Rocket
 * Date: 2020/6/10
 * Time: 11:14 AM
 */
class AvoidUsageApi(
    var method: List<AvoidUsageMethod> = listOf()
)

/**
 * 避免调用的方法
 */
data class AvoidUsageMethod(
    val name: String = "",
    @SerializedName("name_regex")
    val nameRegex: String = "",
    val message: String = "",
    val severity: String = "error"
)