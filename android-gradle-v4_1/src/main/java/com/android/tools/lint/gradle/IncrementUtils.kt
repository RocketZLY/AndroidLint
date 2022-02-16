package com.android.tools.lint.gradle

import com.android.tools.lint.detector.api.Project
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
        fun inject(gradleProject: org.gradle.api.Project, project: Project) {

            //增量扫描逻辑
            printSplitLine(TAG)
            //默认的revision取自最新提交记录的hash
            val defRevision = String(
                Runtime.getRuntime()
                    .exec("git log -1 --pretty=format:%h").inputStream.readBytes(),
                Charsets.UTF_8
            )
            var revision = gradleProject.properties["revision"] ?: defRevision
            var baseline = gradleProject.properties["baseline"]
            val command =
                "git diff $baseline $revision --name-only --diff-filter=ACMRTUXB"
            println("开始执行：")
            println(command)

            val byteArray = Runtime.getRuntime()
                .exec(command)
                .inputStream
                .readBytes()
            val diffFileStr = String(byteArray, Charsets.UTF_8).removeSuffix("\n")
            val diffFileList = diffFileStr.split("\n")
            println()
            println("diff结果：")
            println(diffFileStr)

            diffFileList.forEach {
                project.addFile(File(it))
            }
            printSplitLine(TAG)

        }
    }
}

fun printSplitLine(tag: String) {
    println("--------------------------------------------日志分割线：$tag--------------------------------------------")
}


