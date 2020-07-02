package com.rocketzly.checks

import com.android.tools.lint.detector.api.*
import com.rocketzly.checks.config.bean.BaseConfigProperty
import org.jetbrains.uast.UCallExpression

/**
 * User: Rocket
 * Date: 2020/6/11
 * Time: 4:37 PM
 */

/**
 * 获取该表达式的标准名称
 * 例：android.content.ContextWrapper.getSharedPreferences
 */
fun UCallExpression.getQualifiedName(): String {
    return resolve()?.containingClass?.qualifiedName + "." + resolve()?.name
}

fun Context.report(
    issue: Issue,
    location: Location,
    baseProperty: BaseConfigProperty
) {
    this.report(
        Issue.create(
            issue.id,
            baseProperty.message,
            issue.getExplanation(TextFormat.TEXT),
            issue.category,
            issue.priority,
            baseProperty.lintSeverity,
            issue.implementation
        ),
        location,
        baseProperty.message
    )
}