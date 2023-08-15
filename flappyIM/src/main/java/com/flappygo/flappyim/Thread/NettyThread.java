package com.flappygo.flappyim.Thread;


import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;

import com.flappygo.flappyim.Handler.HandlerLoginCallback;

import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import com.flappygo.flappyim.Handler.ChannelMsgHandler;

import io.netty.channel.socket.nio.NioSocketChannel;

import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.flappyim.Models.Protoc.Flappy;

import io.netty.handler.timeout.IdleStateHandler;

import com.flappygo.flappyim.Config.FlappyConfig;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.bootstrap.Bootstrap;
import java.net.InetSocketAddress;


//用于和服务器保持长连接的线程
public class NettyThread extends Thread {


    //当前的channel
    private ChannelFuture channelFuture;

    //登录的回调
    private HandlerLoginCallback loginHandler;

    //线程死亡的回调
    private final NettyThreadDead deadCallback;

    //group
    private EventLoopGroup group;

    //消息的handler
    private final ChannelMsgHandler channelMsgHandler;

    //服务器的IP
    private final String serverIP;

    //服务器的端口
    private final int serverPort;


    //获取channel
    public ChannelMsgHandler getChannelMsgHandler() {
        return channelMsgHandler;
    }

    //线程构造
    public NettyThread(
            ChatUser user,
            String serverIP,
            int serverPort,
            HandlerLoginCallback loginHandler,
            NettyThreadDead deadCallback) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.loginHandler = loginHandler;
        this.deadCallback = deadCallback;
        this.channelMsgHandler = new ChannelMsgHandler(loginHandler, deadCallback, user);
    }

    //当前是否已经连接
    public boolean isConnected() {
        return channelFuture != null && channelFuture.channel() != null && channelFuture.channel().isActive();
    }

    //连接服务器
    private void connect() {
        //开始连接
        try {
            //连接
            group = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) {
                            ChannelPipeline p = channel.pipeline();
                            //用于心跳,12秒钟没有事件就开始心跳
                            p.addLast(new IdleStateHandler(0, FlappyConfig.getInstance().IdleSeconds, 0))
                                    .addLast(new ProtobufVarint32FrameDecoder())
                                    .addLast(new ProtobufDecoder(Flappy.FlappyResponse.getDefaultInstance()))
                                    .addLast(new ProtobufVarint32LengthFieldPrepender())
                                    .addLast(new ProtobufEncoder())
                                    .addLast(channelMsgHandler);
                        }
                    });
            channelFuture = bootstrap.connect(
                    new InetSocketAddress(
                            serverIP,
                            serverPort)).sync();
        } catch (Exception e) {
            //出现错误直接关闭
            closeConnection(e);
        }
    }


    //关闭连接
    public void closeConnection(Exception ex) {
        //登录失败
        loginFailure(ex);
        try {
            //这一步会阻塞住
            if (channelFuture != null) {
                channelFuture.channel().close().sync();
                channelFuture = null;
            }
            //这一步会阻塞住
            if (group != null) {
                group.shutdownGracefully();
                group = null;
            }
        } catch (Exception e) {
            //清空当前数据
            channelFuture = null;
            group = null;
        }
    }

    //登录失败
    private void loginFailure(Exception ex) {
        synchronized (this) {
            //NSThread 现成死亡
            if (deadCallback != null) {
                deadCallback.dead();
            }
            //登录的handler发送失败信息
            if (loginHandler != null) {
                loginHandler.loginFailure(ex);
                loginHandler = null;
            }
        }
    }


    //下线
    public void offline() {
        //正常退出
        deadCallback.disable();
        //关闭连接
        closeConnection(new Exception("NETTY 线程被关闭"));
    }

    //运行
    public void run() {
        connect();
    }

}
