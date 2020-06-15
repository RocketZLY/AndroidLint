package com.rocketzly.checks

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import com.rocketzly.checks.detector.AvoidInheritClassDetector
import com.rocketzly.checks.detector.HandleExceptionDetector
import com.rocketzly.checks.detector.SerializableClassDetector
import com.rocketzly.checks.detector.AvoidUsageApiDetector

/**
 * User: Rocket
 * Date: 2020/5/27
 * Time: 3:55 PM
 */
class CustomIssueRegistry : IssueRegistry() {

    override val issues: List<Issue>
        get() = listOf(
            SerializableClassDetector.ISSUE,
            HandleExceptionDetector.ISSUE,
            AvoidUsageApiDetector.ISSUE,
            AvoidInheritClassDetector.ISSUE
        )

    override val api: Int
        get() = CURRENT_API
}