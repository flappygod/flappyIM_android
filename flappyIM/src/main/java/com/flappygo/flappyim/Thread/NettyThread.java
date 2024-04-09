package com.flappygo.flappyim.Thread;


import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;

import com.flappygo.flappyim.Handler.HandlerLogin;

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


/******
 * 用于和服务器保持长连接的线程
 */
public class NettyThread extends Thread {


    //当前的channel
    private ChannelFuture channelFuture;

    //登录的回调
    private HandlerLogin loginHandler;

    //线程死亡的回调
    private final NettyThreadDeadListener deadCallback;

    //group
    private EventLoopGroup group;

    //bootstrap
    private Bootstrap bootstrap;

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
            HandlerLogin loginHandler,
            NettyThreadDeadListener deadCallback) {
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
    private void startConnect() throws InterruptedException {
        //连接
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        ChannelHandler handler = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) {
                ChannelPipeline p = channel.pipeline();
                //用于心跳,12秒钟没有事件就开始心跳
                p.addLast(new IdleStateHandler(0, FlappyConfig.getInstance().IdleSeconds, 0))
                        //解决粘包半包问题
                        .addLast(new ProtobufVarint32FrameDecoder())
                        //解析
                        .addLast(new ProtobufDecoder(Flappy.FlappyResponse.getDefaultInstance()))
                        //解析
                        .addLast(new ProtobufVarint32LengthFieldPrepender())
                        //解析
                        .addLast(new ProtobufEncoder())
                        //解析
                        .addLast(channelMsgHandler);
            }
        };
        bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true).handler(handler);
        channelFuture = bootstrap.connect(new InetSocketAddress(serverIP, serverPort)).sync();
    }


    //关闭连接
    public void closeConnect(Exception ex) {
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
            //设置为空
            bootstrap = null;
        } catch (Exception e) {
            //清空当前数据
            group = null;
            bootstrap = null;
            channelFuture = null;
        }
    }

    //登录失败
    private void loginFailure(Exception ex) {
        synchronized (this) {
            //NSThread 现成死亡
            if (deadCallback != null) {
                deadCallback.disconnected();
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
        closeConnect(new Exception("NETTY 线程被关闭"));
    }

    //运行
    public void run() {
        //开始连接
        try {
            startConnect();
        } catch (Exception e) {
            closeConnect(e);
        }
    }

}
