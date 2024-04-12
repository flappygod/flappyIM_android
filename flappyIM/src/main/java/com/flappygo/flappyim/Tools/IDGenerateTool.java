package com.flappygo.flappyim.Tools;

import java.util.UUID;

/******
 * ID生成器
 */

public class IDGenerateTool {

    //使用UUID防止重复的message等
    public static String generateCommonID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        return str.replace("-", "");
    }
}
