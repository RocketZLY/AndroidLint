package com.rocketzly.checks.config.bean

import com.google.gson.annotations.SerializedName

/**
 * 调用指定API时，需要加try-catch处理指定类型的异常
 * User: Rocket
 * Date: 2020/6/10
 * Time: 11:06 AM
 */
data class HandleExceptionMethod(
    val name: String = "",
    @SerializedName("name_regex")
    val nameRegex: String = "",
    val exception: String = "",
    val message: String = "",
    val severity: String? = "error"
)
