package com.rocketzly.checks.config.bean

/**
 * 资源命名规范
 * User: Rocket
 * Date: 2020/6/19
 * Time: 5:06 PM
 */
data class ResourceName(
    /**
     * 图片资源
     */
    val drawable: BaseConfigProperty = BaseConfigProperty(),
    /**
     * 布局资源
     */
    val layout: BaseConfigProperty = BaseConfigProperty()
)