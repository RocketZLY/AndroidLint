package com.rocketzly.lintplugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * User: Rocket
 * Date: 2020/8/13
 * Time: 3:30 PM
 */
class LintPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        LintPluginManager.init(project)
    }
}