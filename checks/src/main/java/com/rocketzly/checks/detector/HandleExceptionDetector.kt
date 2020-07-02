package com.rocketzly.checks.detector

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.rocketzly.checks.config.ConfigParser
import com.rocketzly.checks.config.LintConfig
import com.rocketzly.checks.LintMatcher
import com.rocketzly.checks.config.bean.HandleExceptionMethod
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
            "调用${LintConfig.CONFIG_FILE_NAME}中${ConfigParser.KEY_HANDLE_EXCEPTION_METHOD}指定API时，需要加try-catch处理指定类型的异常"
        val ISSUE = Issue.create(
            "HandleExceptionCheck",
            REPORT_MESSAGE,
            REPORT_MESSAGE,
            Category.SECURITY,
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
        var handleExceptionMethod: HandleExceptionMethod? = null
        lintConfig.handleExceptionMethod.forEach {
            if (LintMatcher.matchMethod(it, node)) {
                handleExceptionMethod = it
                return@forEach
            }
        }

        if (handleExceptionMethod == null) {
            return
        }
        val tryExpression: UTryExpression? =
            node.getParentOfType(UTryExpression::class.java)//获取try节点
        if (tryExpression == null) {
            context.report(ISSUE, context.getLocation(node), handleExceptionMethod!!)
            return
        }
        for (catch in tryExpression.catchClauses) {//拿到catch
            for (reference in catch.typeReferences) {//拿到异常类型
                if (context.evaluator.typeMatches(
                        reference.type,
                        handleExceptionMethod!!.exception
                    )//同一个异常
                    || context.evaluator.extendsClass(
                        context.evaluator.findClass(handleExceptionMethod!!.exception),
                        reference.getQualifiedName()!!,
                        true
                    )//try的是异常的父类
                ) {
                    return
                }
            }
        }
        context.report(ISSUE, context.getLocation(node), handleExceptionMethod!!)
    }
}