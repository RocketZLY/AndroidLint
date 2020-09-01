package com.android.tools.lint.gradle

import com.android.tools.lint.client.api.LintRequest
import org.gradle.api.Project
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
            var open = true
            var currentBranch = ""
            var targetBranch = ""

            project.rootProject.extensions.findByName("lintGlobalConfig")?.let { lintGlobalConfig ->
                lintGlobalConfig::class.java.superclass.apply {
                    declaredFields.forEach {
                        it.isAccessible = true

                        when (it.name) {
                            "currentBranch" -> currentBranch = it.get(lintGlobalConfig) as String
                            "targetBranch" -> targetBranch = it.get(lintGlobalConfig) as String
                            "isOpen" -> open = it.get(lintGlobalConfig) as Boolean
                        }
                    }
                }
            }


            if (!open) {
                println("rootProject.lintGlobalConfig.open为false，将进行全量扫描")
                println("--------------------------------------------LintIncrement--------------------------------------------")
                return
            }
            if (currentBranch.isEmpty()) {
                println("rootProject未设置lintGlobalConfig.currentBranch，无法增量扫描，将进行全量扫描")
                println("--------------------------------------------LintIncrement--------------------------------------------")
                return
            }
            if (targetBranch.isEmpty()) {
                println("rootProject未设置lintGlobalConfig.targetBranch，无法增量扫描，将进行全量扫描")
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


