package com.qingguoguo.butterknife;

import android.support.annotation.UiThread;

/**
 * @author :qingguoguo
 * @datetime ：2018/5/17
 * @describe :
 */

public interface UnBinder {

    @UiThread
    void unbind();

    //接口定义的属性，EMPTY的UnBinder
    UnBinder EMPTY = new UnBinder() {
        @Override
        public void unbind() {
        }
    };
}
