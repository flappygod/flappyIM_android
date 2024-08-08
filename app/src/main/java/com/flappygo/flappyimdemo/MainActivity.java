package com.flappygo.flappyimdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.flappygo.flappyim.Callback.FlappyIMCallback;
import com.flappygo.flappyim.Callback.FlappySendCallback;
import com.flappygo.flappyim.FlappyImService;
import com.flappygo.flappyim.Listener.KickedOutListener;
import com.flappygo.flappyim.Listener.MessageListener;
import com.flappygo.flappyim.Listener.NotificationClickListener;
import com.flappygo.flappyim.Listener.SessionListener;
import com.flappygo.flappyim.Models.Response.ResponseLogin;
import com.flappygo.flappyim.DataBase.Models.SessionModel;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.flappyim.Session.FlappyChatSession;
import com.lcw.library.imagepicker.ImagePicker;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    // 获取相册图片
    private static final int REQUEST_GET_PICTURE = 24;

    private FlappyChatSession mySession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FlappyImService.getInstance().setPushPlat("Google");
        //服务初始
        FlappyImService.getInstance().init((Activity) getBaseContext(), "http://192.168.31.11", "http://192.168.31.11");
        //开启服务
        FlappyImService.getInstance().startServer();

        final EditText message = findViewById(R.id.chatMessage);
        final TextView rect = findViewById(R.id.testrect);


        //创建账号
        FlappyImService.getInstance().createAccount(
                "100",
                "李俊霖",
                "",
                "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1183057007,4270556535&fm=26&gp=0.jpg",
                new FlappyIMCallback<ChatUser>() {
                    @Override
                    public void success(ChatUser data) {
                        Toast.makeText(getBaseContext(), "账号创建成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(Exception ex, int code) {
                        Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        FlappyImService.getInstance().setNotificationClickListener(new NotificationClickListener() {
            @Override
            public void notificationClicked(ChatMessage chatMessage) {
                System.out.println(chatMessage.getChatText());
            }
        });

        FlappyImService.getInstance().addSessionListener(new SessionListener() {
            @Override
            public void sessionUpdate(SessionModel session) {

            }

            @Override
            public void sessionDelete(SessionModel session) {

            }
        });

        FlappyImService.getInstance().setKickedOutListener(new KickedOutListener() {
            @Override
            public void kickedOut() {
                Toast.makeText(getBaseContext(), "当前设备已经被踢下线了", Toast.LENGTH_SHORT).show();
            }
        });


        FlappyImService.getInstance().addGlobalMessageListener(new MessageListener() {
            @Override
            public void messageSend(ChatMessage chatMessage) {

            }

            @Override
            public void messageFailed(ChatMessage chatMessage) {

            }

            @Override
            public void messageUpdate(ChatMessage chatMessage) {

            }

            @Override
            public void messageDelete(ChatMessage chatMessage) {

            }
            @Override
            public void messageReceived(ChatMessage chatMessage) {

                if (chatMessage.getMessageType().intValue() == ChatMessage.MSG_TYPE_TEXT) {
                    Toast.makeText(getBaseContext(), chatMessage.getChatText(), Toast.LENGTH_SHORT).show();
                }
                if (chatMessage.getMessageType().intValue() == ChatMessage.MSG_TYPE_IMG) {
                    Toast.makeText(getBaseContext(), chatMessage.getChatImage().getPath(), Toast.LENGTH_SHORT).show();
                }
                if (chatMessage.getMessageType().intValue() == ChatMessage.MSG_TYPE_LOCATE) {
                    Toast.makeText(getBaseContext(), chatMessage.getChatLocation().getAddress(), Toast.LENGTH_SHORT).show();
                }
                if (chatMessage.getMessageType().intValue() == ChatMessage.MSG_TYPE_VIDEO) {
                    Toast.makeText(getBaseContext(), chatMessage.getChatVideo().getSendPath(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void messageReadOther(String sessionId, String readerId, String tableOffset) {

            }

            @Override
            public void messageReadSelf(String sessionId, String readerId, String tableOffset) {

            }

        });

        //登录
        Button login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //创建账号
                FlappyImService.getInstance().login(
                        "100",
                        new FlappyIMCallback<ResponseLogin>() {
                            @Override
                            public void success(ResponseLogin data) {
                                Toast.makeText(getBaseContext(), "账号登录成功", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void failure(Exception ex, int code) {
                                Toast.makeText(getBaseContext(), "账号登录失败", Toast.LENGTH_SHORT).show();

                            }
                        });

            }
        });


        FlappyImService.getInstance().setNotificationClickListener(new NotificationClickListener() {
            @Override
            public void notificationClicked(ChatMessage chatMessage) {

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);

            }
        });

        //创建会话
        Button session = findViewById(R.id.session);
        session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FlappyImService.getInstance().createSingleSessionByPeer("101", new FlappyIMCallback<FlappyChatSession>() {
                    @Override
                    public void success(FlappyChatSession data) {
                        Toast.makeText(getBaseContext(), "会话创建成功", Toast.LENGTH_SHORT).show();

                        //set session and add listener
                        mySession = data;
                        mySession.addMessageListener(new MessageListener() {
                            @Override
                            public void messageSend(ChatMessage chatMessage) {

                            }

                            @Override
                            public void messageFailed(ChatMessage chatMessage) {

                            }

                            @Override
                            public void messageUpdate(ChatMessage chatMessage) {

                            }

                            @Override
                            public void messageReceived(ChatMessage chatMessage) {
                                rect.setText(chatMessage.getChatText());
                            }

                            @Override
                            public void messageDelete(ChatMessage chatMessage) {

                            }

                            @Override
                            public void messageReadOther(String sessionId, String readerId, String tableOffset) {

                            }

                            @Override
                            public void messageReadSelf(String sessionId, String readerId, String tableOffset) {

                            }

                        });

                        ChatMessage chatMessage = mySession.getLatestMessage();

                        if (chatMessage != null) {
                            List<ChatMessage> messages = mySession.getFormerMessages(chatMessage.getMessageId(), 10);
                            List<ChatMessage> messageArrayList = new ArrayList<>();
                            messageArrayList.add(chatMessage);
                            messageArrayList.addAll(messages);
                            for (int w = 0; w < messageArrayList.size(); w++) {
                                System.out.println(messageArrayList.get(w).getMessageTableOffset().toString());
                            }
                        }

                    }

                    @Override
                    public void failure(Exception ex, int code) {
                        Toast.makeText(getBaseContext(), "会话创建失败", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        //消息发送
        Button send = findViewById(R.id.send);

        //发送啦
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mySession != null) {

                    if (message.getText().toString().equals("")) {
                        return;
                    }


                    mySession.sendText(message.getText().toString(), new FlappySendCallback<ChatMessage>() {
                        @Override
                        public void success(ChatMessage data) {

                            Toast.makeText(getBaseContext(), "消息已经发送", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void failure(ChatMessage data, Exception ex, int code) {

                            Toast.makeText(getBaseContext(), "消息发送失败", Toast.LENGTH_SHORT).show();

                        }

                    });
                }
            }
        });

        Button sendImg = findViewById(R.id.send_img);
        sendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoGetPicture();
            }
        });

    }


    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }


    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Toast.makeText(getBaseContext(), (String) msg.obj, Toast.LENGTH_SHORT).show();
        }
    };

    /************
     * 获取相册图片
     */
    private void gotoGetPicture() {
        ImagePicker.getInstance()
                .setTitle("标题")//设置标题
                .showCamera(true)//设置是否显示拍照按钮
                .showImage(true)//设置是否展示图片
                .showVideo(true)//设置是否展示视频
                .setSingleType(true)//设置图片视频不能同时选择
                .setMaxCount(9)//设置最大选择图片数目(默认为1，单选)
                .start(MainActivity.this, 100);//REQEST_SELECT_IMAGES_CODE为Intent调用的requestCode
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null) {
            List<String> imagePaths = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);

            mySession.sendLocalImage(imagePaths.get(0), new FlappySendCallback<ChatMessage>() {

                @Override
                public void success(ChatMessage data) {
                    Toast.makeText(getBaseContext(), "消息已经发送", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void failure(ChatMessage data, Exception ex, int code) {
                    Toast.makeText(getBaseContext(), "消息发送失败", Toast.LENGTH_SHORT).show();
                }

            });
        }
    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //成功
        if (resultCode == RESULT_OK) {
            //如果是获取图片，返回回来之后得到图片的地址
            if (requestCode == REQUEST_GET_PICTURE) {
                //地址
                String path = TakePicTool.getPicAnalyze(this, data);
                //地址
                if (!StringTool.isEmpty(path)) {

                    mySession.senLocalVideo(path, new FlappySendCallback<ChatMessage>() {

                        @Override
                        public void success(ChatMessage data) {
                            Toast.makeText(getBaseContext(), "消息已经发送", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failure(ChatMessage data, Exception ex, int code) {
                            Toast.makeText(getBaseContext(), "消息发送失败", Toast.LENGTH_SHORT).show();
                        }

                    });

                }
            }
        }
    }*/
}
