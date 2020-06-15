package com.rocketzly.checks.config.bean

/**
 * User: Rocket
 * Date: 2020/6/15
 * Time: 2:29 PM
 */
data class AvoidInheritClass(val exclude: List<String> = listOf()) : BaseConfigProperty()