package com.qingguoguo.butterknife;

import android.app.Activity;
import android.view.View;

/**
 * @author :qingguoguo
 * @datetime ：2018/5/17
 * @describe :
 */

public class Utils {
    //封装 findViewById方法
    public static <T extends View> T findViewById(Activity activity, int id) {
        return (T) activity.findViewById(id);
    }
}
