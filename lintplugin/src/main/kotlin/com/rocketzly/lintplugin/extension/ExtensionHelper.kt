package com.rocketzly.lintplugin.extension

import com.rocketzly.lintplugin.LintPluginManager
import com.rocketzly.lintplugin.isRootProject
import org.gradle.api.Project

/**
 * 添加扩展属性
 * Created by rocketzly on 2020/8/30.
 */
class ExtensionHelper : LintPluginManager.LintHelper {

    companion object {
        const val EXTENSION_LINT_CONFIG = "lintConfig"
        const val EXTENSION_LINT_GLOBAL_CONFIG = "lintGlobalConfig"
    }

    override fun apply(project: Project) {
        if (project.isRootProject()) {
            project.extensions.create(
                EXTENSION_LINT_GLOBAL_CONFIG, LintGlobalConfigExtension::class.java
            )
        } else {
            project.extensions.create(EXTENSION_LINT_CONFIG, LintConfigExtension::class.java)
        }
    }
}