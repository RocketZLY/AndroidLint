package com.rocketzly.lintplugin.dependency

import com.android.build.gradle.tasks.LintBaseTask
import com.rocketzly.lintplugin.LintHelper
import com.rocketzly.lintplugin.utils.LintUtils
import org.gradle.api.Project

/**
 * 添加依赖
 * Created by rocketzly on 2020/8/30.
 */
class DependencyHelper : LintHelper {

//    companion object {
//        const val DEPENDENCY_LINT_PATH = "com.rocketzly:lint:1.0.3"
//
//        /**
//         * 注入lint补丁，目前包含增量扫描和bug修复功能
//         */
//        fun injectLintPatch(project: Project) {
//            val agpVersion = LintUtils.getAgpVersion(project)
//            val lintPatchDependency = if(agpVersion >= "3.5.0" && agpVersion < "3.6.0"){
//                "com.rocketzly:lintPatch:0.0.2"
//            }else if(agpVersion >= "3.6.0" && agpVersion < "4.0.0"){
//                "com.github.rocketzly:lintpatch:3.6.3"
//            }else if(agpVersion >= "4.0.0" && agpVersion < "4.1.0"){
//                "com.github.rocketzly:lintpatch:4.0.0"
//            }else{
//                ""
//            }
//            project.dependencies.add(LintBaseTask.LINT_CLASS_PATH, lintPatchDependency)
//        }
//    }

    override fun apply(project: Project) {
//        project.dependencies.add("implementation", DEPENDENCY_LINT_PATH)

    }
}