package com.flappygo.flappyim.Handler;

import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_ACTION;
import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_SYSTEM;

import com.flappygo.flappyim.Models.Response.Base.FlappyResponse;
import com.flappygo.flappyim.DataBase.Models.SessionMemberModel;
import com.flappygo.flappyim.Models.Request.Base.FlappyRequest;
import com.flappygo.flappyim.Tools.Generate.IDGenerateTool;
import com.flappygo.flappyim.DataBase.Models.SessionModel;
import com.flappygo.flappyim.Callback.FlappySendCallback;
import com.flappygo.flappyim.Thread.NettyThreadListener;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Models.Request.ChatSystem;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Models.Server.ChatUser;

import io.netty.channel.SimpleChannelInboundHandler;

import com.flappygo.flappyim.Tools.Secret.RSATool;
import com.flappygo.flappyim.Models.Protoc.Flappy;
import com.flappygo.flappyim.Config.FlappyConfig;
import com.flappygo.flappyim.Tools.NettyAttrTool;
import com.flappygo.flappyim.DataBase.Database;
import com.flappygo.flappyim.Datas.DataManager;

import io.netty.handler.timeout.IdleStateEvent;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import com.flappygo.flappyim.Tools.StringTool;

import io.netty.channel.ChannelFuture;

import java.util.Collections;
import java.math.BigDecimal;
import java.util.ArrayList;

import android.os.Message;

import java.util.List;


//登录的handler
public class ChannelMsgHandler extends SimpleChannelInboundHandler<Flappy.FlappyResponse> {

    //登录的回调
    private HandlerLogin handlerLogin;

    //用户数据
    private final ChatUser user;

    //当前的channel
    private ChannelHandlerContext currentActiveContext;

    //心跳包
    private final Flappy.FlappyRequest heart;

    //回调
    private final NettyThreadListener deadCallback;

    //更新的sessions
    private final List<String> updatingIdLists = new ArrayList<>();

    //检查是否是active状态的
    public volatile boolean isActive = false;

    //秘钥
    private final String secret;

    //回调
    public ChannelMsgHandler(HandlerLogin handler, NettyThreadListener deadCallback, ChatUser user) {
        //心跳
        this.heart = Flappy.FlappyRequest.newBuilder().setType(FlappyRequest.REQ_PING).build();
        //handler
        this.handlerLogin = handler;
        //回调
        this.deadCallback = deadCallback;
        //用户数据
        this.user = user;
        //秘钥
        this.secret = IDGenerateTool.getRandomStr(16);
    }


    //三次握手成功,发送登录验证
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //发送登录验证
        sendLoginRequest(ctx);
        super.channelActive(ctx);
    }

    //断开连接
    public void channelInactive(ChannelHandlerContext context) throws Exception {
        closeChannel(context);
        super.channelInactive(context);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        sendHeartBeatRequest(ctx, evt);
        super.userEventTriggered(ctx, evt);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Flappy.FlappyResponse response) {
        //登录成功,现在才代表真正的登录成功
        if (response.getType() == FlappyResponse.RES_LOGIN) {
            receiveLogin(ctx, response);
        }
        //发送消息
        else if (response.getType() == FlappyResponse.RES_MSG) {
            receiveMessage(ctx, response);
        }
        //更新数据
        else if (response.getType() == FlappyResponse.RES_UPDATE) {
            receiveUpdate(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        closeChannel(ctx);
        super.exceptionCaught(ctx, cause);
    }

    /******
     * 用户下线
     * @param context 上下文
     */
    private void closeChannel(ChannelHandlerContext context) {

        //活跃状态设置为true
        isActive = false;

        //消息通道关闭，回调
        HandlerNotifyManager.getInstance().handleSendFailureAllCallback();

        //如果这里还没有回调成功，那么就是登录失败
        if (this.handlerLogin != null) {
            //创建消息
            Message message = new Message();
            //失败
            message.what = HandlerLogin.LOGIN_FAILURE;
            //错误
            message.obj = new Exception("channel closed");
            //发送消息
            this.handlerLogin.sendMessage(message);
            //清空引用
            this.handlerLogin = null;
        }
        //线程非正常退出
        if (deadCallback != null) {
            deadCallback.disconnected();
        }
        //关闭与服务器的连接
        context.close();
    }


    /******
     * 检查旧消息进行发送
     */
    private void checkFormerMessagesToSend() {
        List<ChatMessage> formerMessageList = HandlerNotifyManager.getInstance().getUnSendCallbackHandlers();
        for (ChatMessage message : formerMessageList) {
            sendMessageIfActive(message);
        }
    }


    /******
     * 检查会话是否需要更新
     * @param ctx ctx
     */
    private void checkSystemMessageFunction(ChannelHandlerContext ctx) {

        //获取所有数据
        List<ChatMessage> latestMessages = Database.getInstance().getNotActionSystemMessage();

        //区分更新,全量更新和只更新部分用户信息
        List<ChatMessage> actionUpdateSessionAll = new ArrayList<>();
        List<ChatMessage> actionUpdateSessionMember = new ArrayList<>();
        List<ChatMessage> actionDeleteSession = new ArrayList<>();
        for (ChatMessage item : latestMessages) {

            ChatSystem chatSystem = item.getChatSystem();
            //全量更新
            if (chatSystem.getSysAction() == ChatMessage.SYSTEM_MSG_NOTHING) {
                //保存消息状态数据
                item.setMessageReadState(new BigDecimal(1));
                Database.getInstance().insertMessage(item);
            }
            //全量更新
            if (chatSystem.getSysAction() == ChatMessage.SYSTEM_MSG_UPDATE_SESSION) {
                actionUpdateSessionAll.add(item);
            }
            //用户加入是自己也全量更新
            if (chatSystem.getSysAction() == ChatMessage.SYSTEM_MSG_ADD_MEMBER) {
                SessionMemberModel chatUser = GsonTool.jsonStrToModel(item.getChatSystem().getSysData(), SessionMemberModel.class);
                if (chatUser != null && chatUser.getUserId().equals(DataManager.getInstance().getLoginUser().getUserId())) {
                    actionUpdateSessionAll.add(item);
                } else {
                    actionUpdateSessionMember.add(item);
                }
            }
            //用户删除是自己删除会话
            if (chatSystem.getSysAction() == ChatMessage.SYSTEM_MSG_DELETE_MEMBER) {
                SessionMemberModel chatUser = GsonTool.jsonStrToModel(item.getChatSystem().getSysData(), SessionMemberModel.class);
                if (chatUser != null && chatUser.getUserId().equals(DataManager.getInstance().getLoginUser().getUserId())) {
                    actionDeleteSession.add(item);
                } else {
                    actionUpdateSessionMember.add(item);
                }
            }
            //用户加入/删除增量更新
            if (chatSystem.getSysAction() == ChatMessage.SYSTEM_MSG_UPDATE_MEMBER) {
                actionUpdateSessionMember.add(item);
            }
        }

        //全量更新
        if (!actionUpdateSessionAll.isEmpty()) {
            updateSessionAll(ctx, actionUpdateSessionAll);
        }
        //用户更新
        if (!actionUpdateSessionMember.isEmpty()) {
            updateSessionMemberUpdate(actionUpdateSessionMember);
        }
        //用户删除
        if (!actionUpdateSessionMember.isEmpty()) {
            updateSessionMemberDelete(actionDeleteSession);
        }
    }

    /******
     * 登录成功返回处理
     * @param ctx ctx
     * @param response 回复
     */
    private void receiveLogin(ChannelHandlerContext ctx, Flappy.FlappyResponse response) {

        //保存用户成功的登录信息
        synchronized (this) {

            //活跃状态设置为true
            isActive = true;

            //设置当前的context
            currentActiveContext = ctx;

            //设置登录成功，并保存
            user.setLogin(1);

            //保存数据
            DataManager.getInstance().saveLoginUser(user);

            //消息转换为我们的message
            List<ChatMessage> messages = new ArrayList<>();
            for (int s = 0; s < response.getMsgCount(); s++) {
                ChatMessage chatMessage = new ChatMessage(response.getMsgList().get(s), secret);
                messages.add(chatMessage);
            }

            //遍历消息进行通知
            if (handlerLogin.getLoginResponse().getSessions() != null &&
                    !handlerLogin.getLoginResponse().getSessions().isEmpty()) {
                //插入会话
                Database.getInstance().insertSessions(
                        handlerLogin.getLoginResponse().getSessions()
                );
                //通知会话更新
                HandlerNotifyManager.getInstance().notifySessionListUpdate(
                        handlerLogin.getLoginResponse().getSessions()
                );

                //系统消息、用户动作 消息设置为已处理
                for (ChatMessage msg : messages) {
                    if (msg.getMessageType().intValue() == MSG_TYPE_SYSTEM || msg.getMessageType().intValue() == MSG_TYPE_ACTION) {
                        msg.setMessageReadState(new BigDecimal(1));
                    }
                }
            }

            //对消息进行排序，然后在插入数据库
            Collections.sort(messages, (chatMessage, t1) -> {
                if (chatMessage.getMessageTableOffset().intValue() > t1.getMessageTableOffset().intValue()) {
                    return 1;
                } else if (chatMessage.getMessageTableOffset().intValue() < t1.getMessageTableOffset().intValue()) {
                    return -1;
                }
                return 0;
            });

            //处理消息
            for (int s = 0; s < messages.size(); s++) {
                ChatMessage chatMessage = messages.get(s);
                //通知接收成功或者发送成功
                ChatMessage former = Database.getInstance().getMessageById(chatMessage.getMessageId());
                //消息状态更改
                handleMessageSendArriveState(chatMessage, former);
                //插入消息
                Database.getInstance().insertMessage(chatMessage);
                //消息发送回调
                HandlerNotifyManager.getInstance().handleSendSuccessCallback(chatMessage);
                //通知监听变化
                HandlerNotifyManager.getInstance().handleMessageAction(chatMessage);
                //通知监听变化
                HandlerNotifyManager.getInstance().notifyMessageReceive(chatMessage, former);
                //保存最后的offset
                if (s == (messages.size() - 1)) {
                    messageArrivedReceipt(ctx, chatMessage, former);
                }
            }

            //登录成功
            handlerLogin.loginSuccess();

            //检查是否更新
            checkSystemMessageFunction(ctx);

            //如果说之前有消息不是在active状态发送的，那么链接成功后就触发发送
            checkFormerMessagesToSend();
        }
    }


    /******
     * 收到新的消息
     * @param ctx ctx
     * @param response 回复
     */
    private void receiveMessage(ChannelHandlerContext ctx, Flappy.FlappyResponse response) {
        //设置
        for (int s = 0; s < response.getMsgCount(); s++) {
            //得到真正的消息对象
            ChatMessage chatMessage = new ChatMessage(response.getMsgList().get(s), secret);
            //判断数据库是否存在
            ChatMessage former = Database.getInstance().getMessageById(chatMessage.getMessageId());
            //消息到达后的状态改变
            handleMessageSendArriveState(chatMessage, former);
            //插入消息
            Database.getInstance().insertMessage(chatMessage);
            //发送成功
            HandlerNotifyManager.getInstance().handleSendSuccessCallback(chatMessage);
            //新消息到达
            HandlerNotifyManager.getInstance().handleMessageAction(chatMessage);
            //新消息到达
            HandlerNotifyManager.getInstance().notifyMessageReceive(chatMessage, former);
            //消息回执
            messageArrivedReceipt(ctx, chatMessage, former);
        }
        //检查会话是否需要更新
        checkSystemMessageFunction(ctx);
    }

    /******
     * 收到会话更新的消息
     * @param response 回复
     */
    private void receiveUpdate(Flappy.FlappyResponse response) {
        //进行会话更新
        List<Flappy.Session> session = response.getSessionsList();
        //数据开始
        for (int s = 0; s < session.size(); s++) {
            //更新数据
            SessionModel data = new SessionModel(session.get(s));
            //插入数据
            Database.getInstance().insertSession(data);
            //通知session更新
            HandlerNotifyManager.getInstance().notifySessionUpdate(data);
            //消息标记为已经处理
            List<ChatMessage> messages = Database.getInstance().getNotActionSystemMessageBySessionId(data.getSessionId());
            //将系统消息标记成为已经处理，不再需要重复处理
            for (ChatMessage message : messages) {
                //更新消息
                if (data.getSessionStamp().longValue() >= StringTool.strToDecimal(message.getChatSystem().getSysTime()).longValue()) {
                    //设置阅读状态
                    message.setMessageReadState(new BigDecimal(1));
                    //插入消息
                    Database.getInstance().insertMessage(message);
                }
            }
            //移除正在更新
            updatingIdLists.remove(response.getUpdate().getUpdateID());
        }
    }

    //更新所有会话
    private void updateSessionAll(ChannelHandlerContext ctx, List<ChatMessage> messages) {
        //获取更新Session并去重,且判断正在进行中的更新
        List<String> updateIdList = new ArrayList<>();
        for (int s = 0; s < messages.size(); s++) {
            String updateId = messages.get(s).getMessageSessionId();
            if (!updateIdList.contains(updateId) &&
                    !updatingIdLists.contains(updateId)) {
                updateIdList.add(updateId);
            }
        }
        //进入正在更新列表
        updatingIdLists.addAll(updateIdList);
        //遍历发送请求消息
        for (String updateId : updateIdList) {
            //更新消息
            Flappy.ReqUpdate reqUpdate = Flappy.ReqUpdate.newBuilder()
                    .setUpdateID(updateId)
                    .setUpdateType(FlappyRequest.REQ_UPDATE_SESSION_ALL)
                    .build();
            //创建登录请求消息
            Flappy.FlappyRequest.Builder builder = Flappy.FlappyRequest.newBuilder()
                    .setUpdate(reqUpdate)
                    .setType(FlappyRequest.REQ_UPDATE);
            //发送需要更新的消息
            ctx.writeAndFlush(builder.build());
        }
    }

    //更新用户数据
    private void updateSessionMemberUpdate(List<ChatMessage> messages) {
        //遍历请求处理
        for (ChatMessage message : messages) {
            //数据进入数据库
            SessionMemberModel chatUser = GsonTool.jsonStrToModel(
                    message.getChatSystem().getSysData(),
                    SessionMemberModel.class
            );
            Database.getInstance().insertSessionMember(chatUser);

            //保存消息状态数据
            message.setMessageReadState(new BigDecimal(1));
            Database.getInstance().insertMessage(message);

            //通知会话更新了
            Message msg = new Message();
            msg.what = HandlerSession.SESSION_UPDATE;
            msg.obj = Database.getInstance().getUserSessionById(message.getMessageSessionId());
            HandlerNotifyManager.getInstance().getHandlerSession().sendMessage(msg);
        }
    }

    /******
     * 用户的会话被删除
     * @param messages  消息
     */
    private void updateSessionMemberDelete(List<ChatMessage> messages) {
        //遍历请求处理
        for (ChatMessage message : messages) {

            //获取会话信息
            SessionModel sessionModel = Database.getInstance().getUserSessionById(message.getMessageSessionId());
            sessionModel.setIsDelete(new BigDecimal(1));

            //保存消息状态数据
            message.setMessageReadState(new BigDecimal(1));
            Database.getInstance().insertMessage(message);

            //删除用户会话
            Database.getInstance().deleteUserSession(message.getMessageSessionId());

            //通知会话删除了
            Message msg = new Message();
            msg.what = HandlerSession.SESSION_DELETE;
            msg.obj = sessionModel;
            HandlerNotifyManager.getInstance().getHandlerSession().sendMessage(msg);
        }
    }


    /******
     * 修改收到的状态
     * @param msg    消息
     * @param former 之前的消息
     */
    private void handleMessageSendArriveState(ChatMessage msg, ChatMessage former) {
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        //如果是自己发送的，代表发送成功
        if (chatUser.getUserId().equals(msg.getMessageSendId())) {
            msg.setMessageSendState(new BigDecimal(ChatMessage.SEND_STATE_SENT));
        }
        //如果是别人发送的，代表到达目的设备
        else {
            msg.setMessageSendState(new BigDecimal(ChatMessage.SEND_STATE_REACHED));
        }
        //保留之前的已读状态
        if (former != null) {
            msg.setMessageReadState(former.getMessageReadState());
        }
    }

    /******
     * 消息已经到达
     * @param cxt  上下文
     * @param chatMessage 消息
     * @param former 之前存在的同一个消息
     */
    private void messageArrivedReceipt(ChannelHandlerContext cxt, ChatMessage chatMessage, ChatMessage former) {

        //保存最近一条的偏移量
        ChatUser user = DataManager.getInstance().getLoginUser();
        //最近一条为空
        if (user.getLatest() == null) {
            user.setLatest(StringTool.decimalToStr(chatMessage.getMessageTableOffset()));
        }
        //设置最大的那个值
        else {
            user.setLatest(Long.toString(Math.max(chatMessage.getMessageTableOffset().longValue(), StringTool.strToLong(user.getLatest()))));
        }
        //保存用户信息
        DataManager.getInstance().saveLoginUser(user);

        //不是自己，而且确实是最新的消息
        if (!chatMessage.getMessageSendId().equals(DataManager.getInstance().getLoginUser().getUserId()) && former == null) {

            //创建消息到达的回执
            Flappy.ReqReceipt receipt = Flappy.ReqReceipt.newBuilder()
                    .setReceiptID(chatMessage.getMessageTableOffset().toString())
                    .setReceiptType(FlappyRequest.RECEIPT_MSG_ARRIVE)
                    .build();

            //创建回执消息
            Flappy.FlappyRequest.Builder builder = Flappy.FlappyRequest.newBuilder()
                    .setReceipt(receipt)
                    .setType(FlappyRequest.REQ_RECEIPT);

            //发送回执，发送回执后，所有之前的消息都会被列为已经收到，因为端口是阻塞的
            cxt.writeAndFlush(builder.build());
        }
    }


    /******
     * 发送心跳并检查心跳
     * @param ctx ctx
     * @param evt evt
     */
    private void sendHeartBeatRequest(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            //发送心跳包
            ctx.writeAndFlush(heart).addListeners((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    NettyAttrTool.updateReaderTime(ctx.channel(), System.currentTimeMillis());
                }
            });
            //检查心跳，出现异常关闭socket
            Long formerTime = NettyAttrTool.getReaderTime(ctx.channel());
            if (formerTime != null && System.currentTimeMillis() - formerTime > 30 * 1000) {
                closeChannel(ctx);
            }
        }
    }

    /******
     * 发送登录请求
     * @param ctx ctx
     */
    private void sendLoginRequest(ChannelHandlerContext ctx) throws Exception {
        //创建builder
        Flappy.ReqLogin.Builder loginInfoBuilder = Flappy.ReqLogin.newBuilder()
                .setUserID(this.user.getUserId())
                .setDevicePlat(FlappyConfig.getInstance().devicePlat)
                .setDeviceId(DataManager.getInstance().getDeviceId());

        //如果是空
        if (StringTool.isEmpty(DataManager.getInstance().getRSAKey())) {
            //设置secret
            loginInfoBuilder.setSecret(this.secret);
        } else {
            //设置secret RSA
            loginInfoBuilder.setSecret(RSATool.encryptWithPublicKey(
                    DataManager.getInstance().getRSAKey(),
                    this.secret
            ));
        }

        //设置最近的消息偏移量作为请求消息数据
        ChatUser user = DataManager.getInstance().getLoginUser();
        if (user != null && user.getLatest() != null) {
            loginInfoBuilder.setLatest(user.getLatest());
        }

        //创建登录请求消息
        Flappy.FlappyRequest.Builder builder = Flappy.FlappyRequest.newBuilder()
                .setLogin(loginInfoBuilder.build())
                .setType(FlappyRequest.REQ_LOGIN);

        //发送登录消息
        ctx.writeAndFlush(builder.build());
    }


    /******
     * 发送消息
     * @param chatMessage  消息
     * @param callback     发送消息的回调
     */
    public void sendMessage(final ChatMessage chatMessage, final FlappySendCallback<ChatMessage> callback) {

        //多次发送，之前的发送全部直接给他失败
        HandlerNotifyManager.getInstance().handleSendFailureCallback(
                chatMessage,
                new Exception("消息重新发送")
        );

        //添加进入到消息发送请求
        HandlerNotifyManager.getInstance().addSendCallbackHandler(
                chatMessage.getMessageId(),
                new HandlerSendCall(callback, chatMessage)
        );

        //如果当前的Handler处于存货状态，则进行发送
        this.sendMessageIfActive(chatMessage);
    }

    /******
     * 发送消息
     * @param chatMessage 消息
     */
    public void sendMessageIfActive(final ChatMessage chatMessage) {
        synchronized (this) {
            try {
                if (isActive) {
                    //消息创建
                    Flappy.Message message = chatMessage.toProtocMessage(
                            Flappy.Message.newBuilder(),
                            secret
                    );
                    Flappy.FlappyRequest.Builder builder = Flappy.FlappyRequest.newBuilder()
                            .setMsg(message)
                            .setType(FlappyRequest.REQ_MSG);
                    ChannelFuture future = currentActiveContext.writeAndFlush(builder.build());
                    future.addListener((ChannelFutureListener) channelFuture -> {
                        if (!channelFuture.isSuccess()) {
                            HandlerNotifyManager.getInstance().handleSendFailureCallback(
                                    chatMessage,
                                    new Exception("连接已经断开")
                            );
                        }
                    });
                }
            } catch (Exception ex) {
                HandlerNotifyManager.getInstance().handleSendFailureCallback(chatMessage, ex);
            }
        }
    }
}