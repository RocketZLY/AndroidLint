package com.rocketzly.lintplugin.extension

/**
 * Created by rocketzly on 2020/8/30.
 */
open class LintConfigExtension {

    /**
     * 是否生成baseline.xml
     */
    var baseline = false

    /**
     * 只扫自定义规则
     */
    var onlyCheckCustomIssue = true
}