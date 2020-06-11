package com.rocketzly.checks

import org.jetbrains.uast.UCallExpression

/**
 * User: Rocket
 * Date: 2020/6/11
 * Time: 4:37 PM
 */

/**
 * 获取该表达式的标准名称
 * 例：
 */
fun UCallExpression.getQualifiedName(): String {
    return resolve()?.containingClass?.qualifiedName + "." + resolve()?.name
}