package com.rocketzly.checks.config.bean

import com.google.gson.annotations.SerializedName

/**
 * 有依赖关系的api
 * User: Rocket
 * Date: 2020/6/17
 * Time: 4:12 PM
 */
data class DependencyApi(
    /**
     * 触发方法
     */
    @SerializedName("trigger_method")
    val triggerMethod: String = "",
    /**
     * 依赖方法
     */
    @SerializedName("dependency_method")
    val dependencyMethod: String = ""
) : BaseConfigProperty()