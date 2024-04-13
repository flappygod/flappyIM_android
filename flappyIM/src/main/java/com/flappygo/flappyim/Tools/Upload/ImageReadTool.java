package com.flappygo.flappyim.Tools.Upload;


import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapFactory;

import java.io.File;

/*****
 * 图像读取工具
 */
public class ImageReadTool {



    /*********
     * 判断文件是否存在
     * @param path 判断文件是否存在而且不是文件夹
     * @return 是否存在
     */
    public static boolean isFileExistsAntNotDic(String path) {
        try {
            File file = new File(path);
            return file.exists() && !file.isDirectory();
        } catch (Exception e) {
            return false;
        }
    }


    /******
     * 获取图片的大小数据
     * @param path 大小
     * @return 图像大小
     */
    public synchronized static ImageReadWH getImageSize(String path) throws Exception {
        if (isFileExistsAntNotDic(path)) {
            // 创建设置
            Options options = new Options();
            // 设置inJustDecodeBounds为true后，decodeFile并不分配空间，此时计算原始图片的长度和宽度
            options.inJustDecodeBounds = true;
            // 取得参数
            BitmapFactory.decodeFile(path, options);
            // 高度
            int imageHeight = options.outHeight;
            // 宽度
            int imageWidth = options.outWidth;
            // 宽高
            return new ImageReadWH(imageWidth, imageHeight);
        } else {
            throw new Exception("file not exists or is dictionary");
        }
    }


}
