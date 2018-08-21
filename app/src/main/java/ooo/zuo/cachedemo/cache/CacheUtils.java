package ooo.zuo.cachedemo.cache;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;

import ooo.zuo.cachedemo.BaseApplication;

/**
 * 缓存工具类
 * Created by xuhongwei on 17/12/19.
 */

public class CacheUtils {
    private static final String TAG = "CacheUtils";
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
//        ACache.get(BaseApplication.getJDApplication());
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
//        ACache.get(BaseApplication.getJDApplication());
    }
    /**
     * 保存 String数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的String数据
     */
    public static void putString(String key, String value) {
        if (key==null||value==null){
            return;
        }
        long t = System.currentTimeMillis();
        ACache.get(BaseApplication.getJDApplication()).put(key, value);
        Log.d(TAG, "putString: time:"+(System.currentTimeMillis()-t));
    }

    public static Map<String, String> getAllCacheAsString(){
        return ACache.get(BaseApplication.getJDApplication()).getAllCache();
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
        long t = System.currentTimeMillis();
        ACache.get(BaseApplication.getJDApplication()).put(key, value, saveTime);
        Log.d(TAG, "putString: time:"+(System.currentTimeMillis()-t));
    }

    public static void putWithExtensibleTime(String key,String value,int liveTime){
        if (key==null||value==null){
            return;
        }
        long t = System.currentTimeMillis();
        ACache.get(BaseApplication.getJDApplication()).putWithExtensibleTime(key, value, liveTime);
        Log.d(TAG, "putWithExtensibleTime: time:"+(System.currentTimeMillis()-t));
    }


    /**
     * 读取 String数据
     *
     * @param key
     * @return 读取String数据
     */
    public static String getString(String key) {
        long t = System.currentTimeMillis();
        String string = ACache.get(BaseApplication.getJDApplication()).getAsString(key);
        Log.d(TAG, "getString: time:"+(System.currentTimeMillis()-t));
        return string;
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
