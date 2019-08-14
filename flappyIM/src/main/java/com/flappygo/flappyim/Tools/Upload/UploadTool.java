package com.flappygo.flappyim.Tools.Upload;

import android.graphics.Bitmap;
import android.os.Message;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Created by yang on 2016/7/28.
 */
public class UploadTool {

    //tag
    private static String TAG = "UploadTool";
    //读取超时时间
    private static int readTimeOut = 60 * 1000;
    //链接超时时间
    private static int connectTimeOut = 60 * 1000;
    //body类型
    private static String CONTENT_TYPE = "multipart/form-data";

    /****************
     * 上传文件
     *
     * @param urlPath     上传的路径
     * @param mParams     上传的参数
     * @param mFileParams 上传的文件
     * @return
     */
    public static String postImage(String urlPath,
                                   Map<String, Object> mParams,
                                   Map<String, String> mFileParams,
                                   LXImageWH lxImageWH) throws Exception {
        return postImage(urlPath,
                mParams,
                mFileParams,
                lxImageWH,
                null);
    }


    /********************
     * @param urlPath     上传的路径
     * @param mParams     上传的参数
     * @param mFileParams 上传的文件
     */
    public static String postImage(String urlPath,
                                   Map<String, Object> mParams,
                                   Map<String, String> mFileParams,
                                   LXImageWH lxImageWH,
                                   UploadHandler handler) throws Exception {
        // 前缀
        String PREFIX = "--";
        // 换行
        String LINE_END = "\r\n";
        // 边界标识
        String BOUNDARY = UUID.randomUUID().toString();
        //定义url
        URL url;
        //定义conenction
        HttpURLConnection connection;
        try {
            url = new URL(urlPath);
            //开启链接
            connection = (HttpURLConnection) url.openConnection();
            // 设置超时时间
            connection.setReadTimeout(readTimeOut);
            // 连接超时时间
            connection.setConnectTimeout(connectTimeOut);
            // 请求方式POST
            connection.setRequestMethod("POST");
            // XMLHttpRequest
            connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            // 开启输入流
            connection.setDoInput(true);
            // 开启输出流
            connection.setDoOutput(true);
            // 关闭缓存
            connection.setUseCaches(false);
            // 设置编码
            connection.setRequestProperty("Charset", "utf-8");
            // 保活
            connection.setRequestProperty("connection", "keep-alive");
            // 设置客户端的类型，可以省略
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            // 设置内容类型及定义BOUNDARY
            connection.setRequestProperty("Content-Type", "multipart/form-data" + ";boundary=" + BOUNDARY);
            // 获取输出流
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            // StringBuffer
            StringBuffer sb = null;
            // 返回值
            String result = "";
            // 发送param参数
            String paramStr;
            // 发送非文件参数
            if (mParams != null && mParams.size() > 0) {
                //遍历当前的参数param
                Iterator<String> it = mParams.keySet().iterator();
                //如果有
                while (it.hasNext()) {
                    //创建新的buffer
                    sb = new StringBuffer();
                    //获取下一项的key
                    String key = it.next();
                    //获取下一项的value
                    Object value = mParams.get(key);
                    //根据协议构建文本
                    sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
                    //根据协议构建文本
                    sb.append("Content-Disposition: form-data; name=\"")
                            .append(key).append("\"").append(LINE_END)
                            .append(LINE_END);
                    //根据协议构建文本
                    sb.append(value).append(LINE_END);
                    //转换为字符串
                    paramStr = sb.toString();
                    // 写给服务器
                    dos.write(paramStr.getBytes());
                    // 写给服务器
                    dos.flush();
                }
            }
            paramStr = null;
            // 发送文件参数，读取文件流写入post输出流
            if (mFileParams != null && !mFileParams.isEmpty()) {
                //迭代器
                Iterator<Map.Entry<String, String>> fileIter = mFileParams.entrySet().iterator();
                //如果有next
                while (fileIter.hasNext()) {
                    //创建新的buffer
                    sb = new StringBuffer();
                    //获取下一项
                    Map.Entry<String, String> entry = fileIter.next();
                    //获取key
                    String fileKey = entry.getKey();
                    //获取value
                    String filePath = entry.getValue();
                    //判断是否存在
                    File file = new File(filePath);
                    //不存在则报错
                    if (file.exists() == false) {
                        throw new FileNotFoundException();
                    }
                    // 设置边界标示，设置 Content-Disposition头传入文件流
                    sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
                    // 根据协议添加名称
                    sb.append("Content-Disposition:form-data; name=\""
                            + fileKey + "\"; filename=\"" + URLEncoder.encode(file.getName(), "utf-8")
                            + "\"" + LINE_END);
                    // 根据协议添加LINE_END
                    sb.append("Content-Type:" + CONTENT_TYPE + LINE_END);
                    // 根据协议添加LINE_END
                    sb.append(LINE_END);
                    // 写入文件相应信息
                    dos.write(sb.toString().getBytes());


                    //对上传的图片进行压缩处理
                    Bitmap bitmap = ImageReadTool.readFileBitmap(filePath, new LXImageReadOption(1024, 1024, false));
                    //图片数据转换
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    //压缩图片，防止过大
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    //宽度
                    lxImageWH.setWidth(bitmap.getWidth());
                    //高度
                    lxImageWH.setHeight(bitmap.getHeight());
                    //转换为流数据
                    InputStream is = new ByteArrayInputStream(baos.toByteArray());


                    //mem
                    byte[] bytes = new byte[1024];
                    //长度
                    long len = 0;
                    //整个写入
                    long totalWrite = 0;
                    //长度
                    long filelength = baos.size();
                    //写
                    while ((len = is.read(bytes)) != -1) {
                        //写输入
                        dos.write(bytes, 0, (int) len);
                        //总共长度根据写的计算
                        totalWrite += len;
                        //发送上传的消息出去
                        int progress = (int) ((totalWrite * 100 / filelength));
                        //以下是不可能出现的情况
                        if (progress > 100) {
                            progress = 100;
                        } else if (progress < 0) {
                            progress = 0;
                        }
                        //发送进度的handler
                        if (handler != null) {
                            //发送进度的handler信息通知
                            Message message = handler.obtainMessage(0);
                            message.arg1 = progress;
                            handler.sendMessage(message);
                        }
                    }
                    //关闭流
                    is.close();
                    //写入结尾
                    dos.write(LINE_END.getBytes());
                    dos.flush();
                }
                //写入结尾
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                //写入结尾
                dos.write(end_data);
                //写入结尾
                dos.flush();
            }
            dos.close();
            int res = connection.getResponseCode();
            // 返回成功
            if (res == 200) {
                //获取返回流
                InputStream input = connection.getInputStream();
                //转换为字符串
                StringBuffer retbuffer = new StringBuffer();
                //定义strByte
                int strByte;
                while ((strByte = input.read()) != -1) {
                    retbuffer.append((char) strByte);
                }
                //转换为字符串
                result = retbuffer.toString();
                //返回字符串
                return result;
            } else {
                //报错
                throw new Exception("can't connected:" + res);
            }
        } catch (MalformedURLException ex) {
            throw ex;
        } catch (IOException ex) {
            throw ex;
        }
    }


    /****************
     * 上传文件
     *
     * @param urlPath     上传的路径
     * @param mParams     上传的参数
     * @param mFileParams 上传的文件
     * @return
     */
    public static String postFile(String urlPath, Map<String, Object> mParams, Map<String, String> mFileParams) throws Exception {
        return postFile(urlPath, mParams, mFileParams, null);
    }


    /********************
     * @param urlPath     上传的路径
     * @param mParams     上传的参数
     * @param mFileParams 上传的文件
     */
    public static String postFile(String urlPath, Map<String, Object> mParams, Map<String, String> mFileParams, UploadHandler handler) throws Exception {
        // 前缀
        String PREFIX = "--";
        // 换行
        String LINE_END = "\r\n";
        // 边界标识
        String BOUNDARY = UUID.randomUUID().toString();
        //定义url
        URL url;
        //定义conenction
        HttpURLConnection connection;
        try {
            url = new URL(urlPath);
            //开启链接
            connection = (HttpURLConnection) url.openConnection();
            // 设置超时时间
            connection.setReadTimeout(readTimeOut);
            // 连接超时时间
            connection.setConnectTimeout(connectTimeOut);
            // 请求方式POST
            connection.setRequestMethod("POST");
            // XMLHttpRequest
            connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            // 开启输入流
            connection.setDoInput(true);
            // 开启输出流
            connection.setDoOutput(true);
            // 关闭缓存
            connection.setUseCaches(false);
            // 设置编码
            connection.setRequestProperty("Charset", "utf-8");
            // 保活
            connection.setRequestProperty("connection", "keep-alive");
            // 设置客户端的类型，可以省略
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            // 设置内容类型及定义BOUNDARY
            connection.setRequestProperty("Content-Type", "multipart/form-data" + ";boundary=" + BOUNDARY);
            // 获取输出流
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            // StringBuffer
            StringBuffer sb = null;
            // 返回值
            String result = "";
            // 发送param参数
            String paramStr;
            // 发送非文件参数
            if (mParams != null && mParams.size() > 0) {
                //遍历当前的参数param
                Iterator<String> it = mParams.keySet().iterator();
                //如果有
                while (it.hasNext()) {
                    //创建新的buffer
                    sb = new StringBuffer();
                    //获取下一项的key
                    String key = it.next();
                    //获取下一项的value
                    Object value = mParams.get(key);
                    //根据协议构建文本
                    sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
                    //根据协议构建文本
                    sb.append("Content-Disposition: form-data; name=\"")
                            .append(key).append("\"").append(LINE_END)
                            .append(LINE_END);
                    //根据协议构建文本
                    sb.append(value).append(LINE_END);
                    //转换为字符串
                    paramStr = sb.toString();
                    // 写给服务器
                    dos.write(paramStr.getBytes());
                    // 写给服务器
                    dos.flush();
                }
            }
            paramStr = null;
            // 发送文件参数，读取文件流写入post输出流
            if (mFileParams != null && !mFileParams.isEmpty()) {
                //迭代器
                Iterator<Map.Entry<String, String>> fileIter = mFileParams.entrySet().iterator();
                //如果有next
                while (fileIter.hasNext()) {
                    //创建新的buffer
                    sb = new StringBuffer();
                    //获取下一项
                    Map.Entry<String, String> entry = fileIter.next();
                    //获取key
                    String fileKey = entry.getKey();
                    //获取value
                    String filePath = entry.getValue();
                    //判断是否存在
                    File file = new File(filePath);
                    //不存在则报错
                    if (file.exists() == false) {
                        throw new FileNotFoundException();
                    }
                    // 设置边界标示，设置 Content-Disposition头传入文件流
                    sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
                    // 根据协议添加名称
                    sb.append("Content-Disposition:form-data; name=\""
                            + fileKey + "\"; filename=\"" + URLEncoder.encode(file.getName(), "utf-8")
                            + "\"" + LINE_END);
                    // 根据协议添加LINE_END
                    sb.append("Content-Type:" + CONTENT_TYPE + LINE_END);
                    // 根据协议添加LINE_END
                    sb.append(LINE_END);
                    // 写入文件相应信息
                    dos.write(sb.toString().getBytes());


                    File fileF = new File(filePath);
                    //转换为流数据
                    InputStream is = new FileInputStream(fileF);


                    //mem
                    byte[] bytes = new byte[1024];
                    //长度
                    long len = 0;
                    //整个写入
                    long totalWrite = 0;
                    //长度
                    long filelength = fileF.length();
                    //写
                    while ((len = is.read(bytes)) != -1) {
                        //写输入
                        dos.write(bytes, 0, (int) len);
                        //总共长度根据写的计算
                        totalWrite += len;
                        //发送上传的消息出去
                        int progress = (int) ((totalWrite * 100 / filelength));
                        //以下是不可能出现的情况
                        if (progress > 100) {
                            progress = 100;
                        } else if (progress < 0) {
                            progress = 0;
                        }
                        //发送进度的handler
                        if (handler != null) {
                            //发送进度的handler信息通知
                            Message message = handler.obtainMessage(0);
                            message.arg1 = progress;
                            handler.sendMessage(message);
                        }
                    }
                    //关闭流
                    is.close();
                    //写入结尾
                    dos.write(LINE_END.getBytes());
                    dos.flush();
                }
                //写入结尾
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                //写入结尾
                dos.write(end_data);
                //写入结尾
                dos.flush();
            }
            dos.close();
            int res = connection.getResponseCode();
            // 返回成功
            if (res == 200) {
                //获取返回流
                InputStream input = connection.getInputStream();
                //转换为字符串
                StringBuffer retbuffer = new StringBuffer();
                //定义strByte
                int strByte;
                while ((strByte = input.read()) != -1) {
                    retbuffer.append((char) strByte);
                }
                //转换为字符串
                result = retbuffer.toString();
                //返回字符串
                return result;
            } else {
                //报错
                throw new Exception("can't connected:" + res);
            }
        } catch (MalformedURLException ex) {
            throw ex;
        } catch (IOException ex) {
            throw ex;
        }
    }
}
