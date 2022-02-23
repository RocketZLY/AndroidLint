package com.rocketzly.gradle.api.bean

import java.io.File

/**
 * User: Rocket
 * Date: 2022/2/18
 * Time: 3:38 下午
 */
data class LintOptionData(
    /**
     * 只扫描的规则ids
     */
    var check: Set<String> = setOf(),

    /**
     * 是否生成baseline.xml
     */
    var baselineFile: File? = null,

    /**
     * 指定xml输出目录
     */
    var xmlOutput: File? = null,

    /**
     * 指定html输出目录
     */
    var htmlOutput: File? = null,

    /**
     * 发生错误停止task执行
     */
    var abortOnError: Boolean = false,
)