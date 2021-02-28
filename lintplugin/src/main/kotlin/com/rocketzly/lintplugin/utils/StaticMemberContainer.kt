package com.rocketzly.lintplugin.utils

/**
 * 静态成员缓存容器，方便统一管理
 * Created by rocketzly on 2021/2/27.
 */
class StaticMemberContainer {

    enum class Key {

        LINT_CONFIG_EXTENSION,
        AGP_VERSION,
        APP_PLUGIN_ID,
        LIBRARY_PLUGIN_ID
    }

    companion object {
        private val map = mutableMapOf<Key, Any>()

        fun put(k: Key, v: Any) {
            map[k] = v
        }

        @Suppress("UNCHECKED_CAST")
        fun <R> get(k: Key): R? {
            return map[k] as R?
        }

        fun <R> get(k: Key, valueGenerator: () -> R): R {
            var res = get<R>(k)
            if (res == null) {
                res = valueGenerator()
                put(k, res!!)
            }
            return res
        }

        fun reset() {
            map.clear()
        }
    }
}