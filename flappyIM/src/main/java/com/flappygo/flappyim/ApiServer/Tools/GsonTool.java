package com.flappygo.flappyim.ApiServer.Tools;

import com.flappygo.flappyim.Tools.StringTool;
import com.google.gson.GsonBuilder;

import org.json.JSONException;

import com.google.gson.Gson;

import java.util.ArrayList;

import org.json.JSONObject;
import org.json.JSONArray;

import java.util.List;

/******
 * json快速转换工具
 */

public class GsonTool {

    /******
     * GSON对象
     */
    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();


    /********
     * 数组字符串转换为list
     * @param str 数组字符串
     * @return 字符串List
     */
    public static List<String> listJsonArrayStr(String str) {
        try {
            JSONArray array = new JSONArray(str);
            List<String> strList = new ArrayList<>();
            for (int s = 0; s < array.length(); s++) {
                strList.add((String) array.get(s));
            }
            return strList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /********
     * list字符串转换为数组字符串
     * @param strList  list字符串
     * @return json字符串
     */
    public static String jsonArrayListStr(List<String> strList) {
        JSONArray array = new JSONArray();
        if (strList == null || strList.isEmpty()) {
            return array.toString();
        }
        for (int s = 0; s < strList.size(); s++) {
            array.put(strList.get(s));
        }
        return array.toString();
    }


    /*****************
     * 將json數組转换为对象
     *
     * @param jsonArray 数组
     * @param cls       对象
     * @param <T>       泛型
     * @return 对象列表
     */
    public static <T> ArrayList<T> jsonArrayToModels(JSONArray jsonArray, Class<T> cls) {
        try {
            ArrayList<T> rs = new ArrayList<T>();
            for (int s = 0; s < jsonArray.length(); s++) {
                rs.add(gson.fromJson(jsonArray.getJSONObject(s).toString(), cls));
            }
            return rs;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*****************
     * 將json數組转换为对象
     * @param jsonArray 数组
     * @param cls       对象
     * @param <T>       泛型
     * @return 对象数组
     */
    public static <T> List<T> jsonArrayToModels(String jsonArray, Class<T> cls) {
        try {
            JSONArray array = new JSONArray(jsonArray);
            ArrayList<T> rs = new ArrayList<T>();
            for (int s = 0; s < array.length(); s++) {
                rs.add(gson.fromJson(array.getJSONObject(s).toString(), cls));
            }
            return rs;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    /******
     * 將json对象转换为实体对象
     *
     * @param jsonObject json对象
     * @param cls        class
     * @param <T>        泛型
     * @return 对象
     */
    public static <T> T jsonStringToModel(JSONObject jsonObject, Class<T> cls) {
        if (jsonObject == null) {
            return null;
        }
        return gson.fromJson(jsonObject.toString(), cls);
    }

    /******
     * 將json对象转换为实体对象
     *
     * @param jsonStr json对象
     * @param cls        class
     * @param <T>        泛型
     * @return 对象
     */
    public static <T> T jsonStringToModel(String jsonStr, Class<T> cls) {
        if (StringTool.isEmpty(jsonStr)) {
            return null;
        }
        return gson.fromJson(jsonStr, cls);
    }


    /*************
     * 将实体对象转换为字符串
     *
     * @param t   对象
     * @param cls 对象类型
     * @param <T> 泛型
     * @return 字符串
     */
    public static <T> String modelToString(T t, Class<T> cls) {
        if (t == null) {
            return null;
        }
        return gson.toJson(t, cls);
    }

    /**************
     * 将列表对象转换为字符串
     * @param t  对象列表
     * @param cls  class
     * @param <T>  泛型
     * @return 字符串
     */
    public static <T> String modelToString(List<T> t, Class<T> cls) {
        JSONArray array = new JSONArray();
        if (t == null || t.isEmpty()) {
            return array.toString();
        }
        for (int s = 0; s < t.size(); s++) {
            try {
                array.put(new JSONObject(gson.toJson(t.get(s), cls)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array.toString();
    }


    /******
     * 列表转换
     * @param data  A列表
     * @param aClass A类型
     * @param bClass B类型
     * @return B列表
     * @param <A> 列表A
     * @param <B> 列表B
     */
    public static <A, B> List<B> listA2B(List<A> data, Class<A> aClass, Class<B> bClass) {
        return jsonArrayToModels(modelToString(data, aClass), bClass);
    }

    /******
     * 对象转换
     * @param modelA A对象
     * @param bClass B类
     * @return B对象
     * @param <B> B类型
     */
    public static <B> B modelA2B(Object modelA, Class<B> bClass) {
        return gson.fromJson(gson.toJson(modelA), bClass);
    }
}
