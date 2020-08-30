package com.rocketzly.lintplugin.dependency

import com.android.build.gradle.tasks.LintBaseTask
import com.rocketzly.lintplugin.LintPluginManager
import org.gradle.api.Project

/**
 * 添加依赖
 * Created by rocketzly on 2020/8/30.
 */
class DependencyHelper : LintPluginManager.LintHelper {

    companion object {
        const val DEPENDENCY_PATH = "com.rocketzly:lint:1.0.3"
    }

    override fun apply(project: Project) {
        project.dependencies.add("implementation", DEPENDENCY_PATH)

        project.dependencies.add(
            LintBaseTask.LINT_CLASS_PATH,
            project.fileTree(
                mapOf(
                    "dir" to "${project.rootDir.absolutePath}/lintincrement/build/export",
                    "include" to listOf("*.jar")
                )
            )
        )
    }
}