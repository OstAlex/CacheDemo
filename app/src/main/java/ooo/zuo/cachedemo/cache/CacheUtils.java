package ooo.zuo.cachedemo.cache;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;


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
    public static boolean putString(String key, String value) {
        if (key==null||value==null){
            return false;
        }
        return ACache.get(BaseApplication.getJDApplication()).put(key,value);
    }

    public static boolean putString(String key,String value,long startTime){
        if (key == null || value == null) {
            return false;
        }
        return ACache.get(BaseApplication.getJDApplication()).put(key,startTime,value);
    }

    /**
     * 保存 String数据 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的String数据
     * @param liveTime 保存的时间，单位：秒
     */
    public static boolean putString(String key, String value, int liveTime) {
        return putString(key,value,System.currentTimeMillis(),liveTime);
    }

    public static boolean putString(String key,String value,long startTime,int liveTime){
        if (key==null||value==null){
            return false;
        }
        return ACache.get(BaseApplication.getJDApplication()).put(key,value,startTime,liveTime*1000);
    }

    public static boolean putStringWithExtendTime(String key,String value,int liveTime){
        return putStringWithExtendTime(key, value,System.currentTimeMillis(), liveTime);
    }
    public static boolean putStringWithExtendTime(String key,String value,long startTime,int liveTime){
        if (key==null||value==null){
            return false;
        }
        return ACache.get(BaseApplication.getJDApplication()).putWithExtendTime(key,value,startTime,liveTime*1000);
    }

    /**
     * 读取 String数据
     *
     * @param key
     * @return 读取String数据
     */
    @Nullable
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
    public static boolean putByte(String key, byte[] value) {
        if (key == null || value == null) {
            return false;
        }
        return ACache.get(BaseApplication.getJDApplication()).put(key, value);
    }

    public static boolean putByte(String key, byte[] value,long startTime) {
        if (key == null || value == null) {
            return false;
        }
        return ACache.get(BaseApplication.getJDApplication()).put(key, startTime,value);
    }
    /**
     * 保存 byte数据 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的byte数据
     * @param liveTime 保存的时间，单位：秒
     */
    public static boolean putByte(String key, byte[] value, int liveTime) {
        if (key == null || value == null) {
            return false;
        }
        return ACache.get(BaseApplication.getJDApplication()).put(key, value, liveTime * 1000);
    }

    /**
     * 保存byte数据到缓存，存活期间每次访问将延长存活时间
     * @param liveTime  存活时间/延长时间
     */
    public static boolean putByteWithExtendTime(String key,byte[] value,int liveTime){
        if (key == null || value == null) {
            return false;
        }
        return ACache.get(BaseApplication.getJDApplication()).putWithExtendTime(key, value, liveTime * 1000);
    }

    /**
     * 保存byte数据到缓存，存活期间每次访问将延长存活时间
     * @param startTime 开始生效期间
     * @param liveTime 存活时间/延长时间
     */
    public static boolean putByteWithExtendTime(String key,byte[] value,long startTime,int liveTime){
        if (key == null || value == null) {
            return false;
        }
        return ACache.get(BaseApplication.getJDApplication()).putWithExtendTime(key, value, startTime,liveTime * 1000);
    }

    /**
     * 保存 byte数据 到 缓存中
     *
     * @param key 保存的key
     * @return byte数据
     */
    @Nullable
    public static byte[] getByte(String key) {
        return ACache.get(BaseApplication.getJDApplication()).getAsBinary(key);
    }

    /**
     * 保存 bitmap 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的 bitmap 数据
     */
    public static boolean putBitmap(String key, Bitmap value) {
        if (key==null||value==null){
            return false;
        }
        return ACache.get(BaseApplication.getJDApplication()).put(key, value);
    }

    public static boolean putBitmap(String key, Bitmap value,long startTime) {
        if (key==null||value==null){
            return false;
        }
        return ACache.get(BaseApplication.getJDApplication()).put(key,startTime, value);
    }
    /**
     * 保存 bitmap 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的 bitmap 数据
     * @param liveTime 保存的时间，单位：秒
     */
    public static boolean putBitmap(String key, Bitmap value, int liveTime) {
        if (key==null||value==null){
            return false;
        }
        return ACache.get(BaseApplication.getJDApplication()).put(key, value, liveTime * 1000);
    }

    public static boolean putBitmap(String key, Bitmap value,long startTime, int liveTime) {
        if (key==null||value==null){
            return false;
        }
        return ACache.get(BaseApplication.getJDApplication()).put(key, value, startTime,liveTime * 1000);
    }

    public static boolean putBitmapWithExtendTime(String key,Bitmap value,int liveTime){
        return putBitmapWithExtendTime(key, value, System.currentTimeMillis(),liveTime);
    }

    public static boolean putBitmapWithExtendTime(String key,Bitmap value,long startTime,int liveTime){
        if (key==null||value==null){
            return false;
        }
        return ACache.get(BaseApplication.getJDApplication()).putWithExtendTime(key,value,startTime,liveTime*1000);
    }

    /**
     * 读取 bitmap 数据
     *
     * @param key 保存的key
     * @return bitmap 数据
     */
    @Nullable
    public static Bitmap getBitmap(String key) {
        return ACache.get(BaseApplication.getJDApplication()).getAsBitmap(key);
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
