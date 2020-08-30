package com.rocketzly.lintplugin.extension

import com.rocketzly.lintplugin.LintPluginManager

/**
 * 添加扩展属性
 * Created by rocketzly on 2020/8/30.
 */
class ExtensionHelper : LintPluginManager.LintHelper {

    companion object {
        const val EXTENSION_NAME = "lintConfig"
    }

    override fun apply(project: org.gradle.api.Project) {
        project.extensions.create(EXTENSION_NAME, LintConfigExtension::class.java)
    }
}