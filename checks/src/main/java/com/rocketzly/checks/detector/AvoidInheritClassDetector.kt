package com.rocketzly.checks.detector

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.rocketzly.checks.config.ConfigParser
import com.rocketzly.checks.config.LintConfig
import com.rocketzly.checks.config.LintRuleMatcher
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

/**
 * 避免继承或者实现的类
 * User: Rocket
 * Date: 2020/6/15
 * Time: 2:20 PM
 */
class AvoidInheritClassDetector : BaseDetector(), Detector.UastScanner {
    companion object {
        private const val REPORT_MESSAGE =
            "避免继承或实现${LintConfig.CONFIG_FILE_NAME}中${ConfigParser.KEY_AVOID_INHERIT_CLASS}配置的类"
        val ISSUE = Issue.create(
            "AvoidInheritClassCheck",
            REPORT_MESSAGE,
            REPORT_MESSAGE,
            Category.CORRECTNESS,
            10,
            Severity.WARNING,
            Implementation(AvoidInheritClassDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>>? {
        return listOf(UClass::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitClass(node: UClass) {
                lintConfig.avoidInheritClassList.forEach { avoidInheritClass ->
                    node.supers.forEach {
                        if (LintRuleMatcher.match(avoidInheritClass, it.qualifiedName ?: "")) {
                            if (avoidInheritClass.exclude.contains(node.qualifiedName)) {
                                return
                            }
                            context.report(
                                ISSUE,
                                context.getLocation(node as UElement),
                                avoidInheritClass.message
                            )
                        }
                    }
                }

            }
        }
    }

}