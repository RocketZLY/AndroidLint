package com.rocketzly.lintplugin

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * User: Rocket
 * Date: 2020/8/13
 * Time: 3:30 PM
 */
class LintPlugin : Plugin<Project> {

    companion object {
        private val CHECK_LIST = setOf(
            "SerializableClassCheck",
            "HandleExceptionCheck",
            "AvoidUsageApiCheck",
            "DependencyApiCheck",
            "ResourceNameCheck"
        )
        private const val CONFIG_PATH = "custom_lint_config.json"
        private const val EXTENSION_NAME = "lintConfig"
        private const val HTML_OUTPUT_PATH = "build/reports/lint-results.html"
        private const val XML_OUTPUT_PATH = "build/reports/lint-results.xml"
        private const val BASELINE_PATH = "lint-baseline.xml"
        private const val DEPENDENCY_PATH = "com.rocketzly:lint:1.0.3"
    }

    override fun apply(project: Project) {
        addExtensions(project)
        addLintOptionsConfig(project)
        addDependency(project)
        addLintLog(project)
    }

    /**
     * 添加扩展属性
     */
    private fun addExtensions(project: Project) {
        project.extensions.create(EXTENSION_NAME, LintConfigExtension::class.java)
    }

    open class LintConfigExtension {
        var baseline = false
    }

    /**
     * 增加lintOption配置
     */
    private fun addLintOptionsConfig(project: Project) {
        (project.extensions.getByName("android") as? BaseAppModuleExtension)
            ?.lintOptions
            ?.apply {
                check = CHECK_LIST //设置只检查的类型
                isAbortOnError = true //是否发现错误，则停止构建
                xmlOutput = File(XML_OUTPUT_PATH)//指定xml输出目录
                htmlOutput = File(HTML_OUTPUT_PATH)//指定html输出目录
                isWarningsAsErrors = false//返回lint是否应将所有警告视为错误
                isAbortOnError = false//发生错误停止task执行 默认true
                project.afterEvaluate {
                    if ((project.extensions.getByName(EXTENSION_NAME) as LintConfigExtension).baseline) {
                        baselineFile = File(BASELINE_PATH)//创建警告基准
                    }
                }
            }
    }

    /**
     * 添加依赖
     */
    private fun addDependency(project: Project) {
        project.dependencies.add("implementation", DEPENDENCY_PATH)
    }

    /**
     * 添加执行日志
     */
    private fun addLintLog(project: Project) {
        project.afterEvaluate { p ->
            p.tasks.forEach { task ->
                if (task.name.startsWith("lint")) {
                    task.doFirst {
                        println("--------------------------------------------LintConfig--------------------------------------------")
                        val configFile = File(CONFIG_PATH)
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
                        val file = File(project.name + "/" + XML_OUTPUT_PATH)
                        if (!file.exists() || !file.isFile) {
                            println("未找到${file.absolutePath}文件，无法打印lint结果日志")
                            return@doLast
                        }
                        try {
                            val parse = DocumentBuilderFactory.newInstance().newDocumentBuilder()
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
                                println("包括如下几类：")
                                summary.forEach {
                                    println(it)
                                }
                            }
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