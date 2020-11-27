package com.rocketzly.lintplugin.extension

import com.rocketzly.lintplugin.LintHelper
import org.gradle.api.Project

/**
 * 添加扩展属性
 * Created by rocketzly on 2020/8/30.
 */
class ExtensionHelper : LintHelper {

    companion object {
        const val EXTENSION_LINT_CONFIG = "lintConfig"

        fun getConfigExtension(project: Project): LintConfigExtension {
            return (project.extensions.getByName(EXTENSION_LINT_CONFIG) as LintConfigExtension)
        }
    }

    override fun apply(project: Project) {
        project.extensions.create(EXTENSION_LINT_CONFIG, LintConfigExtension::class.java)
    }
}