package com.itheima.reggie.common;


/**
 * 基于ThreadLocal的工具类，用于保存和获取当前登录用户的ID（无法使用session时候）
 */

public class BaseContext {      //作用范围在一个线程之内
    //新建全局ThreadLocal对象
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前用户的id
     * @param id
     */
    public static void setCurrentID(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取当前用户的id
     * @return
     */
    public static Long getCurrentID(){
        return threadLocal.get();
    }
}
