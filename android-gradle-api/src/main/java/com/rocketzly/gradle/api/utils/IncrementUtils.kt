package com.rocketzly.gradle.api.utils

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
        const val PARAM_NAME_BASELINE = "baseline"
        const val PARAM_NAME_REVISION = "revision"

        @JvmStatic
        fun getChangeFile(gradleProject: Project): List<File> {

            //默认的revision取自最新提交记录的hash
            val defRevision = String(
                Runtime.getRuntime()
                    .exec("git log -1 --pretty=format:%h").inputStream.readBytes(),
                Charsets.UTF_8
            )
            var baseline = gradleProject.properties[PARAM_NAME_BASELINE]
            var revision = gradleProject.properties[PARAM_NAME_REVISION] ?: defRevision
            val command =
                "git diff $baseline $revision --name-only --diff-filter=ACMRTUXB"
            println("开始执行：")
            println(command)

            val byteArray = Runtime.getRuntime()
                .exec(command)
                .inputStream
                .readBytes()
            val diffFileStr = String(byteArray, Charsets.UTF_8).removeSuffix("\n")
            val diffFileStrList = diffFileStr.split("\n")
            println()
            println("diff结果：")
            println(diffFileStr)
            return diffFileStrList.map { File(it) }
        }
    }
}


