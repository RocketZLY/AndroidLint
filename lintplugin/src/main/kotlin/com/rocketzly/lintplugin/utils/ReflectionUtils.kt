package com.rocketzly.lintplugin.utils
import java.lang.reflect.Field

object ReflectionUtils {
    fun getDeclaredField(obj: Any, fieldName: String): Field? {
        var clazz: Class<*> = obj.javaClass
        while (clazz != Any::class.java) {
            try {
                return clazz.getDeclaredField(fieldName)
            } catch (e: Exception) {
            }
            clazz = clazz.superclass
        }
        return null
    }

    /**
     * 直接读取对象的属性值, 忽略 private/protected 修饰符, 也不经过 getter
     * @param object : 子类对象
     * @param fieldName : 父类中的属性名
     * @return : 父类中的属性值
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
}