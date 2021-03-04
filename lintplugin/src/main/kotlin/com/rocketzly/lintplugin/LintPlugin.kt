package com.rocketzly.lintplugin

import com.rocketzly.lintplugin.dependency.DependencyHelper
import com.rocketzly.lintplugin.extension.ExtensionHelper
import com.rocketzly.lintplugin.analyze.AnalyzeHelper
import com.rocketzly.lintplugin.task.LintTaskHelper
import com.rocketzly.lintplugin.utils.StaticMemberContainer
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * User: Rocket
 * Date: 2020/8/13
 * Time: 3:30 PM
 */
class LintPlugin : Plugin<Project> {

    companion object {
        lateinit var mProject: Project
    }

    override fun apply(project: Project) {
        mProject = project
        //每次重置下，确保每次拿到的都是最新值
        StaticMemberContainer.reset()

        DependencyHelper().apply(project)
        ExtensionHelper().apply(project)
        AnalyzeHelper().apply(project)
        LintTaskHelper().apply(project)
    }
}