package com.flappygo.flappyim.Tools;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yang on 2016/8/12.
 * version  1.0.0
 */
public class DateTimeTool {


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
