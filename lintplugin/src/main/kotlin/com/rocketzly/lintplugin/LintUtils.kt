package com.rocketzly.lintplugin

import org.gradle.api.Project

/**
 * User: Rocket
 * Date: 2020/9/1
 * Time: 3:13 PM
 */
class LintUtils {
}

fun Project.isRootProject() = this.rootProject == this