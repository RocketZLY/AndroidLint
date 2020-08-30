package com.android.tools.lint.gradle

import com.android.tools.lint.client.api.LintRequest
import org.gradle.api.Project
import org.gradle.internal.extensibility.DefaultExtraPropertiesExtension
import java.io.File

/**
 * 增量扫描工具
 * User: Rocket
 * Date: 2020/8/28
 * Time: 4:09 PM
 */
class IncrementUtils {

    companion object {

        @JvmStatic
        fun inject(project: Project, lintRequest: LintRequest) {
            //增量扫描逻辑
            println("--------------------------------------------LintIncrement--------------------------------------------")
            var isOpen: Boolean
            var currentBranch: String
            var targetBranch: String

            (project.rootProject.extensions.getByName("ext") as DefaultExtraPropertiesExtension).apply {
                isOpen = find(
                    "lintIncrement"
                ) as Boolean? ?: true

                currentBranch = find(
                    "currentBranch"
                ) as String? ?: ""

                targetBranch = find(
                    "targetBranch"
                ) as String? ?: ""
            }

            if (!isOpen) {
                println("rootProject.ext.lintIncrement为false，将进行全量扫描")
                println("--------------------------------------------LintIncrement--------------------------------------------")
                return
            }
            if (currentBranch.isEmpty()) {
                println("rootProject未设置ext.currentBranch，无法增量扫描，将进行全量扫描")
                println("--------------------------------------------LintIncrement--------------------------------------------")
                return
            }
            if (targetBranch.isEmpty()) {
                println("rootProject未设置ext.targetBranch，无法增量扫描，将进行全量扫描")
                println("--------------------------------------------LintIncrement--------------------------------------------")
                return
            }

            val command =
                "git diff $targetBranch $currentBranch --name-only --diff-filter=ACMRTUXB"
            println("开始执行：")
            println(command)
            val byteArray = Runtime.getRuntime()
                .exec(command)
                .inputStream
                .readBytes()
            val diffFileStr = String(byteArray, Charsets.UTF_8)
            val diffFileList = diffFileStr.split("\n")

            println("diff结果：")
            println(diffFileStr)
            lintRequest.getProjects()?.forEach { p ->
                diffFileList.forEach {
                    p.addFile(File(it))
                }
            }
            println("--------------------------------------------LintIncrement--------------------------------------------")
        }
    }
}


