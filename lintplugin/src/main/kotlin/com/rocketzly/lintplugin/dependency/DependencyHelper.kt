package com.rocketzly.lintplugin.dependency

import com.android.build.gradle.tasks.LintBaseTask
import com.rocketzly.lintplugin.LintPluginManager
import com.rocketzly.lintplugin.isRootProject
import org.gradle.api.Project

/**
 * 添加依赖
 * Created by rocketzly on 2020/8/30.
 */
class DependencyHelper : LintPluginManager.LintHelper {

    companion object {
        const val DEPENDENCY_LINT_PATH = "com.rocketzly:lint:1.0.3"
        const val DEPENDENCY_LINT_INCREMENT_PATH = "com.rocketzly:lintIncrement:0.0.3"
    }

    override fun apply(project: Project) {
        if (project.isRootProject()) return

        project.dependencies.add("implementation", DEPENDENCY_LINT_PATH)
        project.dependencies.add(LintBaseTask.LINT_CLASS_PATH, DEPENDENCY_LINT_INCREMENT_PATH)
    }
}