package com.flappygo.flappyim.Tools;

import android.provider.Settings;
import android.content.Context;

import java.util.Collections;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yang on 2015/10/29.
 */
public class StringTool {


    /******
     * 大数转字符串
     * @param decimal decimal
     * @return 字符串
     */
    public static String decimalToStr(BigDecimal decimal) {
        if (decimal != null) {
            return decimal.toString();
        }
        return null;
    }

    /******
     * 大数转Int
     * @param decimal decimal
     * @return int
     */
    public static int decimalToInt(BigDecimal decimal) {
        if (decimal != null) {
            return decimal.intValue();
        }
        return 0;
    }

    /******
     * 大数转long
     * @param decimal decimal
     * @return long
     */
    public static long decimalToLong(BigDecimal decimal) {
        if (decimal != null) {
            return decimal.longValue();
        }
        return 0;
    }

    /******
     * 字符串转decimal
     * @param str 字符串
     * @return long
     */
    public static BigDecimal strToDecimal(String str) {
        if (str != null) {
            try {
                return new BigDecimal(str);
            } catch (Exception ex) {
                return new BigDecimal(0);
            }
        } else {
            return new BigDecimal(0);
        }
    }

    /******
     * 字符串转Long
     * @param str 字符串
     * @return long
     */
    public static long strToLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception ex) {
            return 0;
        }
    }


    /******
     * 获取两个用户的聊天唯一ID
     * @param userOne  用户1
     * @param userTwo  用户2
     * @return 唯一ID
     */
    public static String getTwoUserString(String userOne, String userTwo) {
        List<String> strList = new ArrayList<>();
        strList.add(userOne);
        strList.add(userTwo);
        Collections.sort(strList);
        return strList.get(0) + "-" + strList.get(1);
    }

    /******
     * 判断字符串是否为空的
     * @param str 字符串
     * @return 是否
     */
    public static boolean isEmpty(String str) {
        return str == null || str.equals("null") || str.equals("");
    }

    /******
     * 判断字符串是否为空的或者为0
     * @param str 字符串
     * @return 是否
     */
    public static boolean isEmptyOrZero(String str) {
        return str == null || str.equals("null") || str.equals("") || str.equals("0");
    }


    /******
     * 转换为不为null的字符串
     * @param str 字符串
     * @return 非null字符串
     */
    public static String ToNotNullStr(String str) {
        if (str == null || str.equals("null"))
            return "";
        return str;
    }


    /******
     * 转换为不为空的字符串，如果为空，用defaultStr代替
     * @param str        需要转换的字符串
     * @param defaultStr 默认的String
     * @return 字符串
     */
    public static String ToNotNullStrWithDefault(String str, String defaultStr) {
        if (str == null || str.equals("null") || str.equals(""))
            return defaultStr;
        return str;
    }


    /******
     * 转换
     * @param str 字符串
     * @param defaultValue 默认值
     * @return 结果
     */
    public static int strToInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    /******
     * 获取当前设备的唯一ID
     * @param context 上下文
     * @return ID
     */
    public static String getDeviceIDNumber(Context context) {
        return Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
    }
}
