package com.flappygo.flappyim.Handler;

import android.os.Message;

import com.flappygo.flappyim.Callback.FlappyDeadCallback;
import com.flappygo.flappyim.Callback.FlappyIMCallback;
import com.flappygo.flappyim.Config.BaseConfig;
import com.flappygo.flappyim.DataBase.Database;
import com.flappygo.flappyim.Datas.DataManager;
import com.flappygo.flappyim.FlappyImService;
import com.flappygo.flappyim.Models.Protoc.Flappy;
import com.flappygo.flappyim.Models.Request.Base.FlappyRequest;
import com.flappygo.flappyim.Models.Response.Base.FlappyResponse;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.flappyim.Tools.NettyAttrUtil;
import com.flappygo.flappyim.Tools.StringTool;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

import static com.flappygo.flappyim.Models.Server.ChatMessage.SEND_STATE_PUSHED;
import static com.flappygo.flappyim.Models.Server.ChatMessage.SEND_STATE_REACHED;


//登录的handler
public class ChannelMsgHandler extends SimpleChannelInboundHandler<Flappy.FlappyResponse> {


    //登录的回调
    private HandlerLoginCallback handlerLogin;

    //消息的handler
    private HandlerMessage handlerMessage;

    //用户数据
    private ChatUser user;

    //当前的channel
    private ChannelHandlerContext channelHandlerContext;

    //心跳包
    private Flappy.FlappyRequest heart;

    //回调
    private FlappyDeadCallback deadCallback;

    //回调
    public ChannelMsgHandler(HandlerLoginCallback handler, FlappyDeadCallback deadCallback, ChatUser user) {
        //心跳
        this.heart = Flappy.FlappyRequest.newBuilder().setType(FlappyRequest.REQ_PING).build();
        //消息接收监听
        this.handlerMessage = new HandlerMessage();
        //handler
        this.handlerLogin = handler;
        //回调
        this.deadCallback = deadCallback;
        //用户数据
        this.user = user;
    }


    //三次握手成功,发送登录验证
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        //创建登录消息
        Flappy.LoginInfo loginInfo = Flappy.LoginInfo.newBuilder()
                .setDevice(BaseConfig.device)
                .setUserID(this.user.getUserId())
                .setPushid(StringTool.getDeviceUnicNumber(FlappyImService.getInstance().getAppContext())).build();

        //创建登录请求消息
        Flappy.FlappyRequest.Builder builder = Flappy.FlappyRequest.newBuilder()
                .setLogin(loginInfo)
                .setType(FlappyRequest.REQ_LOGIN);
        //消息
        ChatUser user = DataManager.getInstance().getLoginUser();
        //消息
        if (user != null && user.getLatest() != null) {
            builder.setLatest(user.getLatest());
        }
        //发送登录消息
        ctx.writeAndFlush(builder.build());
        //context
        channelHandlerContext = ctx;
        //消息
        super.channelActive(ctx);
    }

    //断开连接
    public void channelInactive(ChannelHandlerContext context) throws Exception {
        //下线
        closeChannel(context);
        super.channelInactive(context);
    }

    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            System.out.println("客户端正在发送心跳包");
            //发送心跳包用于检测
            ctx.writeAndFlush(heart).addListeners(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    //如果发送给服务器是成功的
                    if (future.isSuccess()) {
                        //更新时间
                        NettyAttrUtil.updateReaderTime(ctx.channel(), System.currentTimeMillis());
                    }
                }
            });
            Long formerTime = NettyAttrUtil.getReaderTime(ctx.channel());
            //30秒没有收到信息了，关闭吧
            if (formerTime != null && System.currentTimeMillis() - formerTime > 30 * 1000) {
                //连接不通，下线
                closeChannel(ctx);
            }
        }
        super.userEventTriggered(ctx, evt);
    }


    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Flappy.FlappyResponse response) throws Exception {
        //转换为字符串
        //登录成功,现在才代表真正的登录成功
        if (response.getType() == FlappyResponse.RES_LOGIN) {

            //登录成功
            if (this.handlerLogin != null) {

                //保存用户成功的登录信息
                user.setLogin(1);
                //保存用户数据
                DataManager.getInstance().saveLoginUser(user);

                List<ChatMessage> messages = new ArrayList<>();
                //每条消息进行插入
                for (int s = 0; s < response.getMsgCount(); s++) {
                    //得到真正的消息对象
                    ChatMessage chatMessage = new ChatMessage(response.getMsgList().get(s));
                    messages.add(chatMessage);
                }

                //对消息进行排序，然后在插入数据库
                Collections.sort(messages, new Comparator<ChatMessage>() {
                    @Override
                    public int compare(ChatMessage chatMessage, ChatMessage t1) {
                        if (chatMessage.getMessageTableSeq().intValue() > t1.getMessageTableSeq().intValue()) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                });

                //遍历消息进行通知
                for (int s = 0; s < messages.size(); s++) {
                    //获取单个消息
                    ChatMessage chatMessage = messages.get(s);
                    //已经送达
                    chatMessage.setMessageSended(new BigDecimal(SEND_STATE_PUSHED));
                    //如果插入成功
                    boolean flag = Database.getInstance().insertMessage(chatMessage);
                    //如果插入成功
                    if (flag) {
                        //收到了新的消息
                        Message msg = new Message();
                        //收到新的消息
                        msg.what = HandlerMessage.MSG_RECIVE;
                        //消息数据体
                        msg.obj = chatMessage;
                        //成功
                        this.handlerMessage.sendMessage(msg);
                    }
                }

                //保存最后的offset
                if (messages.size() > 0) {
                    //用户信息
                    ChatUser user = DataManager.getInstance().getLoginUser();
                    //设置最近的一条
                    user.setLatest(StringTool.decimalToStr(messages.get(messages.size() - 1).getMessageTableSeq()));
                    //更新数据信息
                    DataManager.getInstance().saveLoginUser(user);
                    //到达
                    sendMessageArrive(messages.get(messages.size() - 1));
                }
                //发送成功消息
                Message msg = handlerLogin.obtainMessage(HandlerLoginCallback.LOGIN_SUCCESS);
                //数据
                msg.obj = response.getMsgList();
                //成功
                handlerLogin.sendMessage(msg);
                //清空引用
                handlerLogin = null;
            }
        }
        //发送消息
        else if (response.getType() == FlappyResponse.RES_MSG) {
            //设置
            for (int s = 0; s < response.getMsgCount(); s++) {
                //得到真正的消息对象
                ChatMessage chatMessage = new ChatMessage(response.getMsgList().get(s));
                //已经送达
                chatMessage.setMessageSended(new BigDecimal(SEND_STATE_PUSHED));
                //判断数据库是否存在
                boolean flag = Database.getInstance().insertMessage(chatMessage);
                //插入成功
                if (flag) {

                    //更新最近一条信息
                    ChatUser user = DataManager.getInstance().getLoginUser();
                    //设置最近的世界
                    user.setLatest(StringTool.decimalToStr(chatMessage.getMessageTableSeq()));
                    //更新最近消息的世界
                    DataManager.getInstance().saveLoginUser(user);


                    //发送成功消息
                    Message msg = new Message();
                    //收到新的消息
                    msg.what = HandlerMessage.MSG_RECIVE;
                    //消息数据体
                    msg.obj = chatMessage;
                    //成功
                    this.handlerMessage.sendMessage(msg);

                    sendMessageArrive(chatMessage);
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        closeChannel(ctx);
    }

    //清空回调
    public void closeRegular() {
        this.deadCallback = null;
    }

    //用户下线
    private void closeChannel(ChannelHandlerContext context) {
        //如果这里还没有回调成功，那么就是登录失败
        if (this.handlerLogin != null) {
            //创建消息
            Message message = new Message();
            //失败
            message.what = HandlerLoginCallback.LOGIN_FAILURE;
            //错误
            message.obj = new Exception("channel closed");
            //发送消息
            this.handlerLogin.sendMessage(message);
            //清空引用
            this.handlerLogin = null;
        }
        //线程非正常退出
        if (deadCallback != null) {
            //死亡
            deadCallback.dead();
            //清空
            deadCallback = null;
        }
        //关闭与服务器的连接
        context.close();
    }

    //消息已经到达
    private void sendMessageArrive(ChatMessage chatMessage) {
        //返回
        if (!chatMessage.getMessageSend().equals(DataManager.getInstance().getLoginUser().getUserId())) {

            //创建登录请求消息
            Flappy.FlappyRequest.Builder builder = Flappy.FlappyRequest.newBuilder()
                    .setLatest(chatMessage.getMessageTableSeq().toString())
                    .setType(FlappyRequest.REQ_RECIEVE);

            //发送收到消息的回执
            channelHandlerContext.writeAndFlush(builder.build());
        }
    }

    //发送消息
    public void sendMessage(ChatMessage chatMessage, final FlappyIMCallback<String> callback) {
        //发送
        final HandlerSendCall handlerSendCall = new HandlerSendCall(callback);
        //发送
        try {
            //创建登录消息
            Flappy.Message message = chatMessage.toProtocMessage(Flappy.Message.newBuilder());
            //创建请求消息
            Flappy.FlappyRequest.Builder builder = Flappy.FlappyRequest.newBuilder()
                    .setMsg(message)
                    .setType(FlappyRequest.REQ_MSG);
            //消息发送
            ChannelFuture future = channelHandlerContext.channel().writeAndFlush(builder.build());
            //发送成功
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    //发送成功
                    if (future.isSuccess()) {
                        //发送成功
                        Message message = handlerSendCall.obtainMessage(HandlerSendCall.SEND_SUCCESS);
                        message.obj = "发送成功";
                        handlerSendCall.sendMessage(message);

                    } else {
                        //发送失败
                        Message message = handlerSendCall.obtainMessage(HandlerSendCall.SEND_FAILURE);
                        message.obj = new Exception("连接已经断开");
                        handlerSendCall.sendMessage(message);
                    }
                }
            });
        } catch (Exception ex) {
            //发送失败
            Message message = handlerSendCall.obtainMessage(HandlerSendCall.SEND_FAILURE);
            message.obj = ex;
            handlerSendCall.sendMessage(message);
        }
    }

}