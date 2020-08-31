package com.rocketzly.lintplugin.log

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.rocketzly.lintplugin.LintPluginManager
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
        var startTime = 0L
        project.afterEvaluate { p ->
            p.tasks.forEach { task ->
                if (task.name.startsWith("lint")) {
                    task.doFirst {
                        startTime = System.currentTimeMillis()
                        println("--------------------------------------------LintConfig--------------------------------------------")
                        val configFile = File(project.rootDir, CONFIG_RELATIVE_PATH)
                        if (!configFile.exists() || !configFile.isFile) {
                            println("配置文件未找到 Path：${configFile.absolutePath}")
                        } else {
                            println("配置文件加载成功 Path：${configFile.absolutePath}")
                        }
                        println("本次扫描的issue id如下：")
                        println((p.extensions.getByName("android") as? BaseAppModuleExtension)!!.lintOptions.check)
                        println("--------------------------------------------LintConfig--------------------------------------------")
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
                            println("--------------------------------------------LintResult--------------------------------------------")
                            if (errorCount == 0) {
                                println("lint检查通过，未发现错误")
                            } else {
                                println("lint检查未通过，发现${errorCount}个错误")
                                println("包括如下几类错误：")
                                summary.forEach {
                                    println(it)
                                }
                            }
                            println("耗时：${System.currentTimeMillis()-startTime}ms")
                            println("--------------------------------------------LintResult--------------------------------------------")
                        } catch (e: Exception) {
                            e.printStackTrace()
                            println("解析出错，无法打印lint结果日志")
                        }
                    }
                }
            }
        }
    }
}