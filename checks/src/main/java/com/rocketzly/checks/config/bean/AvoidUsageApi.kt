package com.rocketzly.checks.config.bean


/**
 * 避免调用api
 * User: Rocket
 * Date: 2020/6/10
 * Time: 11:14 AM
 */
class AvoidUsageApi(
    var method: List<AvoidUsageMethod> = listOf(),
    var construction: List<AvoidUsageConstructor> = listOf(),
    var inherit: List<AvoidInheritClass> = listOf()
)

/**
 * 避免调用的方法
 */
class AvoidUsageMethod : BaseConfigProperty()

/**
 * 避免创建的类
 */
class AvoidUsageConstructor : BaseConfigProperty()

/**
 * 避免继承或者实现的类
 */
class AvoidInheritClass : BaseConfigProperty()