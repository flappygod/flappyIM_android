package com.flappygo.flappyim.Tools;

import android.content.Context;
import android.provider.Settings;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yang on 2015/10/29.
 */
public class StringTool {

    public static String decimalToStr(BigDecimal decimal) {
        if (decimal != null) {
            return decimal.toString();
        }
        return null;
    }

    public static int decimalToInt(BigDecimal decimal) {
        if (decimal != null) {
            return decimal.intValue();
        }
        return 0;
    }

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

    public static long strToLong(String data) {
        try {
            if (data != null && !data.isEmpty()) {
                return Long.parseLong(data);
            }
            return 0;
        } catch (Exception ex) {
            return 0;
        }
    }

    /**********
     * 判断字符串是否为空的
     *
     * @param str 字符串
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.equals("null") || str.equals(""))
            return true;
        return false;
    }

    /**********
     * 判断字符串是否为空的或者为0
     *
     * @param str 字符串
     * @return
     */
    public static boolean isEmptyOrZero(String str) {
        if (str == null || str.equals("null") || str.equals("") || str.equals("0"))
            return true;
        return false;
    }


    /*****************
     * 转换为不为null的字符串
     *
     * @param str 字符串
     * @return
     */
    public static String ToNotNullStr(String str) {
        if (str == null || str.equals("null"))
            return "";
        return str;
    }

    /*****************
     * 去掉可能为空的情况，并用零代替空
     *
     * @param str 字符串
     * @return
     */
    public static String ToNotEmptyZeroStr(String str) {
        if (str == null || str.equals("null") || str.equals(""))
            return "0";
        return str;
    }


    /**************
     * 转换为不为空的字符串，如果为空，用defaultStr代替
     *
     * @param str        需要转换的字符串
     * @param defaultStr 默认的String
     * @return
     */
    public static String ToNotNullStrWithDefault(String str, String defaultStr) {
        if (str == null || str.equals("null") || str.equals(""))
            return defaultStr;
        return str;
    }

    /**************
     * 用于设置用户名不能为纯数字
     *
     * @return
     */

    public static boolean isNumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        final int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static boolean isMobileNumber(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 判断邮箱是否合法
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        String str = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }


    /*******
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
     * @param  s 需要得到长度的字符串
     * @return int 得到的字符串长度
     */
    public static int charLength(String s) {
        if (s == null)
            return 0;
        char[] c = s.toCharArray();
        int len = 0;
        for (int i = 0; i < c.length; i++) {
            len++;
            if (!isLetter(c[i])) {
                len++;
            }
        }
        return len;
    }

    public static boolean isLetter(char c) {
        int k = 0x80;
        return c / k == 0 ? true : false;
    }

    public static String getLimitString(String str, int length) {
        if (str == null)
            return str;
        char[] c = str.toCharArray();
        int len = 0;
        for (int i = 0; i < c.length; i++) {
            len++;
            if (!isLetter(c[i])) {
                len++;
            }
            if (len > length && i > 1) {
                String ret = str.substring(0, i);
                return ret;
            }
        }
        return str;
    }

    public static int strToInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception ex) {
            return defaultValue;
        }
    }


    public static List<String> splitStrList(String str, String split) {
        List<String> rets = new ArrayList<>();
        if (str != null && split != null) {
            String[] strs = str.split(split);
            for (int s = 0; s < strs.length; s++) {
                rets.add(strs[s]);
            }
        }
        return rets;
    }

    public static String strListToStr(List<String> strs, String split) {
        if (strs != null && split != null) {
            String retStr = "";
            for (int s = 0; s < strs.size(); s++) {
                if (s == strs.size() - 1) {
                    retStr = retStr + strs.get(s);
                } else {
                    retStr = retStr + strs.get(s) + split;
                }
            }
            return retStr;
        }
        return null;
    }

    //获取当前设备的唯一ID
    public static String getDeviceUnicNumber(Context context) {
        String ANDROID_ID = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        return ANDROID_ID;
    }
}
