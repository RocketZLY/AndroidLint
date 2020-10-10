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

        const val TAG = "lint增量信息"

        @JvmStatic
        fun inject(project: Project, lintRequest: LintRequest) {
            //当前执行的不是增量扫描
            if (project.gradle.startParameter.taskNames.find { it.contains("lintIncrement") } == null) {
                return
            }

            //增量扫描逻辑
            printSplitLine(TAG)
            var revision = project.properties["revision"]
            var baseline = project.properties["baseline"]

            val command =
                "git diff $baseline $revision --name-only --diff-filter=ACMRTUXB"
            println("开始执行：")
            println(command)
            val byteArray = Runtime.getRuntime()
                .exec(command)
                .inputStream
                .readBytes()
            val diffFileStr = String(byteArray, Charsets.UTF_8)
            val diffFileList = diffFileStr.split("\n")

            println("diff结果：")
            println(diffFileStr.removeSuffix("\n"))
            lintRequest.getProjects()?.forEach { p ->
                diffFileList.forEach {
                    p.addFile(File(it))
                }
            }
            printSplitLine(TAG)
        }
    }
}

fun printSplitLine(tag: String) {
    println("--------------------------------------------日志分割线：$tag--------------------------------------------")
}


