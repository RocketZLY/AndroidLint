package com.rocketzly.lintplugin.dependency

import com.rocketzly.lintplugin.LintHelper
import org.gradle.api.Project

/**
 * 添加依赖
 * Created by rocketzly on 2020/8/30.
 */
class DependencyHelper : LintHelper {

    companion object {
        const val version = "1.0.0"
    }

    override fun apply(project: Project) {
        //添加自定义规则依赖
        project.dependencies.add("implementation", "com.github.rocketzly:lintlibrary:$version")
    }
}