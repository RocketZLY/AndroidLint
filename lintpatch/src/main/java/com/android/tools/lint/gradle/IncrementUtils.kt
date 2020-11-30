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
            println()
            println("diff结果：")
            println(diffFileStr.removeSuffix("\n"))

            val filterFileList = filterOtherModuleFile(diffFileList, project)
            println()
            println("当前Module为${project.name}，过滤掉其他Module文件真正进行lint扫描的文件如下：")
            filterFileList.forEach {
                println(it)
            }

            lintRequest.getProjects()?.forEach { p ->
                filterFileList.forEach {
                    p.addFile(File(it))
                }
            }
            printSplitLine(TAG)
        }

        /**
         * 过滤其他module的文件，只扫当前module的
         */
        private fun filterOtherModuleFile(
            originList: List<String>,
            project: Project
        ): List<String> {
            val name = project.name
            return originList.filter { it.startsWith(name) }
        }
    }
}

fun printSplitLine(tag: String) {
    println("--------------------------------------------日志分割线：$tag--------------------------------------------")
}


