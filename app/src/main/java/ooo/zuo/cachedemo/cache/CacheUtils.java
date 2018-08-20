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
     * 保存 String数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的String数据
     */
    public static void putString(String key, String value) {
        putString(key, value,0);
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

    public static void putWithExtensibleTime(String key,String value,int liveTime){
        if (key==null||value==null){
            return;
        }
        ACache.get(BaseApplication.getJDApplication()).putWithExtensibleTime(key, value, liveTime);
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

    public static void put(String key,byte[] value,int liveTime){
        if (key==null||value==null){
            return;
        }
        ACache.get(BaseApplication.getJDApplication()).putWithExtensibleTime(key, value, liveTime);
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
