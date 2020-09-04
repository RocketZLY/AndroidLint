package com.rocketzly.lintplugin

import com.rocketzly.lintplugin.dependency.DependencyHelper
import com.rocketzly.lintplugin.extension.ExtensionHelper
import com.rocketzly.lintplugin.log.LogHelper
import com.rocketzly.lintplugin.task.LintTaskHelper
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * User: Rocket
 * Date: 2020/8/13
 * Time: 3:30 PM
 */
class LintPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        DependencyHelper().apply(project)
        ExtensionHelper().apply(project)
        LogHelper().apply(project)
        LintTaskHelper().apply(project)
    }
}