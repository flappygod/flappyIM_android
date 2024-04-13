package com.flappygo.flappyim.Service;

import com.flappygo.flappyim.Thread.NettyThreadListener;
import com.flappygo.flappyim.Models.Response.ResponseLogin;
import com.flappygo.flappyim.Holder.HolderLoginCallback;
import com.flappygo.flappyim.Handler.HandlerLogin;
import com.flappygo.flappyim.Thread.NettyThread;
import com.flappygo.flappyim.Tools.StringTool;


/******
 * 应用的Socket服务
 */
public class FlappySocketService {

    //线程
    private NettyThread clientThread;


    /******
     * 当前的服务实例
     */
    private static final class InstanceHolder {
        static final FlappySocketService instance = new FlappySocketService();
    }

    /******
     * 获取当前开启的服务
     * @return 单例
     */
    public static FlappySocketService getInstance() {
        return InstanceHolder.instance;
    }


    /******
     * 获取当前服务的线程
     * @return 服务线程
     */
    public NettyThread getClientThread() {
        synchronized (this) {
            return clientThread;
        }
    }

    /******
     * 获取当前是否是在线状态
     * @return 服务线程
     */
    public boolean isOnline() {
        NettyThread thread = getClientThread();
        return thread != null && thread.isConnected();
    }


    /******
     * 根据当前的信息重新连接
     * @param uuid                    UUID
     * @param loginResponse           登录回调
     * @param nettyThreadListener 死亡的监听
     */
    public void startConnect(String uuid, final ResponseLogin loginResponse, NettyThreadListener nettyThreadListener) {

        synchronized (this) {

            //参数不齐全不予处理
            if (uuid == null ||
                    loginResponse == null ||
                    loginResponse.getUser() == null ||
                    loginResponse.getServerIP() == null ||
                    loginResponse.getServerPort() == null) {
                return;
            }

            //之前的先下线
            if (clientThread != null) {
                clientThread.offline();
                clientThread = null;
            }

            //装饰登录
            HandlerLogin loginCallback = new HandlerLogin(
                    HolderLoginCallback.getInstance().getLoginCallBack(uuid),
                    loginResponse
            );


            //创建新的线程
            clientThread = new NettyThread(
                    //获取用户
                    loginResponse.getUser(),
                    //服务端的IP
                    loginResponse.getServerIP(),
                    //端口
                    StringTool.strToInt(loginResponse.getServerPort(), 11211),
                    //登录回调
                    loginCallback,
                    //回调
                    nettyThreadListener
            );

            //开始这个线程
            clientThread.start();
        }
    }


    /******
     * 下线了
     */
    public void offline() {
        synchronized (this) {
            if (clientThread != null) {
                clientThread.offline();
                clientThread = null;
            }
        }
    }

}
