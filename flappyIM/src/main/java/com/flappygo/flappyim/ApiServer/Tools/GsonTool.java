package com.flappygo.flappyim.ApiServer.Tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/30.
 */

public class GsonTool {


    private static Gson gson;


    /********
     * 数组字符串转换为list
     * @param str 数组字符串
     * @return
     */
    public static List<String> listJsonArrayStr(String str) {
        try {
            JSONArray array = new JSONArray(str);
            List<String> strs = new ArrayList<>();
            for (int s = 0; s < array.length(); s++) {
                strs.add((String) array.get(s));
            }
            return strs;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /********
     * list字符串转换为数组字符串
     * @param strs  list字符串
     * @return
     */
    public static String jsonArrayListStr(List<String> strs) {
        JSONArray array = new JSONArray();
        for (int s = 0; s < strs.size(); s++) {
            array.put(strs.get(s));
        }
        return array.toString();
    }


    /*****************
     * 將json數組转换为对象
     *
     * @param jsonArray 数组
     * @param cls       对象
     * @param <T>       泛型
     * @return
     */
    public static <T> List jsonArrayToModels(JSONArray jsonArray, Class<T> cls) {
        try {
            if (gson == null) {
                gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            }
            List rs = new ArrayList();
            for (int s = 0; s < jsonArray.length(); s++) {
                T t = gson.fromJson(jsonArray.getJSONObject(s).toString(), cls);
                rs.add(t);
            }
            return rs;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*****************
     * 將json數組转换为对象
     *
     * @param jsonArray 数组
     * @param cls       对象
     * @param <T>       泛型
     * @return
     */
    public static <T> List jsonArrayToModels(String jsonArray, Class<T> cls) {
        try {
            if (gson == null) {
                gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            }
            JSONArray array = new JSONArray(jsonArray);
            List rs = new ArrayList();
            for (int s = 0; s < array.length(); s++) {
                T t = gson.fromJson(array.getJSONObject(s).toString(), cls);
                rs.add(t);
            }
            return rs;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    /******************
     * 將json对象转换为实体对象
     *
     * @param jsonObject json对象
     * @param cls        class
     * @param <T>        泛型
     * @return
     */
    public static <T> T jsonObjectToModel(JSONObject jsonObject, Class<T> cls) {
        if (gson == null) {
            gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        }

        T t = gson.fromJson(jsonObject.toString(), cls);
        return t;
    }

    /******************
     * 將json对象转换为实体对象
     *
     * @param jsonObject json对象
     * @param cls        class
     * @param <T>        泛型
     * @return
     */
    public static <T> T jsonObjectToModel(String jsonObject, Class<T> cls) {
        if (gson == null) {
            gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        }
        return gson.fromJson(jsonObject, cls);
    }


    /*************
     * 将实体对象转换为字符串
     *
     * @param t   对象
     * @param cls 对象类型
     * @param <T> 泛型
     * @return
     */
    public static <T> String modelToString(T t, Class<T> cls) {
        if (gson == null) {
            gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        }
        String json = gson.toJson(t, cls);
        return json;
    }

    /**************
     * 将列表对象转换为字符串
     * @param t  对象列表
     * @param cls  class
     * @param <T>  fanxing
     * @return
     */
    public static <T> String modelToString(List<T> t, Class<T> cls) {
        if (gson == null) {
            gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        }
        JSONArray array = new JSONArray();
        for (int s = 0; s < t.size(); s++) {
            try {
                array.put(new JSONObject(gson.toJson(t.get(s), cls)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array.toString();
    }


    public static <A, B> List<B> listA2B(List<A> data, Class<A> aClass, Class<B> bClass) {
        return jsonArrayToModels(modelToString(data, aClass), bClass);
    }

    public static <B> B modelA2B(Object modelA, Class<B> bClass) {
        if (gson == null) {
            gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        }
        String gsonA = gson.toJson(modelA);
        B instanceB = gson.fromJson(gsonA, bClass);
        return instanceB;
    }
}
