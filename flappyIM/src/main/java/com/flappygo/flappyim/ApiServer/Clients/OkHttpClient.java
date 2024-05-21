package com.flappygo.flappyim.ApiServer.Clients;

import com.flappygo.flappyim.ApiServer.Clients.AsyncTask.LXAsyncTaskClient;
import com.flappygo.flappyim.ApiServer.Clients.AsyncTask.LXAsyncTask;

import okhttp3.RequestBody;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.HashMap;

import okhttp3.Response;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;

import java.util.Map;


/******
 * OkHttp请求工具类
 */
public class OkHttpClient {


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
    public String postJson(String url, Map<String, Object> data) throws IOException {
        return postJson(url, data, new HashMap<>());
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
            HashMap<String, String> header) throws IOException {

        //创建请求体（RequestBody），这里以JSON字符串为例
        RequestBody body = RequestBody.create(new JSONObject(data).toString().getBytes());

        // 创建请求（Request）
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);

        //添加所有的请求头
        for (String key : header.keySet()) {
            builder.addHeader(key, Objects.requireNonNull(header.get(key)));
        }

        //构建
        Request request = builder.build();

        // 发送请求并获取响应
        Response response = client.newCall(request).execute();

        // 判断请求是否成功
        if (!response.isSuccessful()) {
            assert response.body() != null;
            throw new IOException(response.body().string());
        }

        // 获取响应体的字符串
        assert response.body() != null;
        return response.body().string();
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
            public void failure(Exception e, String tag) {
                callback.failure(e, tag);
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
     * @return 结果
     * @throws IOException 网络错误
     */
    public String postParam(String url, HashMap<String, String> data) throws IOException {
        return postParam(url, data, new HashMap<>());
    }


    /******
     * 请求Post
     * @param url 地址
     * @param data 数据
     * @param header 请求头
     * @return 结果
     * @throws IOException 网络错误
     */
    public String postParam(String url,
            HashMap<String, String> data,
            HashMap<String, String> header) throws IOException {

        // 创建一个FormBody.Builder对象
        FormBody.Builder formBuilder = new FormBody.Builder();

        //添加所有的请求头
        for (String key : data.keySet()) {
            formBuilder.add(key, Objects.requireNonNull(data.get(key)));
        }

        // 构建FormBody对象
        FormBody formBody = formBuilder.build();

        // 创建请求（Request）
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(formBody);

        //添加所有的请求头
        for (String key : header.keySet()) {
            builder.addHeader(key, Objects.requireNonNull(header.get(key)));
        }

        //构建
        Request request = builder.build();

        // 发送请求并获取响应
        Response response = client.newCall(request).execute();

        // 判断请求是否成功
        if (!response.isSuccessful()) {
            assert response.body() != null;
            throw new IOException(response.body().string());
        }

        // 获取响应体的字符串
        assert response.body() != null;
        return response.body().string();
    }

    /******
     * 请求Post
     * @param url      请求地址
     * @param data     请求数据
     * @param callback 回调
     */
    public void postParam(String url,
            HashMap<String, String> data,
            OkHttpAsyncCallback callback) {
        postParam(url, data, new HashMap<>(), callback);
    }

    /******
     * 请求Post
     * @param url      请求地址
     * @param data     请求数据
     * @param header   请求Header
     * @param callback 回调
     */
    @SuppressWarnings("unchecked")
    public void postParam(String url,
            HashMap<String, String> data,
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
                HashMap<String, String> dataParam = (HashMap<String, String>) data.get(1);
                HashMap<String, String> headerParam = (HashMap<String, String>) data.get(2);
                return OkHttpClient.getInstance().postParam(url, dataParam, headerParam);
            }

            @Override
            public void failure(Exception e, String tag) {
                callback.failure(e, tag);
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
     * @return 结果
     * @throws IOException 网络错误
     */
    public String requestGet(String url, HashMap<String, String> data) throws IOException {
        return requestGet(url, data, new HashMap<>());
    }


    /******
     * 请求Post
     * @param url 地址
     * @param data 数据
     * @param header 请求头
     * @return 结果
     * @throws IOException 网络错误
     */
    public String requestGet(String url,
            HashMap<String, String> data,
            HashMap<String, String> header) throws IOException {
        // 创建HttpUrl.Builder对象
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();

        //添加所有的请求头
        for (String key : data.keySet()) {
            urlBuilder.addQueryParameter(key, Objects.requireNonNull(data.get(key)));
        }

        // 创建请求（Request）
        Request.Builder builder = new Request.Builder()
                .url(urlBuilder.build())
                .get();

        //添加所有的请求头
        for (String key : header.keySet()) {
            builder.addHeader(key, Objects.requireNonNull(header.get(key)));
        }

        //构建
        Request request = builder.build();

        // 发送请求并获取响应
        Response response = client.newCall(request).execute();

        // 判断请求是否成功
        if (!response.isSuccessful()) {
            assert response.body() != null;
            throw new IOException(response.body().string());
        }

        // 获取响应体的字符串
        assert response.body() != null;
        return response.body().string();
    }

    /******
     * 请求Post
     * @param url      请求地址
     * @param data     数据
     * @param callback 回调
     */
    public void requestGet(String url,
            HashMap<String, String> data,
            OkHttpAsyncCallback callback) {
        requestGet(url, data, new HashMap<>(), callback);
    }

    /******
     * 请求Post
     * @param url      请求地址
     * @param data     请求数据
     * @param header   请求Header
     * @param callback 回调
     */
    @SuppressWarnings("unchecked")
    public void requestGet(String url,
            HashMap<String, String> data,
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
                HashMap<String, String> dataParam = (HashMap<String, String>) data.get(1);
                HashMap<String, String> headerParam = (HashMap<String, String>) data.get(2);
                return OkHttpClient.getInstance().requestGet(url, dataParam, headerParam);
            }

            @Override
            public void failure(Exception e, String tag) {
                callback.failure(e, tag);
            }

            @Override
            public void success(String data, String tag) {
                callback.success(data, tag);
            }
        }, dataList);
    }

}
