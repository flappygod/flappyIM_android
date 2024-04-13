package com.flappygo.flappyim.Session;

import static com.flappygo.flappyim.Models.Server.ChatMessage.SEND_STATE_CREATE;
import static com.flappygo.flappyim.Models.Server.ChatMessage.SEND_STATE_FAILURE;
import static com.flappygo.flappyim.Models.Server.ChatMessage.SEND_STATE_SENT;
import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_NET_ERROR;
import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_SUCCESS;

import com.flappygo.flappyim.ApiServer.Clients.AsyncTask.LXAsyncTaskClient;
import com.flappygo.flappyim.ApiServer.Clients.AsyncTask.LXAsyncTask;
import com.flappygo.flappyim.Models.Response.ResponseUpload;
import com.flappygo.flappyim.ApiServer.Models.BaseApiModel;
import com.flappygo.flappyim.Handler.MessageNotifyManager;
import com.flappygo.flappyim.Callback.FlappySendCallback;
import com.flappygo.flappyim.Service.FlappySocketService;
import com.flappygo.flappyim.Handler.ChannelMsgHandler;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Tools.Upload.UploadModel;
import com.flappygo.flappyim.Models.Request.ChatImage;
import com.flappygo.flappyim.Models.Request.ChatVideo;
import com.flappygo.flappyim.Models.Request.ChatVoice;
import com.flappygo.flappyim.Models.Request.ChatFile;
import com.flappygo.flappyim.Tools.Upload.UploadTool;
import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.flappyim.Config.FlappyConfig;
import com.flappygo.flappyim.Thread.NettyThread;
import com.flappygo.flappyim.DataBase.Database;
import com.flappygo.flappyim.Datas.DataManager;
import com.flappygo.flappyim.Tools.StringTool;

import java.math.BigDecimal;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


/******
 * 基础会话方法
 */
public class FlappyBaseSession {

    //Client
    private static final LXAsyncTaskClient<ChatMessage, ChatMessage> client = new LXAsyncTaskClient<ChatMessage, ChatMessage>(10);

    ///获取当前的消息handler
    ChannelMsgHandler getCurrentChannelMessageHandler() {
        FlappySocketService flappyService = FlappySocketService.getInstance();
        //如果当前服务不在线，错误
        if (flappyService == null) {
            return null;
        }
        //服务中的线程没有运行(按照道理这里不太可能，因为app在初始化启动的时候就已经开启线程去联网了，不过仍然会有一个小的时间差)
        NettyThread thread = flappyService.getClientThread();
        if (thread == null) {
            return null;
        }
        //线程中的handler不存在
        return thread.getChannelMsgHandler();
    }

    //我们姑且认为是最后一条
    public void insertMessage(ChatMessage msg) {
        //设置状态
        msg.setMessageSendState(new BigDecimal(SEND_STATE_CREATE));
        //设置消息的TableSeq,这里这个值并不是最终值，最终以服务端成功后的返回值为准
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        BigDecimal bigDecimal = StringTool.strToDecimal(chatUser.getLatest());
        bigDecimal = bigDecimal.add(new BigDecimal(1));
        msg.setMessageTableSeq(bigDecimal);
        //插入消息
        Database database = Database.getInstance().open();
        database.insertMessage(msg);
        database.close();
        //通知消息发送成功
        MessageNotifyManager.getInstance().notifyMessageSend(msg);
    }

    //发送失败了更新数据
    private void updateMsgFailure(ChatMessage msg) {
        msg.setMessageSendState(new BigDecimal(SEND_STATE_FAILURE));
        Database database = Database.getInstance().open();
        database.insertMessage(msg);
        database.close();
        //通知消息发送失败
        MessageNotifyManager.getInstance().notifyMessageFailure(msg);
    }

    //将消息的状态更新为已经发送
    private void updateMsgSent(ChatMessage msg) {
        msg.setMessageSendState(new BigDecimal(SEND_STATE_SENT));
        Database database = Database.getInstance().open();
        database.insertMessage(msg);
        database.close();
    }

    //发送消息
    protected void sendMessage(ChatMessage chatMessage, final FlappySendCallback<ChatMessage> callback) {
        //线程中的handler不存在
        ChannelMsgHandler handler = getCurrentChannelMessageHandler();
        if (handler == null) {
            updateMsgFailure(chatMessage);
            callback.failure(chatMessage, new Exception("Channel error"), Integer.parseInt(RESULT_NET_ERROR));
            return;
        }
        //取得了handler,再发送消息
        handler.sendMessage(chatMessage, new FlappySendCallback<ChatMessage>() {
            @Override
            public void success(ChatMessage msg) {
                updateMsgSent(msg);
                callback.success(msg);
            }

            @Override
            public void failure(ChatMessage msg, Exception ex, int code) {
                updateMsgFailure(msg);
                callback.failure(msg, ex, code);
            }
        });
    }

    //上传图片并发送
    protected void uploadImageAndSend(final ChatMessage msg, final FlappySendCallback<ChatMessage> callback) {
        //发送
        client.execute(new LXAsyncTask<ChatMessage, ChatMessage>() {
            @Override
            public ChatMessage run(ChatMessage data, String s) throws Exception {
                //取得消息体
                ChatImage image = data.getChatImage();
                //构建文件参数
                HashMap<String, Object> paramMap = new HashMap<>();
                //上传参数
                UploadModel uploadModel = new UploadModel();
                //参数名称
                uploadModel.setName("file");
                //上传本地文件地址
                uploadModel.setPath(image.getSendPath());
                //创建列表
                ArrayList<UploadModel> files = new ArrayList<>();
                //添加
                files.add(uploadModel);
                //返回的字符串
                String str = UploadTool.postFile(FlappyConfig.getInstance().fileUpload(), paramMap, files);
                //返回数据
                BaseApiModel<String> baseApiModel = new BaseApiModel<>();
                //创建
                JSONObject jb = new JSONObject(str);
                //返回码
                baseApiModel.setCode(jb.optString("code"));
                //解析code
                baseApiModel.setMsg(jb.optString("msg"));
                //返回的总页码
                baseApiModel.setPageCount(jb.optInt("pageCount"));
                //设置返回的数据
                if (jb.optJSONArray("data") != null && Objects.requireNonNull(jb.optJSONArray("data")).length() > 0) {
                    baseApiModel.setData(Objects.requireNonNull(jb.optJSONArray("data")).getString(0));
                }
                //上传不成功抛出异常
                if (!baseApiModel.getCode().equals(RESULT_SUCCESS)) {
                    throw new Exception(baseApiModel.getMsg());
                }
                //设置数据返回
                image.setPath(baseApiModel.getData());
                //转换为content
                data.setChatImage(image);
                return data;
            }

            @Override
            public void failure(Exception e, String s) {
                updateMsgFailure(msg);
                callback.failure(msg, e, StringTool.strToDecimal(s).intValue());
            }

            @Override
            public void success(ChatMessage msg, String s) {
                updateMsgSent(msg);
                sendMessage(msg, callback);
            }
        }, msg, null);
    }


    //上传音频文件并发送
    protected void uploadVoiceAndSend(final ChatMessage msg, final FlappySendCallback<ChatMessage> callback) {
        //发送
        client.execute(new LXAsyncTask<ChatMessage, ChatMessage>() {
            @Override
            public ChatMessage run(ChatMessage data, String s) throws Exception {

                //转换出image对象
                ChatVoice voice = data.getChatVoice();

                //构建文件参数
                HashMap<String, Object> getParamMap = new HashMap<>();
                //构建文件参数
                UploadModel uploadModel = new UploadModel();
                //文件名称
                uploadModel.setName("file");
                //设置地址
                uploadModel.setPath(voice.getSendPath());
                //文件列表
                ArrayList<UploadModel> files = new ArrayList<>();
                //添加
                files.add(uploadModel);
                //上传
                String str = UploadTool.postFile(FlappyConfig.getInstance().fileUpload(), getParamMap, files);

                //返回数据
                BaseApiModel<String> baseApiModel = new BaseApiModel<>();
                //创建
                JSONObject jb = new JSONObject(str);
                //返回码
                baseApiModel.setCode(jb.optString("code"));
                //解析code
                baseApiModel.setMsg(jb.optString("msg"));
                //返回的总页码
                baseApiModel.setPageCount(jb.optInt("pageCount"));
                //设置返回的数据
                if (jb.optJSONArray("data") != null && Objects.requireNonNull(jb.optJSONArray("data")).length() > 0) {
                    baseApiModel.setData(Objects.requireNonNull(jb.optJSONArray("data")).getString(0));
                }
                //上传不成功抛出异常
                if (!baseApiModel.getCode().equals(RESULT_SUCCESS)) {
                    throw new Exception(baseApiModel.getMsg());
                }
                //设置数据返回
                voice.setPath(baseApiModel.getData());
                //设置数据
                data.setChatVoice(voice);
                //消息
                return data;
            }

            @Override
            public void failure(Exception e, String s) {
                updateMsgFailure(msg);
                callback.failure(msg, e, Integer.parseInt(RESULT_NET_ERROR));
            }

            @Override
            public void success(ChatMessage msg, String s) {
                //得到上传后的消息实体
                //设置真实的信息
                sendMessage(msg, callback);
            }
        }, msg, null);
    }


    //上传视频并发送
    protected void uploadVideoAndSend(final ChatMessage msg, final FlappySendCallback<ChatMessage> callback) {
        //发送
        client.execute(new LXAsyncTask<ChatMessage, ChatMessage>() {
            @Override
            public ChatMessage run(ChatMessage data, String s) throws Exception {

                //取得图片信息
                ChatVideo video = data.getChatVideo();
                ArrayList<UploadModel> files = new ArrayList<>();

                //封面图片添加
                UploadModel overFileModel = new UploadModel();
                //上传文件
                overFileModel.setName("cover");
                //上传路径
                overFileModel.setPath(video.getCoverSendPath());
                //添加入上传
                files.add(overFileModel);


                //视频地址添加
                UploadModel videoModel = new UploadModel();
                //文件
                videoModel.setName("video");
                //路径
                videoModel.setPath(video.getSendPath());
                //添加
                files.add(videoModel);


                //返回的字符串
                String str = UploadTool.postFile(FlappyConfig.getInstance().videoUpload(), new HashMap<>(), files);
                //返回数据
                BaseApiModel<ResponseUpload> baseApiModel = new BaseApiModel<>();
                //创建
                JSONObject jb = new JSONObject(str);
                //返回码
                baseApiModel.setCode(jb.optString("code"));
                //解析code
                baseApiModel.setMsg(jb.optString("msg"));
                //返回的总页码
                baseApiModel.setPageCount(jb.optInt("pageCount"));
                //设置返回的数据
                baseApiModel.setData(GsonTool.jsonStringToModel(jb.optString("data"), ResponseUpload.class));
                //上传不成功抛出异常
                if (!baseApiModel.getCode().equals(RESULT_SUCCESS)) {
                    throw new Exception(baseApiModel.getMsg());
                }
                //设置数据返回
                video.setPath(baseApiModel.getData().getFilePath());
                //简介图片地址
                video.setCoverPath(baseApiModel.getData().getOverFilePath());
                //转换为content
                data.setChatVideo(video);
                return data;
            }

            @Override
            public void failure(Exception e, String s) {
                updateMsgFailure(msg);
                callback.failure(msg, e, StringTool.strToDecimal(s).intValue());
            }

            @Override
            public void success(ChatMessage msg, String s) {
                updateMsgSent(msg);
                sendMessage(msg, callback);
            }
        }, msg, null);
    }


    //上传音频文件并发送
    protected void uploadFileAndSend(final ChatMessage msg, final FlappySendCallback<ChatMessage> callback) {
        //发送
        client.execute(new LXAsyncTask<ChatMessage, ChatMessage>() {
            @Override
            public ChatMessage run(ChatMessage data, String s) throws Exception {

                //转换出image对象
                ChatFile chatFile = msg.getChatFile();

                //构建文件参数
                HashMap<String, Object> paramMap = new HashMap<>();

                //上传文件
                ArrayList<UploadModel> files = new ArrayList<>();
                //上传
                UploadModel uploadModel = new UploadModel();
                //名称
                uploadModel.setName("file");
                //路径
                uploadModel.setPath(chatFile.getSendPath());
                //添加
                files.add(uploadModel);
                //上传
                String str = UploadTool.postFile(FlappyConfig.getInstance().fileUpload(), paramMap, files);

                //返回数据
                BaseApiModel<String> baseApiModel = new BaseApiModel<>();
                //创建
                JSONObject jb = new JSONObject(str);
                //返回码
                baseApiModel.setCode(jb.optString("code"));
                //解析code
                baseApiModel.setMsg(jb.optString("msg"));
                //返回的总页码
                baseApiModel.setPageCount(jb.optInt("pageCount"));
                //设置返回的数据
                if (jb.optJSONArray("data") != null && Objects.requireNonNull(jb.optJSONArray("data")).length() > 0) {
                    baseApiModel.setData(Objects.requireNonNull(jb.optJSONArray("data")).getString(0));
                }
                //上传不成功抛出异常
                if (!baseApiModel.getCode().equals(RESULT_SUCCESS)) {
                    throw new Exception(baseApiModel.getMsg());
                }

                //设置数据返回
                chatFile.setPath(baseApiModel.getData());
                //设置文件
                data.setChatFile(chatFile);
                //消息
                return data;
            }

            @Override
            public void failure(Exception e, String s) {
                updateMsgFailure(msg);
                callback.failure(msg, e, Integer.parseInt(RESULT_NET_ERROR));
            }

            @Override
            public void success(ChatMessage msg, String s) {
                updateMsgSent(msg);
                sendMessage(msg, callback);
            }
        }, msg, null);
    }

}
