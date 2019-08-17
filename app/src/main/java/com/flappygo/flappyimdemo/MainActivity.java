package com.flappygo.flappyimdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flappygo.flappyim.Callback.FlappyIMCallback;
import com.flappygo.flappyim.FlappyImService;
import com.flappygo.flappyim.Listener.KnickedOutListener;
import com.flappygo.flappyim.Listener.MessageListener;
import com.flappygo.flappyim.Models.Response.ResponseLogin;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Models.Server.ChatSession;
import com.flappygo.flappyim.Session.FlappyChatSession;
import com.flappygo.flappyim.Tools.StringTool;
import com.flappygo.flappyim.Tools.TakePicTool;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    // 获取相册图片
    private static final int REQUEST_GETPICTURE = 24;

    private FlappyChatSession mySession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //服务初始
        FlappyImService.getInstance().init(getBaseContext());

        //用户text
        final EditText editText = findViewById(R.id.userid);
        final EditText message = findViewById(R.id.message);


        final TextView rect = findViewById(R.id.testrect);


        //创建账号
        Button create = findViewById(R.id.register);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editText.getText().toString().equals("")) {
                    return;
                }

                //创建账号
                FlappyImService.getInstance().createAccount(editText.getText().toString(),
                        "李俊霖",
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
            }
        });


        FlappyImService.getInstance().setKnickedOutListener(new KnickedOutListener() {
            @Override
            public void knickedOut() {

                Toast.makeText(getBaseContext(), "当前设备已经被踢下线了", Toast.LENGTH_SHORT).show();

            }
        });

        //登录
        Button login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editText.getText().toString().equals("")) {
                    return;
                }

                //创建账号
                FlappyImService.getInstance().login(
                        editText.getText().toString(),
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


        FlappyImService.getInstance().logout( new FlappyIMCallback<String>() {
            @Override
            public void success(String data) {

                Toast.makeText(getBaseContext(), "会话创建成功", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void failure(Exception ex, int code) {

                Toast.makeText(getBaseContext(), "会话创建成功", Toast.LENGTH_SHORT).show();
            }
        });

        //创建会话
        Button session = findViewById(R.id.session);
        session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FlappyImService.getInstance().getSessionByID("187-188", new FlappyIMCallback<FlappyChatSession>() {
                    @Override
                    public void success(FlappyChatSession chatSingleSession) {
                        Toast.makeText(getBaseContext(), "会话创建成功", Toast.LENGTH_SHORT).show();

                        //成功创建会话了崔我嫩
                        mySession = chatSingleSession;

                        //设置
                        mySession.addMessageListener(new MessageListener() {
                            @Override
                            public void messageRecieved(ChatMessage chatMessage) {

                                rect.setText(chatMessage.getMessageContent());

                            }
                        });

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


                    mySession.sendText(message.getText().toString(), new FlappyIMCallback<ChatMessage>() {
                        @Override
                        public void success(ChatMessage data) {

                            Toast.makeText(getBaseContext(), "消息已经发送", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void failure(Exception ex, int code) {

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

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            Toast.makeText(getBaseContext(), (String) msg.obj, Toast.LENGTH_SHORT).show();
        }
    };


    /************
     * 获取相册图片
     */
    private void gotoGetPicture() {
        try {
            if (Build.VERSION.SDK_INT >= 19) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_GETPICTURE);
            } else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, REQUEST_GETPICTURE);
            }
        } catch (SecurityException e) {

        } catch (Exception e) {

        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // 如果是获取图片，返回回来之后得到图片的地址
            if (requestCode == REQUEST_GETPICTURE) {
                String path = TakePicTool.getPicAnalyze(this, data);
                if (!StringTool.isEmpty(path)) {

                    //发送本地图片
                    mySession.sendLocalImage(path, new FlappyIMCallback<ChatMessage>() {
                        @Override
                        public void success(ChatMessage data) {

                            Toast.makeText(getBaseContext(), "消息已经发送", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void failure(Exception ex, int code) {

                            Toast.makeText(getBaseContext(), "消息发送失败", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            }
        }
    }
}
