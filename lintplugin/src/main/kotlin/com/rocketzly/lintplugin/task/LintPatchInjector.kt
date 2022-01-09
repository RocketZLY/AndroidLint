package com.rocketzly.lintplugin.task

import org.gradle.api.Project

/**
 * User: Rocket
 * Date: 2021/11/16
 * Time: 4:55 下午
 */
class LintPatchInjector {
    companion object{
        /**
         * 注入lint补丁，目前包含增量扫描和bug修复功能
         */
        fun injectLintPatch(project: Project) {
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

//            project.dependencies.add(LintBaseTask.LINT_CLASS_PATH, project(":lintPatch"))
        }
    }
}