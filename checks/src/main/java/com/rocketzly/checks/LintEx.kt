package com.rocketzly.checks

import org.jetbrains.uast.UCallExpression
import java.util.regex.Pattern

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

/**
 * 判断是否匹配正则
 */
fun String.match(regex: String) = Pattern.compile(regex).matcher(this).matches()
