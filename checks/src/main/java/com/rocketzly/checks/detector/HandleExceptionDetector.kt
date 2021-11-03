package com.rocketzly.checks.detector

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.rocketzly.checks.CUSTOM_CATEGORY
import com.rocketzly.checks.config.LintConfigProvider
import com.rocketzly.checks.config.LintParserKey
import com.rocketzly.checks.matcher.LintMatcher
import com.rocketzly.checks.report
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UTryExpression
import org.jetbrains.uast.getParentOfType

/**
 * 调用指定API时，需要加try-catch处理指定类型的异常
 * User: Rocket
 * Date: 2020/5/28
 * Time: 4:00 PM
 */
class HandleExceptionDetector : BaseDetector(), Detector.UastScanner {
    companion object {
        private const val REPORT_MESSAGE =
            "调用${LintConfigProvider.CONFIG_FILE_NAME}中${LintParserKey.KEY_HANDLE_EXCEPTION_METHOD}指定API时，需要加try-catch处理指定类型的异常"
        val ISSUE = Issue.create(
            "HandleExceptionCheck",
            REPORT_MESSAGE,
            REPORT_MESSAGE,
            CUSTOM_CATEGORY,
            10,
            Severity.ERROR,
            Implementation(HandleExceptionDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>>? {
        return listOf(UCallExpression::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {

            override fun visitCallExpression(node: UCallExpression) {
                checkMethod(context, node)
            }
        }
    }

    private fun checkMethod(context: JavaContext, node: UCallExpression) {
        //匹配需要处理异常方法
        val handleExceptionMethod = lintConfigProvider.handleExceptionMethodList.find {
            LintMatcher.matchMethod(it, node)
        } ?: return

        //获取try节点
        val tryExpression: UTryExpression? =
            node.getParentOfType(UTryExpression::class.java)
        if (tryExpression == null) {
            context.report(ISSUE, context.getLocation(node), handleExceptionMethod)
            return
        }

        for (catch in tryExpression.catchClauses) {//拿到catch
            for (reference in catch.typeReferences) {//拿到异常类型
                //是否catch了当前异常或者父类异常
                val isCatch = context.evaluator.inheritsFrom(
                    context.evaluator.findClass(handleExceptionMethod.exception),
                    reference.getQualifiedName()!!,
                    false
                )
                if (isCatch) {
                    return
                }
            }
        }
        context.report(ISSUE, context.getLocation(node), handleExceptionMethod)
    }
}