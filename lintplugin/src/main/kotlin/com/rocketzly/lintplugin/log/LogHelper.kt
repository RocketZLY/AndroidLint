package com.rocketzly.lintplugin.log

import com.android.build.gradle.BaseExtension
import com.rocketzly.lintplugin.LintException
import com.rocketzly.lintplugin.LintHelper
import com.rocketzly.lintplugin.executor.ScriptExecutor
import com.rocketzly.lintplugin.task.LintCreationAction
import com.rocketzly.lintplugin.task.LintCreationAction.Companion.PARAM_NAME_BASELINE
import com.rocketzly.lintplugin.task.LintCreationAction.Companion.PARAM_NAME_REVISION
import com.rocketzly.lintplugin.task.LintOptionsInjector.Companion.XML_OUTPUT_RELATIVE_PATH
import com.rocketzly.lintplugin.utils.LintUtils
import org.gradle.api.Project
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * 添加执行日志
 * Created by rocketzly on 2020/8/30.
 */
class LogHelper : LintHelper {

    companion object {
        const val CONFIG_RELATIVE_PATH = "custom_lint_config.json"
    }

    override fun apply(project: Project) {
        var startTime = 0L
        project.gradle.taskGraph.whenReady {
            it.allTasks.find {
                if (LintUtils.checkTaskIsIncrementOrFullLint(it, project)
                ) {
                    return@find true
                }
                return@find false
            }?.apply {
                doFirst {
                    if (it.name == LintCreationAction.TASK_NAME_LINT_INCREMENT) {
                        if (!project.hasProperty(PARAM_NAME_BASELINE)) {
                            throw LintException("lintIncrement必须要${PARAM_NAME_BASELINE}参数")
                        }
                        if (!project.hasProperty(PARAM_NAME_REVISION)) {
                            throw LintException("lintIncrement必须要${PARAM_NAME_REVISION}参数")
                        }
                    }

                    startTime = System.currentTimeMillis()
                    printSplitLine("lint配置信息")
                    val configFile = File(project.rootDir, CONFIG_RELATIVE_PATH)
                    if (!configFile.exists() || !configFile.isFile) {
                        println("配置文件未找到 Path：")
                    } else {
                        println("配置文件加载成功 Path：")
                    }
                    println(configFile.absolutePath)

                    val checkList =
                        (project.extensions.getByName("android") as? BaseExtension)?.lintOptions?.check
                    println()
                    if (checkList.isNullOrEmpty()) {
                        println("本次为全issue扫描（即google自带的issue+自定义issue）")
                    } else {
                        println("本次只扫描自定义issue，id如下：")
                        println(checkList)
                    }
                    printSplitLine("lint配置信息")
                }
                doLast {
                    val file = project.file(XML_OUTPUT_RELATIVE_PATH)
                    if (!file.exists() || !file.isFile) {
                        println("未找到${file.absolutePath}文件，无法分析lint结果")
                        return@doLast
                    }

                    var errorCount = 0
                    val summary = HashSet<String>()
                    try {
                        val parse =
                            DocumentBuilderFactory.newInstance().newDocumentBuilder()
                                .parse(file)
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
                            println()
                            println("包括如下几类错误：")
                            summary.forEach {
                                println(it)
                            }
                        }
                        println()
                        println("耗时：")
                        println("${System.currentTimeMillis() - startTime}ms")
                        printSplitLine("lint结果信息")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        println("解析出错，无法打印lint结果日志")
                    }

                    if (errorCount != 0) {
                        ScriptExecutor.exec(project, errorCount, summary)
                        throw  LintException("lint检查发现错误")
                    }
                }
            }
        }
    }
}

fun printSplitLine(tag: String) {
    println("--------------------------------------------日志分割线：$tag--------------------------------------------")
}