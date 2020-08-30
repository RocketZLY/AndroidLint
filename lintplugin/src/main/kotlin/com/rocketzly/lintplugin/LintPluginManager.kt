package com.rocketzly.lintplugin

import com.rocketzly.lintplugin.dependency.DependencyHelper
import com.rocketzly.lintplugin.extension.ExtensionHelper
import com.rocketzly.lintplugin.lintconfig.LintConfigHelper
import com.rocketzly.lintplugin.log.LogHelper
import org.gradle.api.Project


/**
 * Created by rocketzly on 2020/8/30.
 */
object LintPluginManager {

    private val helperList by lazy {
        listOf(
            ExtensionHelper(),
            LintConfigHelper(),
            DependencyHelper(),
            LogHelper()
        )
    }

    fun init(project: Project) {
        helperList.forEach { it.apply(project) }
    }

    interface LintHelper {
        fun apply(project: Project)
    }
}