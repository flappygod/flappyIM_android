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
import com.flappygo.flappyim.Session.FlappySessionData;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Session.FlappyChatSession;
import com.flappygo.flappyim.Tools.Secret.RSATool;
import com.lcw.library.imagepicker.ImagePicker;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    // 获取相册图片
    private static final int REQUEST_GET_PICTURE = 24;

    private FlappyChatSession mySession;

    //我们的公钥字符串
    private String publicKeyStr = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA/EUkiEsrSMsdS3Eb8d5+\n" +
            "RuJlcCa9sQ9nImZ7glnTT9pGfyPPqW5HFcKamNsd1PRb+VFr6OK01i1neakLjHA4\n" +
            "XGBxDg6JKdAdWakk+xMib5OZnhDEin9wGNBmTLCJLreN+vJkj0Knb6D9ClLgHWkl\n" +
            "6mcQUvkU59ckr7NcG4/h9pbFWVigDrDitlpTRZBxOhUH9cOcRlu5nCc2r07hQRvk\n" +
            "ZUBCfg5Gs0liXJsUfeCigpqvKYpFTx2Iz48uRUD9bJKVHyvI3girTR32flbt0pmi\n" +
            "8k/1/Rs74+g6YD1/RC/Bc03eOXfdHQMsy1XjN94sjHNhHgLPE+1TKXjEeOpnb8Gk\n" +
            "kQIDAQAB\n" +
            "-----END PUBLIC KEY-----";


    //我们的私钥字符串
    private String privateKeyPKCS8 = "-----BEGIN PRIVATE KEY-----\n" +
            "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQD8RSSISytIyx1L\n" +
            "cRvx3n5G4mVwJr2xD2ciZnuCWdNP2kZ/I8+pbkcVwpqY2x3U9Fv5UWvo4rTWLWd5\n" +
            "qQuMcDhcYHEODokp0B1ZqST7EyJvk5meEMSKf3AY0GZMsIkut4368mSPQqdvoP0K\n" +
            "UuAdaSXqZxBS+RTn1ySvs1wbj+H2lsVZWKAOsOK2WlNFkHE6FQf1w5xGW7mcJzav\n" +
            "TuFBG+RlQEJ+DkazSWJcmxR94KKCmq8pikVPHYjPjy5FQP1skpUfK8jeCKtNHfZ+\n" +
            "Vu3SmaLyT/X9Gzvj6DpgPX9EL8FzTd45d90dAyzLVeM33iyMc2EeAs8T7VMpeMR4\n" +
            "6mdvwaSRAgMBAAECggEAOiDAezs+3QYYWPLWKpRG6pRuJTp4CR1weUe9+9owy7yS\n" +
            "8+bPic3kSUpPDxumMxSfQMKXJ9FdM+DAcBMYmyKcigSa6E9HIcBXkvpbkBLno2gS\n" +
            "RI6+it80eDNE7zTaV05qQ8GolC4aoKkqxT81bvF0kB9xbn0AaS90v5uHhL5RpdzA\n" +
            "vSNPRm39Cxz2+TuWdqDIVi9iWMVGE3093C15a7pxQKt2ztrbN40YOIQB6d/CB077\n" +
            "SDC5Iqzpj5qdeJFPqkUTDYSIN4uq7y3FpiqJueYsorjRzUnTuTh1ehAa7e4Wi+c7\n" +
            "/HKhokcrS6viMW1wdoBvGP3uaDCrgl2BfQbbgbQgVQKBgQD/GHVP5kdzdVlWBdkB\n" +
            "zp+ph9OVJkU77oATHiKdXjea/TKlZVAqhJZrvqIZ/StGg+svbBYlTCmv3YU7A71o\n" +
            "R5S4dEeTLj9KVDMPutwb/vg4r5GJIPIiJxjeCkZ4uYYgrk2P3D2uxDOvItPzgo/U\n" +
            "x9jvP24olwSu0paUzzMfymitBwKBgQD9Kh6w163y2sewMkOAicPxw6vV9d2Ve9L0\n" +
            "jPaIUfeLjaRHkNcHcUzcuGF3ceFrrMD75buTC2k5AbMz8ZHRmrKJVfu6NASrqvDj\n" +
            "edFhivCXWy6BpFeZ4X3+ZJ53OXWxwZBEzu8uhJoqWzN3J1Flam9BruSKckc5o776\n" +
            "cKGFT0TTpwKBgQDx3oPktSgMLjj9WmAO2ZYDTTjtUzMUByhCeDFD2sCIYQhzUCN5\n" +
            "nOtuz3qtf6FXo98LwMUiqhtgl34qnXoqdKxrVD1FLPVviXQ8tuTaWp+KR9WLGsTa\n" +
            "Yw2uAjodX3Lwa7Q48g8+NOP4a+JhIes0SiTDe/X74GzdQejqwabsvqMPYwKBgFZe\n" +
            "BjZifi95v+I2Y5z4YuaZ0Ief50ZWBfP0Gy8Kd58eZUsc+J7LYmNya53qNfMb7oKB\n" +
            "L2rM28rc75vq78pIMlxz/vrZQDaojKGuL2ZNliKsssL7o/8VVHxzKzSVX7eSx3sR\n" +
            "9bsy9b05e+dMfJJSqz4HQmSQ9AeP+1lJD3GBR4PFAoGBAM9m39+MCa1by4kTOt3R\n" +
            "aTXW19hDKeHSQ5jNxxxTwi5adxiqoFvGcVwn069b3pKas8Uhr/Pwxh4pTYucF/nD\n" +
            "vow9xlu2lma/DKnCSi4X6KyHF+kIzu9n6e8wVqDcPo8IUG1SbShQgDlY6YQu3qzW\n" +
            "i8gv8gVianXQTrYb8RtyUene\n" +
            "-----END PRIVATE KEY-----";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            String encryptStr= RSATool.encryptWithPublicKey(publicKeyStr,"四川雅安河道现大熊猫尸体");
            System.out.println("加密:"+encryptStr + "\n");
            String decryptStr=RSATool.decryptWithPrivatePKCS8(privateKeyPKCS8,"Gw7JoQi+XVG9wM68aRvy42VcI9DIH7BmXC63ZhtBqiU0eR6BpGLEZFupFUkZtu/vXbrUTFoR91ktdrx9QEOYuI0yjA8KQaXfgzlxuHwkMb5wBFniSM4DsLUX97Sc/ojAEJxzqkKP9aAy5JCRi00rqrk0bcoqxvwjZ7o3ta4oD1Y7V0+2bOTeJ1Y8XBW9CMluPTuYzND31xKy4sSLlMtOyh5VyEGbQYkRObrH4fpWJ/X2dsbCTz0/G2JBh0hFdI6j3KEW93S6vnS5bNop0ifBwgH3439oREUiEc1Ra1B6MA1nYUsm78gldwSlV7b6MpWTvf+MvHG1XKBKWDC3aSadQA==");
            System.out.println("解密:"+decryptStr+ "\n");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }



        FlappyImService.getInstance().setPushPlatform("Google");
        //服务初始
        FlappyImService.getInstance().init(getBaseContext(), "http://192.168.31.11", "http://192.168.31.11");
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
                new FlappyIMCallback<String>() {
                    @Override
                    public void success(String data) {
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
            public void sessionUpdate(FlappySessionData session) {

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
            public void messageReadOther(String sessionId, String readerId, String tableSequence) {

            }

            @Override
            public void messageReadSelf(String sessionId, String readerId, String tableSequence) {

            }

            @Override
            public void messageDelete(String messageId) {

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
                FlappyImService.getInstance().createSingleSession("101", new FlappyIMCallback<FlappyChatSession>() {
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
                            public void messageReadOther(String sessionId, String readerId, String tableSequence) {

                            }

                            @Override
                            public void messageReadSelf(String sessionId, String readerId, String tableSequence) {

                            }

                            @Override
                            public void messageDelete(String messageId) {

                            }
                        });

                        ChatMessage chatMessage = mySession.getLatestMessage();

                        if (chatMessage != null) {
                            List<ChatMessage> messages = mySession.getFormerMessages(chatMessage.getMessageId(), 10);
                            List<ChatMessage> messageArrayList = new ArrayList<>();
                            messageArrayList.add(chatMessage);
                            messageArrayList.addAll(messages);
                            for (int w = 0; w < messageArrayList.size(); w++) {
                                System.out.println(messageArrayList.get(w).getMessageTableSeq().toString());
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
