package com.rocketzly.gradle.api.utils

import java.lang.reflect.Field
import java.lang.reflect.Method

object ReflectionUtils {
    fun getDeclaredField(obj: Any, fieldName: String): Field? {
        var clazz = getClazz(obj)
        while (clazz != Any::class.java) {
            try {
                return clazz.getDeclaredField(fieldName)
            } catch (e: Exception) {
            }
            clazz = clazz.superclass
        }
        return null
    }

    fun getDeclaredMethod(obj: Any, methodName: String, paramsType: Array<Class<*>>): Method? {
        var clazz = getClazz(obj)
        while (clazz != Any::class.java) {
            try {
                return clazz.getDeclaredMethod(methodName, *paramsType)
            } catch (e: Exception) {
            }
            clazz = clazz.superclass
        }
        return null
    }

    private fun getClazz(obj: Any): Class<*> = if (obj is Class<*>) {
        obj
    } else {
        obj::class.java
    }

    /**
     * 直接读取对象的属性值, 忽略 private/protected 修饰符, 也不经过 getter
     * @param object : 对象实例 or Class
     * @param fieldName : 属性名
     * @return : 属性值
     */
    fun getFieldValue(obj: Any, fieldName: String): Any? {
        //根据 对象和属性名通过反射 调用上面的方法获取 Field对象
        val field: Field? = getDeclaredField(obj, fieldName)
        field?.isAccessible = true
        try {
            //获取 object 中 field 所代表的属性值

            return field?.get(obj)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 直接设置对象的属性值, 忽略 private/protected 修饰符, 也不经过 setter
     * @param object : 对象实例 or Class
     * @param fieldName : 属性名
     * @return : Unit
     */
    fun setFieldValue(obj: Any, fieldName: String, value: Any?) {
        val field: Field? = getDeclaredField(obj, fieldName)
        field?.isAccessible = true
        field?.set(obj, value)
    }

    /**
     * 反射调用调用方法,
     * @param object : 对象实例 or Class，传class的话调的是静态方法
     * @param methodName : 方法名
     * @return : 方法返回值
     */
    fun invokeMethod(
        obj: Any,
        methodName: String,
        paramsType: Array<Class<*>>,
        args: Array<Any>
    ): Any? {
        val declaredMethod = getDeclaredMethod(obj, methodName, paramsType)
        declaredMethod?.isAccessible = true
        return declaredMethod?.invoke(obj, *args)
    }
}