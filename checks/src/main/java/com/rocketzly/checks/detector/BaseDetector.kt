package com.rocketzly.checks.detector

import com.android.tools.lint.detector.api.Context
import com.android.tools.lint.detector.api.Detector
import com.rocketzly.checks.config.LintConfigProvider

/**
 * User: Rocket
 * Date: 2020/5/27
 * Time: 8:07 PM
 */
open class BaseDetector : Detector() {

    lateinit var lintConfigProvider: LintConfigProvider

    override fun beforeCheckRootProject(context: Context) {
        super.beforeCheckRootProject(context)
        lintConfigProvider = LintConfigProvider.getInstance(context)
    }
}