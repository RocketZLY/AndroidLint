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
        fun match(baseConfig: BaseConfigProperty, qualifiedName: String?): Boolean {
            return match(baseConfig.name, baseConfig.nameRegex, qualifiedName)
        }

        fun match(name: String?, nameRegex: String?, qualifiedName: String?): Boolean {
            qualifiedName ?: return false
            if (name != null && name.isNotEmpty() && name == qualifiedName) {//优先匹配name
                return true
            }
            if (nameRegex != null && nameRegex.isNotEmpty() &&
                Pattern.compile(nameRegex).matcher(qualifiedName).find()
            ) {//在匹配nameRegex
                return true
            }
            return false
        }
    }
}