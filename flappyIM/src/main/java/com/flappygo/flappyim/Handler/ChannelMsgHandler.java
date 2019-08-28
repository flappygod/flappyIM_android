package com.flappygo.flappyim.Handler;

import android.os.Message;

import com.flappygo.flappyim.Callback.FlappyDeadCallback;
import com.flappygo.flappyim.Callback.FlappySendCallback;
import com.flappygo.flappyim.Config.BaseConfig;
import com.flappygo.flappyim.DataBase.Database;
import com.flappygo.flappyim.Datas.DataManager;
import com.flappygo.flappyim.FlappyImService;
import com.flappygo.flappyim.Models.Protoc.Flappy;
import com.flappygo.flappyim.Models.Request.Base.FlappyRequest;
import com.flappygo.flappyim.Models.Response.Base.FlappyResponse;
import com.flappygo.flappyim.Models.Response.SessionData;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.flappyim.Tools.NettyAttrUtil;
import com.flappygo.flappyim.Tools.StringTool;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;


//登录的handler
public class ChannelMsgHandler extends SimpleChannelInboundHandler<Flappy.FlappyResponse> {

    //回调
    private ConcurrentHashMap<String, HandlerSendCall> sendhandlers = new ConcurrentHashMap<>();

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

    //用于加锁
    private Byte[] lock = new Byte[1];


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

        //创建builder
        Flappy.ReqLogin.Builder loginInfoBuilder = Flappy.ReqLogin.newBuilder()
                .setDevice(BaseConfig.device)
                .setUserID(this.user.getUserId())
                .setPushid(StringTool.getDeviceUnicNumber(FlappyImService.getInstance().getAppContext()));

        //获取最近latest
        ChatUser user = DataManager.getInstance().getLoginUser();
        //消息
        if (user != null && user.getLatest() != null) {
            loginInfoBuilder.setLatest(user.getLatest());
        }
        //登录信息创建
        Flappy.ReqLogin loginInfo = loginInfoBuilder.build();

        //创建登录请求消息
        Flappy.FlappyRequest.Builder builder = Flappy.FlappyRequest.newBuilder()
                .setLogin(loginInfo)
                .setType(FlappyRequest.REQ_LOGIN);

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
                //消息
                List<ChatMessage> messages = new ArrayList<>();
                //每条消息进行插入
                for (int s = 0; s < response.getMsgCount(); s++) {
                    //得到真正的消息对象
                    ChatMessage chatMessage = new ChatMessage(response.getMsgList().get(s));
                    //添加
                    messages.add(chatMessage);
                    //消息发送成功
                    messageSendSuccess(chatMessage);
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

                //如果插入成功
                Database database = new Database();
                //遍历消息进行通知
                for (int s = 0; s < messages.size(); s++) {
                    //获取单个消息
                    ChatMessage chatMessage = messages.get(s);
                    //修改收到的状态
                    messageArrivedState(chatMessage);
                    //查看之前是否存在这个消息
                    ChatMessage message = database.getMessageByID(chatMessage.getMessageId());
                    //如果插入成功
                    if (message == null) {
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
                    messageArrivedReciept(messages.get(messages.size() - 1));
                }


                //所有消息更新
                database.insertMessages(messages);
                //更新所有会话
                database.insertSessions(handlerLogin.getLoginResponse().getSessions());
                //关闭数据库
                database.close();


                //发送成功消息
                Message msg = handlerLogin.obtainMessage(HandlerLoginCallback.LOGIN_SUCCESS);
                //数据
                msg.obj = response.getMsgList();
                //成功
                handlerLogin.sendMessage(msg);
                //清空引用
                handlerLogin = null;
            }
            checkSessionNeedUpdate();
        }
        //发送消息
        else if (response.getType() == FlappyResponse.RES_MSG) {

            Database database = new Database();
            //设置
            for (int s = 0; s < response.getMsgCount(); s++) {
                //得到真正的消息对象
                ChatMessage chatMessage = new ChatMessage(response.getMsgList().get(s));
                //有可能是发送成功的
                messageSendSuccess(chatMessage);
                //修改收到的状态
                messageArrivedState(chatMessage);
                //判断数据库是否存在
                ChatMessage former = database.getMessageByID(chatMessage.getMessageId());
                //更新最近一条信息
                ChatUser user = DataManager.getInstance().getLoginUser();
                //设置最近的世界
                user.setLatest(StringTool.decimalToStr(chatMessage.getMessageTableSeq()));
                //更新最近消息的世界
                DataManager.getInstance().saveLoginUser(user);
                //插入成功
                if (former == null) {
                    //发送成功消息
                    Message msg = new Message();
                    //收到新的消息
                    msg.what = HandlerMessage.MSG_RECIVE;
                    //消息数据体
                    msg.obj = chatMessage;
                    //成功
                    this.handlerMessage.sendMessage(msg);
                    //消息送达的回执
                    messageArrivedReciept(chatMessage);
                    //插入消息
                    database.insertMessage(chatMessage);
                }
            }
            database.close();
            //检查会话是否需要更新
            checkSessionNeedUpdate();

        }
        //更新数据
        else if (response.getType() == FlappyResponse.RES_UPDATE) {
            //进行会话更新
            List<Flappy.Session> session = response.getSessionsList();
            //设置
            Database database = new Database();
            //数据开始
            if (session != null && session.size() > 0) {
                for (int s = 0; s < session.size(); s++) {
                    //更新数据
                    SessionData data = new SessionData(session.get(s));
                    //插入数据
                    database.insertSession(data);
                    //消息标记为已经处理
                    List<ChatMessage> messages = database.getNotActionSystemMessage(data.getSessionId());
                    //将系统消息标记成为已经处理，不再需要重复处理
                    for (int w = 0; w < messages.size(); w++) {
                        //更新消息
                        if (data.getSessionStamp().longValue() >= StringTool.strToDecimal(messages.get(w).getChatSystem().getSysActionData()).longValue()) {
                            messages.get(w).setMessageReaded(new BigDecimal(1));
                            database.insertMessage(messages.get(w));
                        }
                    }
                }
            }
            database.close();
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

        synchronized (lock) {
            for (String key : sendhandlers.keySet()) {
                //获取
                HandlerSendCall call = sendhandlers.get(key);
                //不为空
                if (call != null) {
                    //发送失败的消息
                    Message message = call.obtainMessage(HandlerSendCall.SEND_FAILURE);
                    message.obj = new Exception("消息通道已关闭");
                    call.sendMessage(message);
                    //移除这个消息
                    sendhandlers.remove(call.getChatMessage().getMessageId());
                }
            }
            //清空
            sendhandlers.clear();
        }

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

    //检查会话是否需要更新
    private void checkSessionNeedUpdate() {

        //获取所有数据
        Database database = new Database();
        //获取系统消息
        List<ChatMessage> latestMessages = database.getNotActionSystemMessage();
        //开始处理
        database.close();

        HashMap<String, String> needUpdate = new HashMap<>();
        //遍历
        for (int s = 0; s < latestMessages.size(); s++) {
            String former = needUpdate.get(latestMessages.get(s).getMessageSession());
            if (former == null) {
                needUpdate.put(latestMessages.get(s).getMessageSession(), latestMessages.get(s).getChatSystem().getSysActionData());
            } else {
                //获取会话
                long stamp = StringTool.strToDecimal(former).longValue();
                long newStamp = StringTool.strToDecimal(latestMessages.get(s).getChatSystem().getSysActionData()).longValue();
                if (newStamp > stamp) {
                    needUpdate.put(latestMessages.get(s).getMessageSession(), latestMessages.get(s).getChatSystem().getSysActionData());
                }
            }
        }
        //遍历
        for (String key : needUpdate.keySet()) {
            //更新消息
            Flappy.ReqUpdate reqUpdate = Flappy.ReqUpdate.newBuilder()
                    .setUpdateID(key)
                    .setUpdateType(FlappyRequest.UPDATE_SESSION_SGINGLE)
                    .build();

            //创建登录请求消息
            Flappy.FlappyRequest.Builder builder = Flappy.FlappyRequest.newBuilder()
                    .setUpdate(reqUpdate)
                    .setType(FlappyRequest.REQ_UPDATE);

            //发送需要更新的消息
            channelHandlerContext.writeAndFlush(builder.build());

        }
    }


    //修改收到的状态
    private void messageArrivedState(ChatMessage msg) {
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        //自己发送的，已经发送
        if (chatUser.getUserId().equals(msg.getMessageSend())) {
            msg.setMessageSended(new BigDecimal(ChatMessage.SEND_STATE_SENDED));
        }
        //其他人发送的，已经到达
        else {
            msg.setMessageSended(new BigDecimal(ChatMessage.SEND_STATE_REACHED));
        }
    }

    //消息已经到达
    private void messageArrivedReciept(ChatMessage chatMessage) {
        //返回
        if (!chatMessage.getMessageSend().equals(DataManager.getInstance().getLoginUser().getUserId())) {

            //创建消息到达的回执
            Flappy.ReqReceipt receipt = Flappy.ReqReceipt.newBuilder()
                    .setReceiptID(chatMessage.getMessageTableSeq().toString())
                    .setReceiptType(FlappyRequest.RECEIPT_MSG_ARRIVE)
                    .build();

            //创建回执消息
            Flappy.FlappyRequest.Builder builder = Flappy.FlappyRequest.newBuilder()
                    .setReceipt(receipt)
                    .setType(FlappyRequest.REQ_RECEIPT);

            //发送回执，发送回执后，所有之前的消息都会被列为已经收到，因为端口是阻塞的
            channelHandlerContext.writeAndFlush(builder.build());
        }
    }

    //发送消息
    public void sendMessage(final ChatMessage chatMessage, final FlappySendCallback<ChatMessage> callback) {

        //之前是否存在
        HandlerSendCall former = getHandlerSendCall(chatMessage.getMessageId());
        //如果存在，则之前的失败
        if (former != null) {
            messageSendFailure(chatMessage, new Exception("消息重新发送"));
        }
        //发送
        try {//发送
            HandlerSendCall handlerSendCall = new HandlerSendCall(callback, chatMessage);
            //插入其中
            addHandlerSendCall(chatMessage.getMessageId(), handlerSendCall);
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
                    if (!future.isSuccess()) {
                        messageSendFailure(chatMessage, new Exception("连接已经断开"));
                    }
                }
            });
            //暂时不能确认发送状态，等收到回传的消息后才能真正确定发送已经成功，这个future,不一定成功
        } catch (Exception ex) {
            messageSendFailure(chatMessage, ex);
        }
    }


    //获取某个回调
    private synchronized HandlerSendCall getHandlerSendCall(String messageID) {
        synchronized (lock) {
            return sendhandlers.get(messageID);
        }
    }

    //添加回调
    private void addHandlerSendCall(String messageID, HandlerSendCall call) {
        synchronized (lock) {
            sendhandlers.put(messageID, call);
        }
    }

    //发送成功
    private synchronized void messageSendSuccess(ChatMessage chatMessage) {
        synchronized (lock) {
            HandlerSendCall call = sendhandlers.get(chatMessage.getMessageId());
            if (call != null) {
                //发送失败的消息
                Message message = call.obtainMessage(HandlerSendCall.SEND_SUCCESS);
                //消息
                message.obj = chatMessage;
                //成功的消息
                call.sendMessage(message);
                //移除这个消息
                sendhandlers.remove(chatMessage.getMessageId());
            }
        }
    }

    //发送失败
    private void messageSendFailure(ChatMessage chatMessage, Exception ex) {
        synchronized (lock) {
            HandlerSendCall call = sendhandlers.get(chatMessage.getMessageId());
            if (call != null) {
                //发送失败的消息
                Message message = call.obtainMessage(HandlerSendCall.SEND_FAILURE);
                //发送失败的原因
                message.obj = ex;
                //发送消息
                call.sendMessage(message);
                //移除这个消息
                sendhandlers.remove(chatMessage.getMessageId());
            }
        }
    }


}