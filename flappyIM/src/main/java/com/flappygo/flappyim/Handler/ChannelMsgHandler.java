package com.flappygo.flappyim.Handler;

import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_ACTION;
import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_SYSTEM;

import com.flappygo.flappyim.Models.Response.Base.FlappyResponse;
import com.flappygo.flappyim.DataBase.Models.ChatSessionMember;
import com.flappygo.flappyim.Models.Request.Base.FlappyRequest;
import com.flappygo.flappyim.DataBase.Models.ChatSessionData;
import com.flappygo.flappyim.Tools.Generate.IDGenerateTool;
import com.flappygo.flappyim.Callback.FlappySendCallback;
import com.flappygo.flappyim.Thread.NettyThreadListener;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Models.Request.ChatSystem;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Models.Server.ChatUser;

import io.netty.channel.SimpleChannelInboundHandler;

import com.flappygo.flappyim.Tools.Secret.RSATool;
import com.flappygo.flappyim.Models.Protoc.Flappy;
import com.flappygo.flappyim.Tools.NettyAttrTool;
import com.flappygo.flappyim.DataBase.Database;
import com.flappygo.flappyim.Datas.DataManager;

import io.netty.handler.timeout.IdleStateEvent;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import com.flappygo.flappyim.Tools.StringTool;
import com.flappygo.flappyim.FlappyImService;

import io.netty.channel.ChannelFuture;

import java.util.ArrayList;

import android.os.Message;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

    //通道秘钥
    private final String channelSecret;

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
        this.channelSecret = IDGenerateTool.getRandomStr(16);
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
        //被踢下线
        else if (response.getType() == FlappyResponse.RES_KICKED) {
            receiveKicked();
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
    private void checkCachedMessagesToSend() {
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
        List<ChatMessage> actionUpdateSessionMemberDelete = new ArrayList<>();

        //遍历消息处理
        for (ChatMessage item : latestMessages) {
            ChatSystem chatSystem = item.getChatSystem();
            //全量更新
            if (chatSystem.getSysAction() == ChatMessage.SYSTEM_MSG_NOTHING) {
                //保存消息状态数据
                item.setMessageReadState(1);
                Database.getInstance().insertMessage(item);
            }
            //全量更新
            if (chatSystem.getSysAction() == ChatMessage.SYSTEM_MSG_UPDATE_SESSION) {
                actionUpdateSessionAll.add(item);
            }
            //用户加入是自己也全量更新
            if (chatSystem.getSysAction() == ChatMessage.SYSTEM_MSG_ADD_MEMBER) {
                ChatSessionMember chatUser = GsonTool.jsonStrToModel(item.getChatSystem().getSysData(), ChatSessionMember.class);
                if (chatUser != null && chatUser.getUserId().equals(DataManager.getInstance().getLoginUser().getUserId())) {
                    actionUpdateSessionAll.add(item);
                } else {
                    actionUpdateSessionMember.add(item);
                }
            }
            //用户删除是自己删除会话
            if (chatSystem.getSysAction() == ChatMessage.SYSTEM_MSG_DELETE_MEMBER) {
                ChatSessionMember chatUser = GsonTool.jsonStrToModel(item.getChatSystem().getSysData(), ChatSessionMember.class);
                if (chatUser != null && chatUser.getUserId().equals(DataManager.getInstance().getLoginUser().getUserId())) {
                    actionUpdateSessionMemberDelete.add(item);
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
        if (!actionUpdateSessionMemberDelete.isEmpty()) {
            updateSessionDeleteSelf(actionUpdateSessionMemberDelete);
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

            //设置当前的context
            currentActiveContext = ctx;

            //活跃状态设置为true
            isActive = true;

            //设置登录成功，并保存
            user.setLogin(1);

            //保存数据
            DataManager.getInstance().saveLoginUser(user);

            //消息转换为我们的消息体(解码)，并进行排序
            List<ChatMessage> receiveMessageList = new ArrayList<>();
            for (int s = 0; s < response.getMsgCount(); s++) {
                ChatMessage chatMessage = new ChatMessage(response.getMsgList().get(s), channelSecret);
                receiveMessageList.add(chatMessage);
            }
            receiveMessageList.sort((chatMessage, t1) -> {
                if (chatMessage.getMessageTableOffset().intValue() > t1.getMessageTableOffset().intValue()) {
                    return 1;
                } else if (chatMessage.getMessageTableOffset().intValue() < t1.getMessageTableOffset().intValue()) {
                    return -1;
                }
                return 0;
            });

            //获取登录同步的消息列表
            List<Flappy.Session> session = response.getSessionsList();

            //遍历消息进行处理
            if (!session.isEmpty()) {

                //会话进行转换
                List<ChatSessionData> chatSessionDataList = new ArrayList<>();
                for (Flappy.Session item : session) {
                    chatSessionDataList.add(new ChatSessionData(item));
                }

                //插入会话
                Database.getInstance().insertSessions(
                        chatSessionDataList
                );

                //通知会话更新
                HandlerNotifyManager.getInstance().notifySessionReceiveList(
                        chatSessionDataList
                );

                //存在会话更新的情况下，该会话下的所有执行类消息默认设置未已读(已处理模式)
                for (ChatSessionData data : chatSessionDataList) {
                    for (ChatMessage msg : receiveMessageList) {
                        if ((msg.getMessageType() == MSG_TYPE_SYSTEM ||
                                msg.getMessageType() == MSG_TYPE_ACTION) &&
                                data.getSessionId().equals(msg.getMessageSessionId())) {
                            msg.setMessageReadState(1);
                        }
                    }
                }
            }

            //因为消息更新了，所以会话的最后一条消息更新了，这里需要收集处理一下
            Set<String> notifySessionIdList = new HashSet<>();

            //登录的时候插入的消息必然是新收到的消息
            for (int s = 0; s < receiveMessageList.size(); s++) {
                ChatMessage chatMessage = receiveMessageList.get(s);
                //消息状态更改
                handleMessageSendReadState(chatMessage);
                //插入消息
                Database.getInstance().insertMessage(chatMessage);
                //通知监听变化
                HandlerNotifyManager.getInstance().handleMessageAction(chatMessage);
                //消息发送回调
                HandlerNotifyManager.getInstance().handleSendSuccessCallback(chatMessage);
                //更新了的会话ID
                notifySessionIdList.add(chatMessage.getMessageSessionId());
            }

            //更新了的列表
            List<ChatSessionData> updatedSessionList = new ArrayList<>();
            for (String id : notifySessionIdList) {
                ChatSessionData item = Database.getInstance().getUserSessionById(id);
                if (item != null) {
                    updatedSessionList.add(Database.getInstance().getUserSessionById(id));
                }
            }

            //会话监听变化
            HandlerNotifyManager.getInstance().notifySessionUpdateList(updatedSessionList);

            //消息监听变化
            HandlerNotifyManager.getInstance().notifyMessageListReceive(receiveMessageList);

            //消息到达回执
            messageArrivedReceipt(ctx, receiveMessageList);

            //登录成功
            handlerLogin.loginSuccess();

            //检查是否更新
            checkSystemMessageFunction(ctx);

            //如果说之前有消息不是在active状态发送的，那么链接成功后就触发发送
            checkCachedMessagesToSend();
        }
    }


    /******
     * 收到新的消息
     * @param ctx ctx
     * @param response 回复
     */
    private void receiveMessage(ChannelHandlerContext ctx, Flappy.FlappyResponse response) {
        //设置
        List<ChatMessage> receiveMessageList = new ArrayList<>();
        for (int s = 0; s < response.getMsgCount(); s++) {
            //得到真正的消息对象
            ChatMessage chatMessage = new ChatMessage(response.getMsgList().get(s), channelSecret);
            //消息到达后的状态改变
            handleMessageSendReadState(chatMessage);
            //插入消息
            Database.getInstance().insertMessage(chatMessage);
            //新消息到达
            HandlerNotifyManager.getInstance().handleMessageAction(chatMessage);
            //发送成功
            HandlerNotifyManager.getInstance().handleSendSuccessCallback(chatMessage);
            //会话监听变化
            HandlerNotifyManager.getInstance().notifySessionUpdate(
                    Database.getInstance().getUserSessionById(chatMessage.getMessageSessionId())
            );
            //新消息到达
            HandlerNotifyManager.getInstance().notifyMessageReceive(chatMessage);
            //消息回执
            receiveMessageList.add(chatMessage);
        }
        //消息到达回执
        messageArrivedReceipt(ctx, receiveMessageList);
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
            ChatSessionData data = new ChatSessionData(session.get(s));
            //插入数据
            Database.getInstance().insertSession(data);
            //通知session更新
            HandlerNotifyManager.getInstance().notifySessionReceive(data);
            //消息标记为已经处理
            List<ChatMessage> messages = Database.getInstance().getNotActionSystemMessageBySessionId(data.getSessionId());
            //将系统消息标记成为已经处理，不再需要重复处理
            for (ChatMessage message : messages) {
                //更新消息
                if (data.getSessionStamp() >= StringTool.strToDecimal(message.getChatSystem().getSysTime()).longValue()) {
                    //设置阅读状态
                    message.setMessageReadState(1);
                    //插入消息
                    Database.getInstance().insertMessage(message);
                }
            }
            //移除正在更新
            updatingIdLists.remove(response.getUpdate().getResponseID());
        }
    }

    /******
     * 收到被踢下线的回复消息
     */
    private void receiveKicked() {
        FlappyImService.getInstance().sendKickedOutEvent();
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

    /******
     * 更新用户数据
     * @param messages 消息数据
     */
    private void updateSessionMemberUpdate(List<ChatMessage> messages) {
        //遍历请求处理
        for (ChatMessage message : messages) {
            //数据进入数据库
            ChatSessionMember chatUser = GsonTool.jsonStrToModel(
                    message.getChatSystem().getSysData(),
                    ChatSessionMember.class
            );
            Database.getInstance().insertSessionMember(chatUser);

            //保存消息状态数据
            message.setMessageReadState(1);
            Database.getInstance().insertMessage(message);

            //通知会话更新了
            ChatSessionData sessionModel = Database.getInstance().getUserSessionById(message.getMessageSessionId());
            HandlerNotifyManager.getInstance().notifySessionReceive(sessionModel);
        }
    }

    /******
     * 用户的会话被删除
     * @param messages  消息
     */
    private void updateSessionDeleteSelf(List<ChatMessage> messages) {
        //遍历请求处理
        for (ChatMessage message : messages) {

            //数据进入数据库
            ChatSessionMember chatUser = GsonTool.jsonStrToModel(
                    message.getChatSystem().getSysData(),
                    ChatSessionMember.class
            );
            Database.getInstance().insertSessionMember(chatUser);

            //保存消息状态数据
            message.setMessageReadState(1);
            Database.getInstance().insertMessage(message);

            //删除会话
            Database.getInstance().deleteUserSession(message.getMessageSessionId());

            //获取会话信息
            ChatSessionData sessionModel = Database.getInstance().getUserSessionById(message.getMessageSessionId());
            sessionModel.setIsDelete(1);
            HandlerNotifyManager.getInstance().notifySessionDelete(sessionModel);
        }
    }


    /******
     * 修改收到的状态
     * @param msg    消息
     */
    private void handleMessageSendReadState(ChatMessage msg) {
        //通知接收成功或者发送成功
        ChatMessage former = Database.getInstance().getMessageById(msg.getMessageId());
        //当前用户
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        //如果是自己发送的，代表发送成功
        if (chatUser.getUserId().equals(msg.getMessageSendId())) {
            msg.setMessageSendState(ChatMessage.SEND_STATE_SENT);
        }
        //如果是别人发送的，代表到达目的设备
        else {
            msg.setMessageSendState(ChatMessage.SEND_STATE_REACHED);
        }
        // 这里
        // 如果是普通消息，那么保留之前的已读状态，
        // 如果是action消息的话之前必然是处理过的或者没有正常插入的
        msg.setMessageReadState((former != null) ? former.getMessageReadState() : msg.getMessageReadState());
    }

    /******
     * 消息已经到达
     * @param cxt  上下文
     * @param messageList 消息
     */
    private void messageArrivedReceipt(ChannelHandlerContext cxt, List<ChatMessage> messageList) {

        //前面已经排序了，这里获取最后一条消息
        ChatMessage chatMessage;
        if (!messageList.isEmpty()) {
            chatMessage = messageList.get(messageList.size() - 1);
        } else {
            return;
        }

        //获取保存的最近一条的偏移量
        ChatUser user = DataManager.getInstance().getLoginUser();

        //检查这个消息是不是最新的
        boolean isLatest;

        //最近一条为空
        if (user.getLatest() == null) {
            user.setLatest(chatMessage.getMessageTableOffset().toString());
            isLatest = true;
        }
        //设置最大的那个值
        else {
            long valueNewer = chatMessage.getMessageTableOffset();
            long valueFormer = StringTool.strToLong(user.getLatest());
            long maxValue = Math.max(valueNewer, valueFormer);
            user.setLatest(Long.toString(maxValue));
            isLatest = (valueNewer > valueFormer);
        }
        //保存用户信息
        DataManager.getInstance().saveLoginUser(user);

        //不是自己，而且确实是最新的消息
        if (!chatMessage.getMessageSendId().equals(user.getUserId()) && isLatest) {

            //创建消息到达的回执
            Flappy.ReqReceipt receipt = Flappy.ReqReceipt.newBuilder()
                    .setReceiptID(chatMessage.getMessageTableOffset().toString())
                    .setReceiptType(FlappyRequest.RECEIPT_MSG_ARRIVE)
                    .build();

            //创建回执消息
            Flappy.FlappyRequest.Builder builder = Flappy.FlappyRequest.newBuilder()
                    .setReceipt(receipt)
                    .setType(FlappyRequest.REQ_RECEIPT);

            //发送回执，发送回执后，所有之前的消息都会被列为已经收到
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
                .setUserID(user.getUserId())
                .setDevicePlat(DataManager.getInstance().getDevicePlat())
                .setDeviceId(DataManager.getInstance().getDeviceId());

        //如果是空
        if (StringTool.isEmpty(DataManager.getInstance().getRSAPublicKey())) {
            loginInfoBuilder.setSecret(channelSecret);
        } else {
            loginInfoBuilder.setSecret(RSATool.encryptWithPublicKey(
                    DataManager.getInstance().getRSAPublicKey(),
                    channelSecret
            ));
        }

        //设置最近的消息偏移量作为请求消息数据
        //(注意一下，如果是登录，这里必然是没有值的，如果是自动登录，这里的值已经在前面的流程中将latest赋值了)
        if(!StringTool.isEmpty(user.getLatest())){
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
                            channelSecret
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