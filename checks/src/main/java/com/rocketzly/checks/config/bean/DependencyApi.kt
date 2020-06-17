package com.rocketzly.checks.config.bean

import com.google.gson.annotations.SerializedName

/**
 * User: Rocket
 * Date: 2020/6/17
 * Time: 4:12 PM
 */
data class DependencyApi(
    val clazz: String = "",
    @SerializedName("condition_method")
    val conditionMethod: String = "",
    @SerializedName("dependency_method")
    val dependencyMethod: String = ""
) : BaseConfigProperty()