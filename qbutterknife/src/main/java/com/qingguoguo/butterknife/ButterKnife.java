package com.qingguoguo.butterknife;

import android.app.Activity;

import java.lang.reflect.Constructor;

/**
 * @author :qingguoguo
 * @datetime ：2018/5/17
 * @describe :
 */

public class ButterKnife {
    public static UnBinder bind(Activity activity) {
        //获取activity的Class
        Class<?> activityClass = activity.getClass();
        //获取activity的全路径名
        String activityName = activityClass.getName();
        //拼接className
        String className = activityName + "_ViewBinding";
        try {
            //获取生成类的Class
            Class clazz = Class.forName(className);
            //获取构造函数
            Constructor constructor = clazz.getDeclaredConstructor(activityClass);
            //创建生成类的对象
            UnBinder unBinder = (UnBinder) constructor.newInstance(activity);
            return unBinder;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果出错返回EMPTY
        return UnBinder.EMPTY;
    }
}
