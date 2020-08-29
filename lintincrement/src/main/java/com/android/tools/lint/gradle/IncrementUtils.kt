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
            val currentBranch =
                (project.rootProject.extensions.getByName("ext") as DefaultExtraPropertiesExtension).get(
                    "current_branch"
                )
            val targetBranch =
                (project.rootProject.extensions.getByName("ext") as DefaultExtraPropertiesExtension).get(
                    "target_branch"
                )
            val command = "git diff $targetBranch $currentBranch --name-only --diff-filter=ACMRTUXB"
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


