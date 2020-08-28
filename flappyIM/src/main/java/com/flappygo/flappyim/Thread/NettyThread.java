package com.flappygo.flappyim.Thread;

import android.os.Message;

import com.flappygo.flappyim.Handler.HandlerLoginCallback;
import com.flappygo.flappyim.Handler.ChannelMsgHandler;
import com.flappygo.flappyim.Models.Protoc.Flappy;
import com.flappygo.flappyim.Models.Server.ChatUser;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

//用于和服务器保持长连接的线程
public class NettyThread extends Thread {


    private ChatUser user;

    //当前的channel
    private ChannelFuture future;

    //登录的回调
    private HandlerLoginCallback loginHandler;

    //线程死亡的回调
    private NettyThreadDead deadCallback;

    //获取channel
    public ChannelMsgHandler getChannelMsgHandler() {
        return channelMsgHandler;
    }

    //消息的handler
    private ChannelMsgHandler channelMsgHandler;

    //group
    private EventLoopGroup group;

    //服务器的IP
    private String serverIP;

    //服务器的端口
    private int serverPort;

    //线程构造
    public NettyThread(
            ChatUser user,
            String serverIP,
            int serverPort,
            HandlerLoginCallback loginHandler,
            NettyThreadDead deadCallback) {
        this.user = user;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.loginHandler = loginHandler;
        this.deadCallback = deadCallback;
        this.channelMsgHandler = new ChannelMsgHandler(loginHandler, deadCallback, user);
    }

    //当前是否已经连接
    public boolean isConnected() {
        if (future != null && future.channel() != null && future.channel().isActive()) {
            return true;
        } else {
            return false;
        }
    }

    //连接服务器
    private boolean connect() {
        //连接
        group = new NioEventLoopGroup();
        //设置
        Bootstrap bootstrap = new Bootstrap();
        //group
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline p = channel.pipeline();
                        //用于心跳,十秒钟没有事件就开始心跳
                        p.addLast(new IdleStateHandler(0, 10, 0))
                                .addLast(new ProtobufVarint32FrameDecoder())
                                .addLast(new ProtobufDecoder(Flappy.FlappyResponse.getDefaultInstance()))
                                .addLast(new ProtobufVarint32LengthFieldPrepender())
                                .addLast(new ProtobufEncoder())
                                .addLast(channelMsgHandler);

                    }
                });
        //开始连接
        try {
            future = bootstrap.connect(
                    new InetSocketAddress(
                            serverIP,
                            serverPort)).sync();
        } catch (Exception e) {
            //假如存在登录回调
            closeConnection(e);
            //设置为空
            future = null;
            //返回false
            return false;
        }
        //成功连接
        return true;
    }

    //下线
    public void offline() {
        //正常退出
        deadCallback.disable();
        //关闭连接
        closeConnection(new Exception("NETTY 线程被关闭"));
    }

    //关闭连接
    public void closeConnection(Exception ex) {
        //登录失败
        loginFaulure(ex);
        //通知死亡
        if (deadCallback != null) {
            deadCallback.dead();
        }
        try {
            //这一步会阻塞住
            if (future != null) {
                //关闭
                future.channel().close().sync();
                //清空
                future = null;
            }
            if (group != null) {
                group.shutdownGracefully();
            }
        } catch (Exception e) {
            //清空当前的future
            future = null;
        }
    }

    //登录失败
    private void loginFaulure(Exception ex) {
        //假如存在登录回调
        synchronized (this) {
            if (loginHandler != null) {
                Message message = new Message();
                //失败
                message.what = HandlerLoginCallback.LOGIN_FAILURE;
                //失败excepiton
                message.obj = ex;
                //发送消息，只执行一次
                this.loginHandler.sendMessage(message);
                //清空
                loginHandler = null;
            }
        }
    }

    //运行
    public void run() {
        //当前线程开始连接服务器
        connect();
    }

}
