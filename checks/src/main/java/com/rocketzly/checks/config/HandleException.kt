package com.rocketzly.checks.config

/**
 * 需要处理的异常
 * User: Rocket
 * Date: 2020/6/10
 * Time: 11:06 AM
 */
data class HandleException(
    val method: String,
    val exception: String,
    val message: String,
    val severity: String? = "error"
)