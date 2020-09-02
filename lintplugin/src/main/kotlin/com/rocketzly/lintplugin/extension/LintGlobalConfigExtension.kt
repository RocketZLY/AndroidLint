package com.rocketzly.lintplugin.extension

/**
 * lint 全局配置
 * User: Rocket
 * Date: 2020/9/1
 * Time: 2:45 PM
 */
open class LintGlobalConfigExtension {

    var currentBranch: String = ""
    var targetBranch: String = ""
    var isOpen: Boolean = true
    var failScriptPath = ""
}