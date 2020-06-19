package com.rocketzly.checks.config.bean

/**
 * 资源命名规范
 * User: Rocket
 * Date: 2020/6/19
 * Time: 5:06 PM
 */
data class ResourceName(
    val drawable: BaseConfigProperty = BaseConfigProperty(),
    val layout: BaseConfigProperty = BaseConfigProperty()
)