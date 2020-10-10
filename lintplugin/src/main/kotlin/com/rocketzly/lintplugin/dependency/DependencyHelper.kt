package com.rocketzly.lintplugin.dependency

import com.android.build.gradle.tasks.LintBaseTask
import com.rocketzly.lintplugin.LintHelper
import org.gradle.api.Project

/**
 * 添加依赖
 * Created by rocketzly on 2020/8/30.
 */
class DependencyHelper : LintHelper {

    companion object {
        const val DEPENDENCY_LINT_PATH = "com.rocketzly:lint:1.0.3"
        const val DEPENDENCY_LINT_INCREMENT_PATH = "com.rocketzly:lintPatch:0.0.2"

        /**
         * 注入lint补丁，目前包含增量扫描和bug修复功能
         */
        fun injectLintPatch(project: Project) {
            project.dependencies.add(LintBaseTask.LINT_CLASS_PATH, DEPENDENCY_LINT_INCREMENT_PATH)
        }
    }

    override fun apply(project: Project) {
        project.dependencies.add("implementation", DEPENDENCY_LINT_PATH)

    }
}