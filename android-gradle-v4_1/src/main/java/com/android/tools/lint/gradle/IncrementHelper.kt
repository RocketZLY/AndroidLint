package com.android.tools.lint.gradle

import com.android.tools.lint.detector.api.Project
import com.rocketzly.gradle.utils.IncrementUtils

/**
 * 增量扫描工具
 * User: Rocket
 * Date: 2020/8/28
 * Time: 4:09 PM
 */
class IncrementHelper {

    companion object {

        const val TAG = "lint增量信息"

        @JvmStatic
        fun injectFile(gradleProject: org.gradle.api.Project, project: Project) {
            //增量扫描逻辑
            printSplitLine(TAG)

            IncrementUtils.getChangeFile(gradleProject).forEach {
                project.addFile(it)
            }

            printSplitLine(TAG)
        }
    }
}

fun printSplitLine(tag: String) {
    println("--------------------------------------------日志分割线：$tag--------------------------------------------")
}


