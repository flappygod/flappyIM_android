package com.flappygo.flappyim.Tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/******
 * 视频工具类
 */
public class VideoTool {


    /******
     * 视频信息类
     */
    public static class VideoInfo {
        private String width;
        private String height;
        private String overPath;
        private String duration;


        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getOverPath() {
            return overPath;
        }

        public void setOverPath(String overPath) {
            this.overPath = overPath;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }
    }


    /******
     * 获取视频信息
     * @param context  上下文
     * @param maxWidHeight 最大高度
     * @param path  路径
     * @return 视频信息疏浚
     */
    public static VideoInfo getVideoInfo(Context context, int maxWidHeight, String path) throws Exception {
        //创建视频信息
        VideoInfo info = new VideoInfo();
        //解析视频
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        //设置文件路径
        retriever.setDataSource(path);
        //获取时长
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        //获取第一帧
        Bitmap bitmap = retriever.getFrameAtTime();
        //设置时长
        info.setDuration(duration);
        //设置
        if (bitmap != null) {
            //宽度
            int maxWidth = bitmap.getWidth();
            //高度
            int maxHeight = bitmap.getHeight();
            //设置宽高
            info.setWidth(maxWidth + "");
            //设置宽高
            info.setHeight(maxHeight + "");
            //裁剪大小
            if (maxWidth > maxHeight) {
                if (maxWidth > maxWidHeight) {
                    maxWidth = maxWidHeight;
                }
                maxHeight = (int) (maxWidth * (bitmap.getHeight() * 1.0f / bitmap.getWidth()));
            } else {
                if (maxHeight > maxWidHeight) {
                    maxHeight = maxWidHeight;
                }
                maxWidth = (int) (maxHeight * (bitmap.getWidth() * 1.0f / bitmap.getHeight()));
            }
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, maxWidth, maxHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        //创建一个临时地址
        String temPath = VideoTool.generateTempImagePath(context);
        //保存临时文件
        saveBitmapAsPng(bitmap, new File(temPath));
        //设置保存地址
        info.setOverPath(temPath);
        //释放
        retriever.release();
        //返回信息
        return info;
    }


    //保存到本地
    public static void saveBitmapAsPng(Bitmap bmp, File file) throws IOException {
        //创建新的文件
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);
        bmp.compress(Bitmap.CompressFormat.PNG, 80, out);
        out.flush();
        out.close();
    }

    //创建一个图片临时文件路径用于保存图片
    public static String generateTempImagePath(Context context) {
        String str = getDefaultDirPath(context);
        //如果没有路径就创建路径
        File file = new File(str);
        if (!file.exists()) {
            file.mkdirs();
        }
        return str + System.currentTimeMillis() + ".png";
    }

    //地址
    private static String getDefaultDirPath(Context context) {
        String cachePath = null;
        try {
            if (context.getExternalCacheDir() != null) {
                cachePath = context.getExternalCacheDir().getPath() + "/imagecache/";
            } else if (context.getCacheDir() != null) {
                cachePath = context.getCacheDir().getPath() + "/imagecache/";
            }
        } catch (Exception e) {
            cachePath = "/imagecache/";
        }
        return cachePath == null ? "" : cachePath;
    }
}
