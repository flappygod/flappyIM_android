package com.flappygo.flappyim.ApiServer.Tools;


import com.flappygo.flappyim.Tools.StringTool;
import com.google.gson.reflect.TypeToken;

import java.util.stream.Collectors;
import java.lang.reflect.Type;

import org.json.JSONException;
import com.google.gson.Gson;

import org.json.JSONObject;
import java.util.ArrayList;

import org.json.JSONArray;
import com.google.gson.*;

import java.util.List;

/******
 * Gson工具类
 */
public class GsonTool {


    /******
     * GSON对象
     */
    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();


    /**
     * 将JSONArray转换为指定类型对象的List集合。
     *
     * @param jsonArray JSONArray对象，包含了要转换的JSON数据。
     * @param cls       目标对象的Class类型。
     * @param <T>       泛型参数，表示目标对象的类型。
     * @return 转换后的对象List集合，如果转换失败则返回null。
     */
    public static <T> List<T> jsonArrToModels(JSONArray jsonArray, Class<T> cls) {
        try {
            List<T> rs = new ArrayList<T>();
            for (int s = 0; s < jsonArray.length(); s++) {
                T t = gson.fromJson(jsonArray.getJSONObject(s).toString(), cls);
                rs.add(t);
            }
            return rs;
        } catch (JSONException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 将JSON数组字符串转换为指定类型的实体对象列表。
     *
     * @param <T>       泛型类型参数，表示列表中对象的类型。
     * @param jsonArray 要转换的JSON数组字符串。
     * @param cls       目标对象的类类型。
     * @return 转换后的实体对象列表，如果jsonArray为空或转换失败则返回空列表。
     */
    public static <T> List<T> jsonStrToModels(String jsonArray, Class<T> cls) {
        if (StringTool.isEmpty(jsonArray)) {
            return new ArrayList<>();
        }
        try {
            Type listType = TypeToken.getParameterized(List.class, cls).getType();
            return gson.fromJson(jsonArray, listType);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 将JSONObject转换为指定类型的实体对象。
     *
     * @param <T>        泛型类型参数，表示目标对象的类型。
     * @param jsonObject 要转换的JSONObject实例。
     * @param cls        目标对象的类类型。
     * @return 转换后的实体对象，如果jsonObject为null或转换失败则返回null。
     */
    public static <T> T jsonObjToModel(JSONObject jsonObject, Class<T> cls) {
        if (jsonObject == null) {
            throw new IllegalArgumentException("JSONObject cannot be null");
        }
        try {
            return gson.fromJson(jsonObject.toString(), cls);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将JSON字符串转换为指定类型的实体对象。
     *
     * @param <T>     泛型类型参数，表示目标对象的类型。
     * @param jsonStr 要转换的JSON字符串。
     * @param cls     目标对象的类类型。
     * @return 转换后的实体对象，如果jsonStr为空或转换失败则返回null。
     */
    @SuppressWarnings("unchecked")
    public static <T> T jsonStrToModel(String jsonStr, Class<T> cls) {
        if (cls == String.class) {
            return (T) jsonStr;
        }
        try {
            return gson.fromJson(jsonStr, cls);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将提供的实体对象转换为JSON字符串。
     *
     * @param <T> 泛型类型参数，表示对象的类型。
     * @param t   要转换为JSON字符串的实体对象。
     * @return 表示实体对象的JSON字符串。
     */
    public static <T> String modelToJsonStr(T t) {
        return gson.toJson(t);
    }


    /**
     * 将提供的列表对象转换为JSON字符串。
     *
     * @param <T>  泛型类型参数，表示列表中的对象类型。
     * @param list 要转换为JSON字符串的列表对象。
     * @return 表示列表对象的JSON字符串。
     */
    public static <T> String modelToJsonStr(List<T> list) {
        return gson.toJson(list);
    }


    /******
     * 列表复制
     * @param dataList  数据源
     * @param aClass    需要的数据
     * @return 转换后的列表
     * @param <A>  转换后的对象
     * @param <B>  转换前的对象
     */
    public static <A, B> List<A> listB2A(List<B> dataList, Class<A> aClass) {
        return jsonStrToModels(modelToJsonStr(dataList), aClass);
    }

    /******
     * 对象转换
     * @param modelA  需要转换的对象
     * @param bClass  转换为的对象
     * @return 转换后的对象
     * @param <B>  转换后的对象
     */
    public static <B> B modelA2B(Object modelA, Class<B> bClass) {
        return gson.fromJson(gson.toJson(modelA), bClass);
    }

    /********
     * 数组字符串转换为list
     * @param str 数组字符串
     * @return 字符串列表
     */
    public static List<String> listJsonArrayStr(String str) {
        try {
            JSONArray array = new JSONArray(str);
            List<String> arrayList = new ArrayList<>();
            for (int s = 0; s < array.length(); s++) {
                arrayList.add((String) array.get(s));
            }
            return arrayList;
        } catch (Exception e) {
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

}
