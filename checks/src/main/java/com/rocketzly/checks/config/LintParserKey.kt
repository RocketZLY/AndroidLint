package com.rocketzly.checks.config

/**
 * User: Rocket
 * Date: 2021/9/13
 * Time: 4:52 下午
 */
class LintParserKey {
    companion object {
        /**
         * 避免使用的api
         */
        const val KEY_AVOID_USAGE_API = "avoid_usage_api"

        /**
         * 需要try catch处理的方法
         */
        const val KEY_HANDLE_EXCEPTION_METHOD = "handle_exception_method"

        /**
         * 有依赖使关系的方法
         */
        const val KEY_DEPENDENCY_API = "dependency_api"

        /**
         * 资源命名规范
         */
        const val KEY_RESOURCE_NAME = "resource_name"
    }
}