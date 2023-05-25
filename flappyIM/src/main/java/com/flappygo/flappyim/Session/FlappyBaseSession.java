package com.flappygo.flappyim.Session;


import com.flappygo.lilin.lxhttpclient.Asynctask.LXAsyncTaskClient;
import com.flappygo.lilin.lxhttpclient.Asynctask.LXAsyncTask;
import com.flappygo.flappyim.Models.Response.ResponseUpload;
import com.flappygo.flappyim.ApiServer.Models.BaseApiModel;
import com.flappygo.flappyim.Callback.FlappySendCallback;
import com.flappygo.flappyim.Handler.ChannelMsgHandler;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Tools.Upload.UploadModel;
import com.flappygo.flappyim.Models.Request.ChatImage;
import com.flappygo.flappyim.Models.Request.ChatVideo;
import com.flappygo.flappyim.Models.Request.ChatVoice;
import com.flappygo.flappyim.Models.Request.ChatFile;
import com.flappygo.flappyim.Tools.Upload.UploadTool;
import com.flappygo.flappyim.Tools.Upload.LXImageWH;
import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.flappyim.Service.FlappyService;
import com.flappygo.flappyim.Config.FlappyConfig;
import com.flappygo.flappyim.Thread.NettyThread;
import com.flappygo.flappyim.DataBase.Database;
import com.flappygo.flappyim.Datas.DataManager;
import com.flappygo.flappyim.FlappyImService;
import com.flappygo.flappyim.Tools.StringTool;
import com.flappygo.flappyim.Tools.VideoTool;

import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import java.math.BigDecimal;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.flappygo.flappyim.Models.Server.ChatMessage.SEND_STATE_CREATE;
import static com.flappygo.flappyim.Models.Server.ChatMessage.SEND_STATE_FAILURE;
import static com.flappygo.flappyim.Models.Server.ChatMessage.SEND_STATE_SENT;
import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_NET_ERROR;
import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_SUCCESS;

public class FlappyBaseSession {


    ///获取当前的消息handler
    ChannelMsgHandler getCurrentChannelMessageHandler() {
        FlappyService flappyService = FlappyService.getInstance();
        //如果当前服务不在线，错误
        if (flappyService == null) {
            return null;
        }
        //服务中的线程没有运行
        NettyThread thread = flappyService.getClientThread();
        if (thread == null) {
            return null;
        }
        //线程中的handler不存在
        ChannelMsgHandler handler = thread.getChannelMsgHandler();
        return handler;
    }

    //我们姑且认为是最后一条
    public void insertMessage(ChatMessage msg) {
        msg.setMessageSendState(new BigDecimal(SEND_STATE_CREATE));
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        BigDecimal bigDecimal = StringTool.strToDecimal(chatUser.getLatest());
        bigDecimal = bigDecimal.add(new BigDecimal(1));
        msg.setMessageTableSeq(bigDecimal);
        Database database = new Database();
        database.insertMessage(msg);
        database.close();

        //线程中的handler不存在
        ChannelMsgHandler handler = getCurrentChannelMessageHandler();
        if (handler != null) {
            handler.notifyMessageSend(msg);
        }
    }


    //发送失败了更新数据
    private void updateMsgFailure(ChatMessage msg) {
        msg.setMessageSendState(new BigDecimal(SEND_STATE_FAILURE));
        Database database = new Database();
        database.insertMessage(msg);
        database.close();
        //线程中的handler不存在
        ChannelMsgHandler handler = getCurrentChannelMessageHandler();
        if (handler != null) {
            handler.notifyMessageFailure(msg);
        }
    }


    //将消息的状态更新为已经发送
    private void updateMsgSent(ChatMessage msg) {
        msg.setMessageSendState(new BigDecimal(SEND_STATE_SENT));
        Database database = new Database();
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
        //client
        LXAsyncTaskClient client = new LXAsyncTaskClient(1);
        //发送
        client.excute(new LXAsyncTask<ChatMessage, ChatMessage>() {
            @Override
            public ChatMessage run(ChatMessage data, String s) throws Exception {
                //取得消息体
                ChatImage image = data.getChatImage();

                //构建文件参数
                HashMap<String, Object> paramMap = new HashMap<>();

                UploadModel uploadModel = new UploadModel();
                uploadModel.setName("file");
                uploadModel.setPath(image.getSendPath());
                ArrayList<UploadModel> files = new ArrayList<>();
                files.add(uploadModel);

                //返回的字符串
                String str = UploadTool.postFile(FlappyConfig.getInstance().fileUpload, paramMap, files);


                //返回数据
                BaseApiModel<String> baseApiModel = new BaseApiModel<>();
                //创建
                JSONObject jb = new JSONObject(str);
                //返回码
                baseApiModel.setCode(jb.optString("code"));
                //解析code
                baseApiModel.setMsg(jb.optString("msg"));
                //返回的消息
                baseApiModel.setSign(jb.optString("sign"));
                //返回的总页码
                baseApiModel.setPageCount(jb.optInt("pageCount"));
                //设置返回的数据
                if (jb.optJSONArray("data") != null && jb.optJSONArray("data").length() > 0) {
                    baseApiModel.setData(jb.optJSONArray("data").getString(0));
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
        //client
        LXAsyncTaskClient client = new LXAsyncTaskClient(1);
        //发送
        client.excute(new LXAsyncTask<ChatMessage, ChatMessage>() {
            @Override
            public ChatMessage run(ChatMessage data, String s) throws Exception {

                //转换出image对象
                ChatVoice voice = data.getChatVoice();

                //构建文件参数
                HashMap<String, Object> getParamMap = new HashMap<>();

                //构建文件参数
                UploadModel uploadModel = new UploadModel();
                uploadModel.setName("file");
                uploadModel.setPath(voice.getSendPath());
                ArrayList<UploadModel> files = new ArrayList<>();
                files.add(uploadModel);
                String str = UploadTool.postFile(FlappyConfig.getInstance().fileUpload, getParamMap, files);


                //返回数据
                BaseApiModel<String> baseApiModel = new BaseApiModel<>();
                //创建
                JSONObject jb = new JSONObject(str);
                //返回码
                baseApiModel.setCode(jb.optString("code"));
                //解析code
                baseApiModel.setMsg(jb.optString("msg"));
                //返回的消息
                baseApiModel.setSign(jb.optString("sign"));
                //返回的总页码
                baseApiModel.setPageCount(jb.optInt("pageCount"));
                //设置返回的数据
                if (jb.optJSONArray("data") != null && jb.optJSONArray("data").length() > 0) {
                    baseApiModel.setData(jb.optJSONArray("data").getString(0));
                }
                //上传不成功抛出异常
                if (!baseApiModel.getCode().equals(RESULT_SUCCESS)) {
                    throw new Exception(baseApiModel.getMsg());
                }

                //设置数据返回
                voice.setPath(baseApiModel.getData());

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
        //client
        LXAsyncTaskClient client = new LXAsyncTaskClient(1);
        //发送
        client.excute(new LXAsyncTask<ChatMessage, ChatMessage>() {
            @Override
            public ChatMessage run(ChatMessage data, String s) throws Exception {

                //取得图片信息
                ChatVideo video = data.getChatVideo();

                ArrayList<UploadModel> files = new ArrayList<>();

                //视频
                UploadModel videoModel = new UploadModel();
                videoModel.setName("file");
                videoModel.setPath(video.getSendPath());
                files.add(videoModel);

                //获取到图片的bitmap
                VideoTool.VideoInfo info = VideoTool.getVideoInfo(FlappyImService.getInstance().getAppContext(),
                        512,
                        video.getSendPath());

                //封面
                UploadModel overFileModel = new UploadModel();
                overFileModel.setName("file");
                overFileModel.setPath(info.getOverPath());
                files.add(overFileModel);

                //返回的字符串
                String str = UploadTool.postFile(FlappyConfig.getInstance().videoUpload, new HashMap<>(), files);
                //返回数据
                BaseApiModel<ResponseUpload> baseApiModel = new BaseApiModel<>();
                //创建
                JSONObject jb = new JSONObject(str);
                //返回码
                baseApiModel.setCode(jb.optString("code"));
                //解析code
                baseApiModel.setMsg(jb.optString("msg"));
                //返回的消息
                baseApiModel.setSign(jb.optString("sign"));
                //返回的总页码
                baseApiModel.setPageCount(jb.optInt("pageCount"));
                //设置返回的数据
                baseApiModel.setData(GsonTool.jsonObjectToModel(jb.optString("data"), ResponseUpload.class));
                //上传不成功抛出异常
                if (!baseApiModel.getCode().equals(RESULT_SUCCESS)) {
                    throw new Exception(baseApiModel.getMsg());
                }
                //设置时长
                video.setDuration(info.getDuration());
                //设置宽度
                video.setWidth(info.getWidth());
                //设置高度
                video.setHeight(info.getHeight());
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
        //client
        LXAsyncTaskClient client = new LXAsyncTaskClient(1);
        //发送
        client.excute(new LXAsyncTask<ChatMessage, ChatMessage>() {
            @Override
            public ChatMessage run(ChatMessage data, String s) throws Exception {

                //转换出image对象
                ChatFile chatFile = msg.getChatFile();

                //构建文件参数
                HashMap<String, Object> paramMap = new HashMap<>();
                //构建文件参数
                HashMap<String, String> fileMap = new HashMap<>();
                //地址
                fileMap.put("file", chatFile.getSendPath());


                ArrayList<UploadModel> files = new ArrayList<>();
                UploadModel uploadModel = new UploadModel();
                uploadModel.setName("file");
                uploadModel.setPath(chatFile.getSendPath());
                files.add(uploadModel);
                String str = UploadTool.postFile(FlappyConfig.getInstance().fileUpload, paramMap, files);


                //返回数据
                BaseApiModel<String> baseApiModel = new BaseApiModel<>();
                //创建
                JSONObject jb = new JSONObject(str);
                //返回码
                baseApiModel.setCode(jb.optString("code"));
                //解析code
                baseApiModel.setMsg(jb.optString("msg"));
                //返回的消息
                baseApiModel.setSign(jb.optString("sign"));
                //返回的总页码
                baseApiModel.setPageCount(jb.optInt("pageCount"));
                //设置返回的数据
                if (jb.optJSONArray("data") != null && jb.optJSONArray("data").length() > 0) {
                    baseApiModel.setData(jb.optJSONArray("data").getString(0));
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
