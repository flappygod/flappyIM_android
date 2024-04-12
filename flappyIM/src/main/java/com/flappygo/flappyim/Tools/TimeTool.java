package com.flappygo.flappyim.Tools;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/******
 * 日期时间工具类
 */
public class TimeTool {

    //定义格式
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //将时间格式化为字符串
    public static String dateToStr(Date date) {
        //不为空进行处理
        if (date != null) {
            try {
                return df.format(date);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    //字符串转换为时间
    public static Date strToDate(String str) {
        //不为空进行处理
        if (str != null) {
            try {
                return df.parse(str);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
