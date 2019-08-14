package com.flappygo.flappyim.Tools;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.loader.content.CursorLoader;
import android.text.TextUtils;

/**
 * Created by yang on 2017/1/11.
 */
public class TakePicTool {


    /*******************
     * 解析获取图片
     *
     * @param context 上下文
     * @param data    数据data
     * @return
     */
    public static String getPicAnalyze(Activity context, Intent data) {
        if (Build.VERSION.SDK_INT >= 19) {
            String[] pojo = {MediaStore.Images.Media.DATA};
            Uri uri = data.getData();
            CursorLoader cursorLoader = new CursorLoader(context,
                    uri, pojo, null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();
            if (cursor != null) {
                cursor.moveToFirst();
                String path = cursor.getString(cursor
                        .getColumnIndex(pojo[0]));
                cursor.close();
                if (path != null && path.length() > 0) {
                    return path;
                } else {
                    return null;
                }
            }
        } else {
            Uri uri = data.getData();
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.managedQuery(uri, proj, null,
                    null, null);
            if (cursor != null) {
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String path = cursor.getString(column_index);
                try {
                    if (Build.VERSION.SDK_INT < 14) {
                        cursor.close();
                    }
                } catch (Exception e) {

                }
                if (path == null || TextUtils.isEmpty(path)) {
                    return null;
                } else {
                    return path;
                }
            }
        }
        return null;
    }


}
