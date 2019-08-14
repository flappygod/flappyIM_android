package com.flappygo.flappyim.Tools;

import io.netty.buffer.ByteBuf;

import java.io.UnsupportedEncodingException;

public class NettyStringTool {

    //转换
    public static String byteBufToString(ByteBuf msg) {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        try {
            String body = new String(req, "UTF-8");
            return body;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

}
