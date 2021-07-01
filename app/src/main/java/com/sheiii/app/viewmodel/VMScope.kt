package com.sheiii.app.viewmodel

/**
 * @author created by Zhenqing He on  10:01
 * @description 用于标记vm的作用域
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class VMScope(val scopeName:String) {
}