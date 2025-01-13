package com.flappygo.flappyim.ApiServer.Clients;

import com.flappygo.flappyim.ApiServer.Clients.AsyncTask.LXAsyncTaskClient;
import com.flappygo.flappyim.ApiServer.Clients.AsyncTask.LXAsyncTask;
import com.flappygo.flappyim.Tools.Secret.AESTool;
import com.flappygo.flappyim.Tools.Secret.RSATool;
import com.flappygo.flappyim.Datas.DataManager;
import com.flappygo.flappyim.Tools.StringTool;
import com.flappygo.flappyim.FlappyImService;

import okhttp3.RequestBody;

import org.json.JSONObject;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.HashMap;

import okhttp3.MediaType;

import okhttp3.Response;
import okhttp3.Request;

import java.util.UUID;
import java.util.Map;


/******
 * OkHttp请求工具类
 */
public class OkHttpClient {


    /******
     * uuid
     */
    private static final String dataUuid = String.valueOf(UUID.randomUUID());

    /******
     * 请求秘钥
     */
    private static final String dataKey = StringTool.getRandomStr(16);


    /******
     * 单例
     */
    private static final class InstanceHolder {
        static final OkHttpClient instance = new OkHttpClient();
    }

    /*****
     * 单例
     * @return OkHttpClient
     */
    public static OkHttpClient getInstance() {
        return OkHttpClient.InstanceHolder.instance;
    }


    /******
     * 创建请求池
     */
    public final LXAsyncTaskClient httpTaskClient = new LXAsyncTaskClient(20);


    /******
     * 创建OkHttpClient实例
     */
    private final okhttp3.OkHttpClient client = new okhttp3.OkHttpClient.Builder().build();


    /******
     * 请求Post
     * @param url 地址
     * @param data 数据
     * @return 结果
     * @throws IOException 网络错误
     */
    public String postJson(String url, Map<String, Object> data) throws Exception {
        return postJson(url, data, new HashMap<>());
    }


    /******
     * 请求Post
     * @param url      请求地址
     * @param data     请求数据
     * @param callback 回调
     */
    public void postJson(String url,
            Map<String, Object> data,
            OkHttpAsyncCallback callback) {
        postJson(url, data, new HashMap<>(), callback);
    }

    /******
     * 请求Post
     * @param url      请求地址
     * @param data     请求数据
     * @param header   请求Header
     * @param callback 回调
     */
    @SuppressWarnings("unchecked")
    public void postJson(String url,
            Map<String, Object> data,
            HashMap<String, String> header,
            OkHttpAsyncCallback callback) {
        ArrayList<Object> dataList = new ArrayList<>();
        dataList.add(url);
        dataList.add(data);
        dataList.add(header);
        httpTaskClient.execute(new LXAsyncTask<ArrayList<Object>, String>() {
            @Override
            public String run(ArrayList<Object> data, String tag) throws Exception {
                String url = (String) data.get(0);
                Map<String, Object> dataParam = (Map<String, Object>) data.get(1);
                HashMap<String, String> headerParam = (HashMap<String, String>) data.get(2);
                return OkHttpClient.getInstance().postJson(url, dataParam, headerParam);
            }

            @Override
            public void failure(Exception ex, String tag) {
                callback.failure(ex, tag);
                if (ex instanceof UnauthorizedException) {
                    FlappyImService.getInstance().kickedOut();
                }
            }

            @Override
            public void success(String data, String tag) {
                callback.success(data, tag);
            }
        }, dataList);
    }


    /******
     * 请求Post
     * @param url 地址
     * @param data 数据
     * @param header 请求头
     * @return 结果
     * @throws IOException 网络错误
     */
    public String postJson(String url,
            Map<String, Object> data,
            HashMap<String, String> header) throws Exception {

        ///数据
        String dataStr = new JSONObject(data).toString();

        ///RSA
        if (!StringTool.isEmpty(DataManager.getInstance().getRSAPublicKey())) {
            dataStr = AESTool.EncryptECBNoThrow(
                    dataStr,
                    dataKey
            );
        }

        //创建请求体(RequestBody)，这里以JSON字符串为例
        RequestBody body = RequestBody.create(
                dataStr.getBytes(),
                MediaType.get("application/json; charset=utf-8")
        );

        //创建请求（Request）
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);

        //添加所有的请求头
        for (String key : header.keySet()) {
            builder.addHeader(key, Objects.requireNonNull(header.get(key)));
        }

        //Uuid
        builder.addHeader("dataUuid", dataUuid);

        //传递加密秘钥
        if (!StringTool.isEmpty(DataManager.getInstance().getRSAPublicKey())) {
            builder.addHeader("dataKey", RSATool.encryptWithPublicKey(
                    DataManager.getInstance().getRSAPublicKey(),
                    dataKey
            ));
        } else {
            builder.addHeader("dataKey", dataKey);
        }

        //传递用户信息
        if (!StringTool.isEmpty(DataManager.getInstance().getUserToken())) {
            String dataToken = DataManager.getInstance().getUserToken();
            builder.addHeader(
                    "dataToken",
                    AESTool.EncryptECBNoThrow(dataToken, dataKey)
            );
        }

        //构建
        Request request = builder.build();

        //发送请求并获取响应
        Response response = client.newCall(request).execute();

        // 检查是否是 401 未授权
        if (response.code() == 401) {
            throw new UnauthorizedException("Unauthorized: Invalid or expired token");
        }

        //判断请求是否成功
        if (!response.isSuccessful()) {
            assert response.body() != null;
            throw new IOException(response.body().string());
        }

        //获取响应体的字符串
        assert response.body() != null;

        //结束
        String bodyEncryptStr = response.body().string();
        return AESTool.DecryptECBNoThrow(bodyEncryptStr, dataKey);
    }

    //未授权
    static class UnauthorizedException extends Exception {
        private static final long serialVersionUID = -4058737144721996830L;

        public UnauthorizedException(String message) {
            super(message);
        }
    }

}


