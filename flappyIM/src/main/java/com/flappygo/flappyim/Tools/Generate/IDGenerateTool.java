package com.flappygo.flappyim.Tools.Generate;

import java.util.UUID;

/******
 * ID生成器
 */

public class IDGenerateTool {

    /******
     * 使用UUID防止重复的message等
     * @return UUID
     */
    public static String generateCommonID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        return str.replace("-", "");
    }

    /******
     * 生成一个指定长度的随机数
     * @param length 长度
     * @return 随机字符串
     */
    public static String getRandomStr(int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int intVal = (int) (Math.random() * 26 + 97);
            result.append((char) intVal);
        }
        return result.toString();
    }

}
