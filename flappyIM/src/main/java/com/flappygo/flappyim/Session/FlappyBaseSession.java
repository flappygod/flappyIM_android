package com.flappygo.flappyim.Session;

import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import com.flappygo.flappyim.ApiServer.Models.BaseApiModel;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Callback.FlappyIMCallback;
import com.flappygo.flappyim.Callback.FlappySendCallback;
import com.flappygo.flappyim.Config.BaseConfig;
import com.flappygo.flappyim.DataBase.Database;
import com.flappygo.flappyim.Datas.DataManager;
import com.flappygo.flappyim.Handler.ChannelMsgHandler;
import com.flappygo.flappyim.Models.Request.ChatImage;
import com.flappygo.flappyim.Models.Request.ChatVoice;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Service.FlappyService;
import com.flappygo.flappyim.Thread.NettyThread;
import com.flappygo.flappyim.Tools.StringTool;
import com.flappygo.flappyim.Tools.Upload.LXImageWH;
import com.flappygo.flappyim.Tools.Upload.UploadTool;
import com.flappygo.lilin.lxhttpclient.Asynctask.LXAsyncTask;
import com.flappygo.lilin.lxhttpclient.Asynctask.LXAsyncTaskClient;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;

import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_NETERROR;
import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_SUCCESS;
import static com.flappygo.flappyim.Models.Server.ChatMessage.SEND_STATE_CREATE;
import static com.flappygo.flappyim.Models.Server.ChatMessage.SEND_STATE_FAILURE;
import static com.flappygo.flappyim.Models.Server.ChatMessage.SEND_STATE_SENDED;

public class FlappyBaseSession {


    //我们姑且认为是最后一条
    protected void insertMessage(ChatMessage msg) {
        //已经发送了
        msg.setMessageSended(new BigDecimal(SEND_STATE_CREATE));
        //当前最后一条
        BigDecimal bigDecimal = StringTool.strToDecimal(DataManager.getInstance().getLoginUser().getLatest());
        //+1
        bigDecimal = bigDecimal.add(new BigDecimal(1));
        //放到最后
        msg.setMessageTableSeq(bigDecimal);
        //插入数据
        Database.getInstance().insertMessage(msg);
    }

    //将消息的状态更新为已经发送
    private void updateMsgSended(ChatMessage msg) {
        //已经发送了
        msg.setMessageSended(new BigDecimal(SEND_STATE_SENDED));
        //插入数据
        Database.getInstance().insertMessage(msg);
    }


    //发送失败了更新数据
    private void updateMsgFailure(ChatMessage msg) {
        //已经发送了
        msg.setMessageSended(new BigDecimal(SEND_STATE_FAILURE));
        //插入数据
        Database.getInstance().insertMessage(msg);
    }

    //发送消息
    protected void sendMessage(final ChatMessage chatMessage, final FlappySendCallback<ChatMessage> callback) {

        FlappyService flappyService = FlappyService.getInstance();

        //如果当前服务不在线，错误
        if (flappyService == null) {
            updateMsgFailure(chatMessage);
            callback.failure(chatMessage, new Exception("服务已停止"), Integer.parseInt(RESULT_NETERROR));
            return;
        }

        //服务中的线程没有运行
        NettyThread thread = flappyService.getClientThread();
        if (thread == null) {
            updateMsgFailure(chatMessage);
            callback.failure(chatMessage, new Exception("线程已停止"), Integer.parseInt(RESULT_NETERROR));
            return;
        }

        //线程中的handler不存在
        ChannelMsgHandler handler = thread.getChannelMsgHandler();
        if (handler == null) {
            updateMsgFailure(chatMessage);
            callback.failure(chatMessage, new Exception("Handler不存在"), Integer.parseInt(RESULT_NETERROR));
            return;
        }

        //取得了handler,再发送消息
        handler.sendMessage(chatMessage, new FlappyIMCallback<String>() {
            @Override
            public void success(String data) {
                updateMsgSended(chatMessage);
                callback.success(chatMessage);
            }

            @Override
            public void failure(Exception ex, int code) {
                updateMsgFailure(chatMessage);
                callback.failure(chatMessage, ex, code);
            }
        });
    }

    //上传音频文件并发送
    protected void uploadVoiceAndSend(final ChatMessage msg, final FlappySendCallback callback) {
        //client
        LXAsyncTaskClient client = new LXAsyncTaskClient(1);
        //发送
        client.excute(new LXAsyncTask() {
            @Override
            public Object run(Object data, String s) throws Exception {

                //获取数据
                ChatMessage message = (ChatMessage) data;
                //转换出image对象
                ChatVoice voice = GsonTool.jsonObjectToModel(message.getMessageContent(), ChatVoice.class);

                //构建文件参数
                HashMap<String, Object> parmap = new HashMap<String, Object>();
                //构建文件参数
                HashMap<String, String> fileMap = new HashMap<String, String>();
                //地址
                fileMap.put("file", voice.getSendPath());
                //返回的字符串
                String str = UploadTool.postFile(BaseConfig.getInstance().uploadUrl, parmap, fileMap);
                //返回数据
                BaseApiModel<String> baseApiModel = new BaseApiModel<String>();
                //创建
                JSONObject jb = new JSONObject(str);
                //返回码
                baseApiModel.setResultCode(jb.optString("resultCode"));
                //解析code
                baseApiModel.setResultMessage(jb.optString("resultMessage"));
                //返回的消息
                baseApiModel.setResultSign(jb.optString("resultSign"));
                //返回的总页码
                baseApiModel.setResultTotalPage(jb.optInt("resultTotalPage"));
                //设置返回的数据
                baseApiModel.setResultData(jb.optString("resultData"));
                //上传不成功抛出异常
                if (!baseApiModel.getResultCode().equals(RESULT_SUCCESS)) {
                    throw new Exception(baseApiModel.getResultMessage());
                }
                //设置数据返回
                voice.setPath(baseApiModel.getResultData());
                //长度
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                //在获取前，设置文件路径（应该只能是本地路径）
                retriever.setDataSource(voice.getSendPath());
                //长度
                String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                //释放
                retriever.release();
                //发送哦
                if (!TextUtils.isEmpty(duration)) {
                    //设置宽度
                    voice.setSeconds(duration);
                }
                //更新content
                message.setMessageContent(GsonTool.modelToString(voice, ChatVoice.class));
                //消息
                return message;
            }

            @Override
            public void failure(Exception e, String s) {
                updateMsgFailure(msg);
                callback.failure(msg, e, Integer.parseInt(RESULT_NETERROR));
            }

            @Override
            public void success(Object msg, String s) {
                //得到上传后的消息实体
                ChatMessage message = (ChatMessage) msg;
                //设置真实的信息
                sendMessage(message, callback);
            }
        }, msg, null);
    }

    //上传图片并发送
    protected void uploadImageAndSend(final ChatMessage msg, final FlappySendCallback callback) {
        //client
        LXAsyncTaskClient client = new LXAsyncTaskClient(1);
        //发送
        client.excute(new LXAsyncTask() {
            @Override
            public Object run(Object data, String s) throws Exception {
                //取得消息体
                ChatMessage message = (ChatMessage) data;
                //取得图片信息
                ChatImage image = GsonTool.jsonObjectToModel(message.getMessageContent(), ChatImage.class);

                //构建文件参数
                HashMap<String, Object> parmap = new HashMap<String, Object>();
                //构建文件参数
                HashMap<String, String> fileMap = new HashMap<String, String>();
                //地址
                fileMap.put("file", image.getSendPath());
                //保存宽高
                LXImageWH lxImageWH = new LXImageWH();
                //返回的字符串
                String str = UploadTool.postImage(BaseConfig.getInstance().uploadUrl, parmap, fileMap, lxImageWH);
                //返回数据
                BaseApiModel<String> baseApiModel = new BaseApiModel<String>();
                //创建
                JSONObject jb = new JSONObject((String) str);
                //返回码
                baseApiModel.setResultCode(jb.optString("resultCode"));
                //解析code
                baseApiModel.setResultMessage(jb.optString("resultMessage"));
                //返回的消息
                baseApiModel.setResultSign(jb.optString("resultSign"));
                //返回的总页码
                baseApiModel.setResultTotalPage(jb.optInt("resultTotalPage"));
                //设置返回的数据
                baseApiModel.setResultData(jb.optString("resultData"));
                //上传不成功抛出异常
                if (!baseApiModel.getResultCode().equals(RESULT_SUCCESS)) {
                    throw new Exception(baseApiModel.getResultMessage());
                }
                //设置宽度
                image.setWidth(lxImageWH.getWidth() + "");
                //设置高度
                image.setHeight(lxImageWH.getHeight() + "");
                //设置数据返回
                image.setPath(baseApiModel.getResultData());
                //转换为content
                message.setMessageContent(GsonTool.modelToString(image, ChatImage.class));

                return message;
            }

            @Override
            public void failure(Exception e, String s) {
                updateMsgFailure(msg);
                callback.failure(msg, e, StringTool.strToDecimal(s).intValue());
            }

            @Override
            public void success(Object msg, String s) {
                //消息
                ChatMessage message = (ChatMessage) msg;
                //发送消息
                sendMessage(message, callback);
            }
        }, msg, null);
    }


}
