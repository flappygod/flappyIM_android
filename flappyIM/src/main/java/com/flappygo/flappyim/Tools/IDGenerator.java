package com.flappygo.flappyim.Tools;


import java.util.Random;

/**
 * Created by lijunlin on 2018/3/11.
 */

public class IDGenerator {


    public static String generateCommomID() {
        return longTo36(System.currentTimeMillis()) + getRandomString(6);
    }


    //方法1：length为产生的位数
    public static String getRandomString(int length) {
        //定义一个字符串（A-Z，a-z，0-9）即62位；
        String str = "zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        //由Random生成随机数
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        //长度为几就循环几次
        for (int i = 0; i < length; ++i) {
            //产生0-61的数字
            int number = random.nextInt(62);
            //将产生的数字通过length次承载到sb中
            sb.append(str.charAt(number));
        }
        //将承载的字符转换成字符串
        return sb.toString().toUpperCase();
    }


    public static String longTo36(long time) {
        String[] str = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        //返回的字符串
        String retStr = "";
        //时间
        long ret = time;
        //如果比它大
        while (ret > 0) {
            int end = (int) (ret % str.length);
            ret = ret / str.length;
            retStr = str[end] + retStr;
        }
        return retStr;
    }


}
