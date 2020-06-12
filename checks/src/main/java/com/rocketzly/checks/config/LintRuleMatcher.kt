package com.rocketzly.checks.config

import com.rocketzly.checks.config.bean.BaseConfigProperty
import java.util.regex.Pattern

/**
 * lint规则匹配器
 * User: Rocket
 * Date: 2020/6/12
 * Time: 4:38 PM
 */
class LintRuleMatcher {
    companion object {
        fun match(baseConfig: BaseConfigProperty, qualifiedName: String): Boolean {
            if (baseConfig.name.isNotEmpty() && baseConfig.name == qualifiedName) {//优先匹配name
                return true
            }
            if (baseConfig.nameRegex.isNotEmpty() &&
                Pattern.compile(baseConfig.nameRegex).matcher(qualifiedName).matches()
            ) {//在匹配nameRegex
                return true
            }
            return false
        }
    }
}