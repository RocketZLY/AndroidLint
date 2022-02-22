package com.rocketzly.gradle.utils

import com.rocketzly.gradle.AndroidGradleCompatException
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * User: Rocket
 * Date: 2022/1/9
 * Time: 8:24 下午
 */

/**
 * android插件
 */
val Project.androidPlugin: Plugin<*>
    get() {
        val pluginContainer = this.plugins

        val appPlugin =
            pluginContainer.findPlugin("com.android.internal.application")
        if (appPlugin != null) return appPlugin

        val libraryPlugin =
            pluginContainer.findPlugin("com.android.internal.library")
        if (libraryPlugin != null) return libraryPlugin

        throw AndroidGradleCompatException("未找到basePlugin")
    }

/**
 * VariantManager，Any类型使用的时候在转换成对应版本的VariantManager
 */
val Plugin<*>.variantManager: Any
    get() {
        val variantManagerStr = "variantManager"
        return ReflectionUtils.getFieldValue(
            this,
            variantManagerStr
        ) ?: throw AndroidGradleCompatException("未找到variantManager")
    }

/**
 * BaseExtension，Any类型使用的时候在转换成对应版本的BaseExtension
 */
val Plugin<*>.extension: Any
    get() {
        return ReflectionUtils.invokeMethod(
            this,
            "getExtension",
            arrayOf(),
            arrayOf()
        ) ?: throw AndroidGradleCompatException("未找到extension")
    }