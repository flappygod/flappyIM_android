package com.flappygo.flappyim.ApiServer.Tools;


import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonToken;
import com.google.gson.TypeAdapter;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/******
 * json快速转换工具
 */

public class GsonTool {

    ///GSON对象
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Integer.class, new IntegerTypeAdapter())
            .registerTypeAdapter(Double.class, new DoubleTypeAdapter())
            .registerTypeAdapter(String.class, new StringTypeAdapter())
            .setDateFormat("yyyy-MM-dd HH:mm:ss").create();


    /********
     * 数组字符串转换为list
     * @param str 数组字符串
     * @return 字符串List
     */
    public static List<String> listJsonArrayStr(String str) {
        if (str == null || str.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return gson.fromJson(str, new TypeToken<List<String>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            return new ArrayList<>();
        }
    }

    /********
     * list字符串转换为数组字符串
     * @param strList  list字符串
     * @return json字符串
     */
    public static String jsonArrayListStr(List<String> strList) {
        if (strList == null || strList.isEmpty()) {
            return "[]";
        }
        return gson.toJson(strList);
    }


    /*****************
     * 將json數組转换为对象
     *
     * @param jsonArray 数组
     * @param cls       对象
     * @param <T>       泛型
     * @return 对象列表
     */

    public static <T> List<T> jsonStrToModels(JSONArray jsonArray, Class<T> cls) {
        return jsonStrToModels(jsonArray.toString(), cls);
    }

    /*****************
     * 將json數組转换为对象
     * @param jsonArray 数组
     * @param cls       对象
     * @param <T>       泛型
     * @return 对象数组
     */
    public static <T> List<T> jsonStrToModels(String jsonArray, Class<T> cls) {
        if (jsonArray == null || jsonArray.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return gson.fromJson(jsonArray, TypeToken.getParameterized(List.class, cls).getType());
        } catch (JsonSyntaxException e) {
            return new ArrayList<>();
        }
    }


    /******
     * 將json对象转换为实体对象
     * @param jsonStr json对象
     * @param tClass        class
     * @param <T>        泛型
     * @return 对象
     */
    /**
     * 将 JSON 字符串转换为指定类型的对象
     *
     * @param jsonStr JSON 字符串
     * @param tClass  目标对象的类型
     * @param <T>     泛型
     * @return 转换后的对象，如果转换失败或输入为空，则返回 null
     */
    public static <T> T jsonStrToModel(String jsonStr, Class<T> tClass) {
        // 检查输入是否为空
        if (jsonStr == null || jsonStr.isEmpty()) {
            return null;
        }
        //如果目标类型是 String，直接返回原始字符串
        if (tClass == String.class) {
            @SuppressWarnings("unchecked")
            T result = (T) jsonStr;
            return result;
        }
        //使用Gson解析JSON 字符串
        try {
            return gson.fromJson(jsonStr, tClass);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }


    /*************
     * 将实体对象转换为字符串
     *
     * @param t   对象
     * @param <T> 泛型
     * @return 字符串
     */
    public static <T> String modelToJsonStr(T t) {
        if (t == null) {
            return null;
        }
        return gson.toJson(t);
    }

    /**************
     * 将列表对象转换为字符串
     * @param t  对象列表
     * @param <T>  泛型
     * @return 字符串
     */
    public static <T> String modelsToJsonStr(List<T> t) {
        if (t == null || t.isEmpty()) {
            return "[]";
        }
        //使用Gson直接将列表序列化为JSON数组字符串
        return gson.toJson(t);
    }


    /******
     * 列表转换
     * @param data  A列表
     * @param bClass B类型
     * @return B列表
     * @param <A> 列表A
     * @param <B> 列表B
     */
    public static <A, B> List<B> listA2B(List<A> data, Class<B> bClass) {
        if (data == null || data.isEmpty()) {
            return new ArrayList<>();
        }
        List<B> result = new ArrayList<>(data.size());
        for (A item : data) {
            B converted = gson.fromJson(gson.toJson(item), bClass);
            result.add(converted);
        }
        return result;
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

///int类型
class IntegerTypeAdapter extends TypeAdapter<Integer> {
    @Override
    public void write(JsonWriter out, Integer value) throws IOException {
        out.value(value);
    }

    @Override
    public Integer read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return 0;
        }
        if (in.peek() == JsonToken.STRING) {
            String str = in.nextString();
            if (str.isEmpty()) {
                return 0;
            }
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return in.nextInt();
    }
}

///爽精度类型
class DoubleTypeAdapter extends TypeAdapter<Double> {
    @Override
    public void write(JsonWriter out, Double value) throws IOException {
        out.value(value);
    }

    @Override
    public Double read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return 0.0;
        }
        if (in.peek() == JsonToken.STRING) {
            String str = in.nextString();
            if (str.isEmpty()) {
                return 0.0;
            }
            try {
                return Double.parseDouble(str);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return in.nextDouble();
    }
}

class StringTypeAdapter extends TypeAdapter<String> {

    @Override
    public void write(JsonWriter out, String value) throws IOException {
        // 自定义序列化逻辑
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value);
        }
    }

    @Override
    public String read(JsonReader in) throws IOException {
        //自定义反序列化逻辑
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        return in.nextString();
    }
}
