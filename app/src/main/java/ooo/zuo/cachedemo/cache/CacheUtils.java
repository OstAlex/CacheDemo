package ooo.zuo.cachedemo.cache;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

import ooo.zuo.cachedemo.BaseApplication;

/**
 * 缓存工具类
 * Created by xuhongwei on 17/12/19.
 */

public class CacheUtils {
    public static final int TIME_MINUTE = 60;
    public static final int TIME_HOUR = TIME_MINUTE * 60;
    public static final int TIME_DAY = TIME_HOUR * 24;

    /**
     * 缓存初始化
     *
     * @param cachePath 缓存路径
     * @param cacheName 缓存文件夹名称
     */
    public static void init(String cachePath, String cacheName) {
        ACache.init(cachePath, cacheName);
    }

    /**
     * 缓存初始化
     *
     * @param cachePath 缓存路径
     * @param cacheName 缓存文件夹名称
     * @param maxSize   最大缓存大小
     */
    public static void init(String cachePath, String cacheName, int maxSize) {
        ACache.init(cachePath, cacheName, maxSize);
    }

    /**
     * 保存 JSONObject数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的JSONObject数据
     */
    public static void putJSONObject(String key, JSONObject value) {
        ACache.get(BaseApplication.getJDApplication()).put(key, value);
    }

    /**
     * 保存 JSONObject数据 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的JSONObject数据
     * @param saveTime 保存的时间，单位：秒
     */
    public static void putJSONObject(String key, JSONObject value, int saveTime) {
        ACache.get(BaseApplication.getJDApplication()).put(key, value, saveTime);
    }

    /**
     * 读取JSONObject数据
     *
     * @param key
     * @return JSONObject数据
     */
    public static JSONObject getJSONObject(String key) {
        return ACache.get(BaseApplication.getJDApplication()).getAsJSONObject(key);
    }

    /**
     * 保存 JSONArray 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的JSONArray数据
     */
    public static void putJSONArray(String key, JSONArray value) {
        ACache.get(BaseApplication.getJDApplication()).put(key, value);
    }

    /**
     * 保存 JSONArray 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的JSONArray数据
     * @param saveTime 保存的时间，单位：秒
     */
    public static void putJSONArray(String key, JSONArray value, int saveTime) {
        ACache.get(BaseApplication.getJDApplication()).put(key, value, saveTime);
    }

    /**
     * 读取JSONArray数据
     *
     * @param key
     * @return JSONArray数据
     */
    public static JSONArray getJSONArray(String key) {
        return ACache.get(BaseApplication.getJDApplication()).getAsJSONArray(key);
    }

    /**
     * 保存 String数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的String数据
     */
    public static void putString(String key, String value) {
        if (key == null || value == null) {
            return;
        }
        ACache.get(BaseApplication.getJDApplication()).put(key, value);
    }

    /**
     * 保存 String数据 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的String数据
     * @param saveTime 保存的时间，单位：秒
     */
    public static void putString(String key, String value, int saveTime) {
        if (key == null || value == null) {
            return;
        }
        ACache.get(BaseApplication.getJDApplication()).put(key, value, saveTime);
    }

    /**
     * 读取 String数据
     *
     * @param key
     * @return 读取String数据
     */
    public static String getString(String key) {
        return ACache.get(BaseApplication.getJDApplication()).getAsString(key);
    }

    /**
     * 返回缓存时间
     *
     * @param key
     * @return
     */
    public static long getCacheTime(String key) {
        return ACache.get(BaseApplication.getJDApplication()).getAsCacheTime(key);
    }


    /**
     * 保存 Serializable数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的Serializable数据
     */
    public static void putObject(String key, Serializable value) {
        ACache.get(BaseApplication.getJDApplication()).put(key, value);
    }

    /**
     * 保存 Serializable数据 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的Serializable数据
     * @param saveTime 保存的时间，单位：秒
     */
    public static void putObject(String key, Serializable value, int saveTime) {
        ACache.get(BaseApplication.getJDApplication()).put(key, value, saveTime);
    }

    /**
     * 读取 Serializable数据
     *
     * @param key 保存的key
     * @return Serializable数据
     */
    public static Object getObject(String key) {
        return ACache.get(BaseApplication.getJDApplication()).getAsObject(key);
    }

    /**
     * 保存 byte数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的byte数据
     */
    public static void putByte(String key, byte[] value) {
        if (key == null || value == null) {
            return;
        }
        ACache.get(BaseApplication.getJDApplication()).put(key, value);
    }

    /**
     * 保存 byte数据 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的byte数据
     * @param saveTime 保存的时间，单位：秒
     */
    public static void putByte(String key, byte[] value, int saveTime) {
        if (key == null || value == null) {
            return;
        }
        ACache.get(BaseApplication.getJDApplication()).put(key, value, saveTime);
    }

    /**
     * 保存 byte数据 到 缓存中
     *
     * @param key 保存的key
     * @return byte数据
     */
    public static byte[] getByte(String key) {
        return ACache.get(BaseApplication.getJDApplication()).getAsBinary(key);
    }

    /**
     * 保存 bitmap 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的 bitmap 数据
     */
    public static void putBitmap(String key, Bitmap value) {
        ACache.get(BaseApplication.getJDApplication()).put(key, value);
    }

    /**
     * 保存 bitmap 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的 bitmap 数据
     * @param saveTime 保存的时间，单位：秒
     */
    public static void putBitmap(String key, Bitmap value, int saveTime) {
        ACache.get(BaseApplication.getJDApplication()).put(key, value, saveTime);
    }

    /**
     * 读取 bitmap 数据
     *
     * @param key 保存的key
     * @return bitmap 数据
     */
    public static Bitmap getBitmap(String key) {
        return ACache.get(BaseApplication.getJDApplication()).getAsBitmap(key);
    }

    /**
     * 保存 drawable 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的 drawable 数据
     */
    public static void putDrawable(String key, Drawable value) {
        ACache.get(BaseApplication.getJDApplication()).put(key, value);
    }

    /**
     * 保存 drawable 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的 drawable 数据
     * @param saveTime 保存的时间，单位：秒
     */
    public static void putDrawable(String key, Drawable value, int saveTime) {
        ACache.get(BaseApplication.getJDApplication()).put(key, value, saveTime);
    }

    /**
     * 读取 drawable 数据
     *
     * @param key 保存的key
     * @return drawable 数据
     */
    public static Drawable getDrawable(String key) {
        return ACache.get(BaseApplication.getJDApplication()).getAsDrawable(key);
    }

    /**
     * 移除某个key
     *
     * @param key
     * @return 是否移除成功
     */
    public static boolean remove(String key) {
        return ACache.get(BaseApplication.getJDApplication()).remove(key);
    }

    /**
     * 清除所有数据
     */
    public static void clear() {
        ACache.get(BaseApplication.getJDApplication()).clear();
    }

}
