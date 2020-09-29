package com.rocketzly.lintplugin.dependency

import com.android.build.gradle.tasks.LintBaseTask
import com.rocketzly.lintplugin.LintConstants
import com.rocketzly.lintplugin.LintHelper
import org.gradle.api.Project

/**
 * 添加依赖
 * Created by rocketzly on 2020/8/30.
 */
class DependencyHelper : LintHelper {

    companion object {
        const val DEPENDENCY_LINT_PATH = "com.rocketzly:lint:1.0.3"
        const val DEPENDENCY_LINT_INCREMENT_PATH = "com.rocketzly:lintIncrement:0.0.5"

        fun injectLintIncrement(project: Project) {
            if (LintConstants.IS_DEBUG) {
                project.dependencies.add(
                    LintBaseTask.LINT_CLASS_PATH,
                    project.fileTree("${project.rootDir}/lintincrement/build/export")
                )
                return
            }

            project.dependencies.add(LintBaseTask.LINT_CLASS_PATH, DEPENDENCY_LINT_INCREMENT_PATH)
        }
    }

    override fun apply(project: Project) {
        project.dependencies.add("implementation", DEPENDENCY_LINT_PATH)

    }
}