package com.flappygo.flappyim.Tools;

import io.netty.util.AttributeKey;
import io.netty.channel.Channel;
import io.netty.util.Attribute;

public class NettyAttrUtil {

    //创建时间
    private static final AttributeKey<String> ATTR_KEY_READER_TIME = AttributeKey.valueOf("readerTime");

    //更新时间
    public static void updateReaderTime(Channel channel, Long time) {
        channel.attr(ATTR_KEY_READER_TIME).set(time.toString());
    }

    //获取时间
    public static Long getReaderTime(Channel channel) {
        String value = getAttribute(channel, ATTR_KEY_READER_TIME);
        if (value != null) {
            return Long.valueOf(value);
        }
        return null;
    }

    //获取Attribute
    public static String getAttribute(Channel channel, AttributeKey<String> key) {
        Attribute<String> attr = channel.attr(key);
        return attr.get();
    }
}

