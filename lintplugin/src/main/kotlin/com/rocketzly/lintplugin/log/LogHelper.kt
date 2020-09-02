package com.rocketzly.lintplugin.log

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.rocketzly.lintplugin.LintPluginManager
import com.rocketzly.lintplugin.extension.LintGlobalConfigExtension
import com.rocketzly.lintplugin.isRootProject
import com.rocketzly.lintplugin.lintconfig.LintConfigHelper.Companion.HTML_OUTPUT_RELATIVE_PATH
import com.rocketzly.lintplugin.lintconfig.LintConfigHelper.Companion.XML_OUTPUT_RELATIVE_PATH
import org.gradle.api.Project
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * 添加执行日志
 * Created by rocketzly on 2020/8/30.
 */
class LogHelper : LintPluginManager.LintHelper {

    companion object {
        const val CONFIG_RELATIVE_PATH = "custom_lint_config.json"
    }

    override fun apply(project: Project) {
        if (project.isRootProject()) return

        var startTime = 0L
        project.afterEvaluate { p ->
            p.tasks.forEach { task ->
                if (task.name.startsWith("lint")) {
                    task.doFirst {
                        startTime = System.currentTimeMillis()
                        printSplitLine("lint配置信息")
                        val configFile = File(project.rootDir, CONFIG_RELATIVE_PATH)
                        if (!configFile.exists() || !configFile.isFile) {
                            println("配置文件未找到 Path：${configFile.absolutePath}")
                        } else {
                            println("配置文件加载成功 Path：${configFile.absolutePath}")
                        }
                        println("本次扫描的issue id如下：")
                        println((p.extensions.getByName("android") as? BaseAppModuleExtension)!!.lintOptions.check)
                        printSplitLine("lint配置信息")
                    }

                    task.doLast {
                        val file = project.file(XML_OUTPUT_RELATIVE_PATH)
                        if (!file.exists() || !file.isFile) {
                            println("未找到${file.absolutePath}文件，无法打印lint结果日志")
                            return@doLast
                        }
                        try {
                            val parse =
                                DocumentBuilderFactory.newInstance().newDocumentBuilder()
                                    .parse(file)
                            var errorCount = 0
                            val summary = HashSet<String>()
                            parse.getElementsByTagName("issue").apply {
                                for (i in 0 until length) {
                                    if (item(i).attributes.getNamedItem("severity").nodeValue == "Error") {
                                        errorCount++
                                        summary.add(item(i).attributes.getNamedItem("summary").nodeValue)
                                    }
                                }
                            }
                            printSplitLine("lint结果信息")
                            if (errorCount == 0) {
                                println("lint检查通过，未发现错误")
                            } else {
                                println("lint检查未通过，发现${errorCount}个错误")
                                println("包括如下几类错误：")
                                summary.forEach {
                                    println(it)
                                }
                            }
                            println("耗时：${System.currentTimeMillis() - startTime}ms")
                            printSplitLine("lint结果信息")

                            if (errorCount != 0) {
                                executePython3(project)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            println("解析出错，无法打印lint结果日志")
                        }
                    }
                }
            }
        }
    }

    /**
     * lint检查发现错误执行脚本
     */
    private fun executePython3(project: Project) {
        printSplitLine("lint发现错误执行脚本信息")
        val scriptPath =
            project.rootProject.extensions.getByType(LintGlobalConfigExtension::class.java).failScriptPath
        if (scriptPath.isEmpty()) {
            println("rootProject.lintGlobalConfig.failScriptPath未设置，将不会自动执行脚本")
            printSplitLine("lint发现错误执行脚本信息")
            return
        }

        val userName = String(
            Runtime.getRuntime().exec("git config user.name").inputStream.readBytes(),
            Charsets.UTF_8
        )
        val branch = String(
            Runtime.getRuntime().exec("git symbolic-ref --short -q HEAD").inputStream.readBytes(),
            Charsets.UTF_8
        )

        val command =
            "python3 $scriptPath --userName=$userName --branch=$branch --reportPath=${project.file(
                HTML_OUTPUT_RELATIVE_PATH
            )}"
        println("执行命令如下：")
        println(command)
        val byteArray = Runtime.getRuntime().exec(command).inputStream.readBytes()
        val result = String(byteArray, Charsets.UTF_8)
        println()
        println("执行日志如下：")
        println(result)
        printSplitLine("lint发现错误执行脚本信息")
    }
}

fun printSplitLine(tag: String) {
    println("--------------------------------------------日志分割线：$tag--------------------------------------------")
}