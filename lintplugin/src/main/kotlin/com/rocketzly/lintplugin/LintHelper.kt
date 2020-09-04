package com.rocketzly.lintplugin

import org.gradle.api.Project

/**
 * User: Rocket
 * Date: 2020/9/4
 * Time: 2:05 PM
 */
interface LintHelper {
    fun apply(project: Project)
}