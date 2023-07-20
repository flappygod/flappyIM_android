package com.flappygo.flappyim.Tools;


import java.util.Random;
import java.util.UUID;

/**
 * Created by lijunlin on 2018/3/11.
 */

public class IDGenerator {

    //use this to avoid repeat
    public static String generateCommonID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        return str.replace("-", "");
    }


}
