/**
 * Copyright (c) 2012-2013, Michael Yang 杨福海 (www.yangfuhai.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ooo.zuo.cachedemo.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Michael Yang（www.yangfuhai.com） update at 2013.08.07
 */
public class ACache {
    private static final String TAG = "ACache";
    public static final long TIME_HOUR = 60 * 60 * 1000;
    public static final long TIME_DAY = TIME_HOUR * 24;
    private static int MAX_SIZE = 1000 * 1000 * 50; // 50 mb
    private static final int MAX_COUNT = Integer.MAX_VALUE; // 不限制存放数据的数量
    private static Map<String, ACache> mInstanceMap = new HashMap<String, ACache>();
    private ACacheManager mCache;
    private static String mCachePath;
    private static String mCacheName = "cache";

    private static final String BYTE = "byte";
    private static final String STRING = "string";
    private static final String BITMAP = "bitmap";

    /**
     * 初始化
     *
     * @param cachePath 缓存路径
     * @param cacheName 缓存文件夹名称
     */
    public static void init(String cachePath, String cacheName) {
        mCachePath = cachePath;
        mCacheName = cacheName;
    }

    /**
     * 初始化
     *
     * @param cachePath 缓存路径
     * @param cacheName 缓存文件夹名称
     * @param maxSize   最大缓存大小
     */
    public static void init(String cachePath, String cacheName, int maxSize) {
        mCachePath = cachePath;
        mCacheName = cacheName;
        MAX_SIZE = maxSize;
    }

    public static ACache get(Context ctx) {
        return get(ctx, mCacheName);
    }

    public static ACache get(Context ctx, String cacheName) {
        File f = new File(mCachePath, cacheName);
        return get(f, MAX_SIZE, MAX_COUNT);
    }

    public static ACache get(File cacheDir) {
        return get(cacheDir, MAX_SIZE, MAX_COUNT);
    }

    public static ACache get(Context ctx, long max_zise, int max_count) {
        File f = new File(mCachePath, "cache");
        return get(f, max_zise, max_count);
    }

    public static ACache get(File cacheDir, long max_zise, int max_count) {
        ACache manager = mInstanceMap.get(cacheDir.getAbsoluteFile() + myPid());
        if (manager == null) {
            manager = new ACache(cacheDir, max_zise, max_count);
            mInstanceMap.put(cacheDir.getAbsolutePath() + myPid(), manager);
        }
        return manager;
    }

    private static String myPid() {
        return "_" + android.os.Process.myPid();
    }

    private ACache(File cacheDir, long max_size, int max_count) {
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            throw new RuntimeException("can't make dirs in " + cacheDir.getAbsolutePath());
        }
        mCache = new ACacheManager(cacheDir, max_size, max_count);
    }

    // =======================================
    // ============ String数据 读写 ==============
    // =======================================

    /**
     * 保存 String数据 到 缓存中 立即生效，永久有效
     *
     * @param key   保存的key
     * @param value 保存的String数据
     */
    public boolean put(String key, String value) {
        return put(key, System.currentTimeMillis(), value);
    }

    /**
     * 保存 String数据 到 缓存中 在startTime后生效，永久有效
     *
     * @param key   保存的key
     * @param value 保存的String数据
     */
    public boolean put(String key, long startTime, String value) {
        try {
            return put(key, value.getBytes("UTF-8"), startTime, STRING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 保存 String数据 到 缓存中 立即生效,存活liveTime时间
     *
     * @param key      保存的key
     * @param value    保存的String数据
     * @param liveTime 保存的时间，单位 ms
     */
    public boolean put(String key, String value, long liveTime) {
        return put(key, value, System.currentTimeMillis(), liveTime);
    }

    /**
     * 保存 String数据 到缓存中 在开始时间后生效，存活liveTime时间。
     *
     * @param key       保存的Key
     * @param value     保存的String数据
     * @param startTime 缓存开始生效的时间 单位ms
     * @param liveTime  缓存的生效时长 单位ms
     * @return true 缓存成功  false 缓存失败
     */
    public boolean put(String key, String value, long startTime, long liveTime) {
        try {
            return put(key, value.getBytes("UTF-8"), startTime, liveTime, STRING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 保存 String数据 到缓存中， 立即生效，在生效期内每次访问延长存活时间
     *
     * @param key      保存的key
     * @param value    保存的数据
     * @param liveTime 存活时间/延长时间 单位 ms
     * @return true 缓存成功  false 缓存失败
     */
    public boolean putWithExtendTime(String key, String value, long liveTime) {
        return putWithExtendTime(key, value, System.currentTimeMillis(), liveTime);
    }

    /**
     * 保存 String数据 到缓存中，在startTime开始生效，生效时间内每次访问将延长缓存的存活时间。
     *
     * @param key       保存的key
     * @param value     保存的数据
     * @param startTime 缓存开始生效的时间  单位ms
     * @param liveTime  缓存存活时间/延长存活时间, 单位ms
     * @return true 缓存成功  false 缓存失败
     */
    public boolean putWithExtendTime(String key, String value, long startTime, long liveTime) {
        try {
            return putWithExtendTime(key, value.getBytes("UTF-8"), startTime, liveTime, STRING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 读取 String数据
     *
     * @param key 缓存的key
     * @return String 数据,如果key所对应的数据不存在、非存活时期、数据类型不是String 将返回null
     */
    @Nullable
    public String getAsString(String key) {
        byte[] bytes = getBytes(key, STRING);
        if (bytes == null) {
            return null;
        }
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param key
     * @return 将返回文件最后一个访问的时间. -1表示缓存不存在
     */
    public long getAsCacheTime(String key) {
        File file = file(key);
        File cacheInfoFile = mCache.cacheInfoFile(key);
        if (file != null && cacheInfoFile.exists()) {
            CacheInfo cacheInfo = mCache.readCacheInfo(cacheInfoFile);
            if (cacheInfo != null && Utils.isAlive(cacheInfo)) {
                return cacheInfo.lastVisitTime;
            }
        } else if (file != null) {
            long lastModified = file.lastModified();
            return lastModified == 0 ? -1 : lastModified;
        }
        return -1;
    }


    // =======================================
    // ============== byte 数据 读写 =============
    // =======================================

    /**
     * 保存 byte数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的数据
     */
    public boolean put(String key, byte[] value) {
        return put(key, System.currentTimeMillis(), value);
    }

    public boolean put(String key, long startTime, byte[] value) {
        return put(key, value, startTime, BYTE);
    }

    public boolean put(String key, byte[] value, long liveTime) {
        return put(key, value, System.currentTimeMillis(), liveTime);
    }

    public boolean put(String key, byte[] value, long startTime, long liveTime) {
        return put(key, value, startTime, liveTime, BYTE);
    }


    public boolean putWithExtendTime(String key, byte[] value, long liveTime) {
        return putWithExtendTime(key, value, System.currentTimeMillis(), liveTime);
    }

    public boolean putWithExtendTime(String key, byte[] value, long startTime, long liveTime) {
        return putWithExtendTime(key, value, startTime, liveTime, BYTE);
    }

    /**
     * 保存 byte数据 到缓存中，立即生效，永久有效
     *
     * @param key      保存的key
     * @param value    保存的数据
     * @param dataType 数据类型
     * @return true 缓存成功  false 缓存失败
     */
    private boolean put(String key, byte[] value, String dataType) {
        return put(key, value, System.currentTimeMillis(), 0, dataType, LiveType.NORMAL);
    }

    /**
     * 保存 byte数据 到缓存中，可以设置缓存的生效时间
     *
     * @param key       保存的key
     * @param value     保存的数据
     * @param startTime 缓存生效的时间 单位ms
     * @param dataType  数据类型
     * @return true 缓存成功  false 缓存失败
     */
    private boolean put(String key, byte[] value, long startTime, String dataType) {
        return put(key, value, startTime, 0, dataType, LiveType.NORMAL);
    }

    /**
     * 保存 byte数据 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的数据
     * @param liveTime 保存的时间 单位ms
     */
    private boolean put(String key, byte[] value, String dataType, long liveTime) {
        return put(key, value, System.currentTimeMillis(), liveTime, dataType, LiveType.ONCE);
    }

    /**
     * 保存 byte 数据到 缓存中
     *
     * @param key       保存的Key
     * @param value     保存的数据
     * @param startTime 生效的开始时间 单位ms
     * @param liveTime  生效时长 单位ms
     * @param dataType  数据类型
     * @return true 缓存成功 false 缓存失败
     */
    public boolean put(String key, byte[] value, long startTime, long liveTime, String dataType) {
        return put(key, value, startTime, liveTime, dataType, LiveType.ONCE);
    }


    /**
     * 保存 byte数据 到缓存中，默认生效时间为当前时间
     *
     * @param key      保存的key
     * @param value    保存的数据
     * @param liveTime 生效时长 单位ms
     * @param dataType 数据类型
     * @return true 缓存成功  false 缓存失败
     */
    public boolean putWithExtendTime(String key, byte[] value, long liveTime, String dataType) {
        return put(key, value, System.currentTimeMillis(), liveTime, dataType, LiveType.REFRESH_TIME);
    }

    /**
     * 保存 byte数据 到缓存中
     *
     * @param key       保存的key
     * @param value     保存的数据
     * @param startTime 缓存的生效时间
     * @param liveTime  生效时长 单位ms
     * @param dataType  数据类型
     * @return true 缓存成功  false 缓存失败
     */
    public boolean putWithExtendTime(String key, byte[] value, long startTime, long liveTime, String dataType) {
        return put(key, value, startTime, liveTime, dataType, LiveType.REFRESH_TIME);
    }

    /**
     * 保存 byte数据 到缓存中
     *
     * @param key       保存的key
     * @param value     保存的数据
     * @param startTime 缓存生效时间
     * @param liveTime  生效时长
     * @param type      数据类型
     * @param liveType  生效类型
     *                  LiveType.NORMAL        :  普通缓存，没有失效时间。
     *                  LiveType.ONCE          :  有失效时间。
     *                  liveType.REFRESH_TIME  :  每次访问延长生效时间
     * @return true 缓存成功 false 缓存失败
     */
    public boolean put(String key, byte[] value, long startTime, long liveTime, String type, int liveType) {
        File file = mCache.cacheFile(key);
        File infoFile = mCache.cacheInfoFile(key);
        if (Utils.createFile(file) && Utils.createFile(infoFile)) {
            boolean writeFile = mCache.writeFile(file, value);
            if (!writeFile) {
                Log.e(TAG, "put: write cache file failed");
                return false;
            }
            mCache.put(file);

            long currentTimeMillis = System.currentTimeMillis();

            CacheInfo info = new CacheInfo();
            info.fileType = type;
            info.createTime = currentTimeMillis;
            info.lastVisitTime = currentTimeMillis;
            info.liveTime = liveTime;
            info.liveType = liveType;
            info.effectiveTime = startTime;
            info.expiryTime = startTime + liveTime;
            mCache.writeCacheInfoFile(infoFile, info);

            return true;
        }
        return false;
    }

    public byte[] getAsBinary(String key) {
        return getBytes(key, "byte");
    }

    /**
     * 获取 byte 数据
     *
     * @param key
     * @return byte 数据
     */
    private byte[] getBytes(String key, String dataType) {
        File cacheFile = mCache.cacheFile(key);
        File infoFile = mCache.cacheInfoFile(key);
        if (!cacheFile.exists()) {
            mCache.remove(key);
            return null;
        }

        if (infoFile.exists()) {
            CacheInfo cacheInfo = mCache.readCacheInfo(infoFile);
            if (cacheInfo == null) {
                return null;
            } else if (!Utils.isAlive(cacheInfo)) {
                if (Utils.outOfDate(cacheInfo)){
                    Log.d(TAG, "getAsBinary: "+key+" is out of date !");
                    remove(key);
                }
                return null;
            } else if (!TextUtils.equals(dataType, cacheInfo.fileType)) {
                Log.e(TAG, "getBytes: dateType is not matched!! request:"+dataType+" found:"+cacheInfo.fileType);
                return null;
            }
            long currentTimeMillis = System.currentTimeMillis();
            cacheInfo.lastVisitTime = currentTimeMillis;
            if (cacheInfo.liveType==LiveType.REFRESH_TIME){
                cacheInfo.expiryTime = currentTimeMillis + cacheInfo.liveTime;
            }
            mCache.writeCacheInfoFile(infoFile, cacheInfo);
        }

        byte[] bytes = mCache.readFile(cacheFile);
        if (Utils.hasDateInfo(bytes)) { //是否是老数据
            if (Utils.isDue(bytes)) {
                remove(key);
                return null;
            }
            bytes = Utils.clearDateInfo(bytes);
        }
        return bytes;
    }

    // =======================================
    // ============== bitmap 数据 读写 =============
    // =======================================

    /**
     * 保存 bitmap 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的bitmap数据
     */
    public boolean put(String key, Bitmap value) {
        return put(key, Utils.Bitmap2Bytes(value), BITMAP);
    }

    /**
     * 保存 bitmap 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的 bitmap 数据
     * @param liveTime 保存的时间，单位 ms
     */
    public boolean put(String key, Bitmap value, long liveTime) {
        return put(key, Utils.Bitmap2Bytes(value), System.currentTimeMillis(), liveTime, BITMAP);
    }

    public boolean put(String key, long startTime, Bitmap value) {
        return put(key, Utils.Bitmap2Bytes(value), startTime, BITMAP);
    }

    public boolean put(String key, Bitmap value, long startTime, long liveTime) {
        return put(key, Utils.Bitmap2Bytes(value), startTime, liveTime, BITMAP);
    }

    /**
     * 将 bitmap 保存到 缓存中，立即生效，在存活周期内每次访问将延长存活时间
     *
     * @param key      保存的key
     * @param value    保存的bitmap
     * @param liveTime 存活时间
     * @return true 缓存成功  false 缓存失败
     */
    public boolean putWithExtendTime(String key, Bitmap value, long liveTime) {
        return putWithExtendTime(key, value, System.currentTimeMillis(), liveTime);
    }


    /**
     * 将 bitmap 保存到 缓存中，将在startTime后生效，在存活周期内，每次访问将延长存活时间
     *
     * @param key      保存的key
     * @param value    保存的bitmap
     * @param liveTime 存活时间
     * @return true 缓存成功  false 缓存失败
     */
    public boolean putWithExtendTime(String key, Bitmap value, long startTime, long liveTime) {
        return put(key, Utils.Bitmap2Bytes(value), startTime, liveTime, BITMAP);
    }

    /**
     * 读取 bitmap 数据
     *
     * @param key
     * @return bitmap 数据
     */
    public Bitmap getAsBitmap(String key) {
        byte[] bytes = getBytes(key, BITMAP);
        if (bytes == null) {
            return null;
        }
        return Utils.Bytes2Bimap(bytes);
    }

    // =======================================
    // ============= drawable 数据 读写 =============
    // =======================================

    /**
     * 保存 drawable 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的drawable数据
     */
    public boolean put(String key, Drawable value) {
        return put(key, Utils.drawable2Bitmap(value));
    }

    public boolean put(String key, long startTime, Drawable value) {
        return put(key, Utils.drawable2Bitmap(value), startTime);
    }

    public boolean put(String key, Drawable value, long liveTime) {
        return put(key, value, System.currentTimeMillis(), liveTime);
    }

    public boolean put(String key, Drawable value, long startTime, long liveTime) {
        return put(key, Utils.drawable2Bitmap(value), startTime, liveTime);
    }

    public boolean putWithExtendTime(String key, Drawable value, long liveTime) {
        return putWithExtendTime(key, value, System.currentTimeMillis(), liveTime);
    }

    public boolean putWithExtendTime(String key, Drawable value, long startTime, long liveTime) {
        return put(key, Utils.drawable2Bitmap(value), startTime, liveTime);
    }

    /**
     * 读取 Drawable 数据
     *
     * @param key
     * @return Drawable 数据
     */
    @Nullable
    public Drawable getAsDrawable(String key) {
        byte[] bytes = getAsBinary(key);
        if (bytes == null) {
            return null;
        }
        return Utils.bitmap2Drawable(Utils.Bytes2Bimap(bytes));
    }

    /**
     * 获取缓存文件
     *
     * @param key
     * @return value 缓存的文件
     */
    @Nullable
    public File file(String key) {
        File f = mCache.cacheFile(key);
        if (f.exists())
            return f;
        return null;
    }

    /**
     * 移除某个key
     *
     * @param key
     * @return 是否移除成功
     */
    public boolean remove(String key) {
        return mCache.remove(key);
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        mCache.clear();
    }

    /**
     * @author 杨福海（michael） www.yangfuhai.com
     * @version 1.0
     * @title 缓存管理器
     */
    public class ACacheManager {
        private final AtomicLong cacheSize;
        private final AtomicInteger cacheCount;
        private final long sizeLimit;
        private final int countLimit;
        private final Map<File, Long> lastUsageDates = Collections.synchronizedMap(new HashMap<File, Long>());
        protected File cacheDir;

        private ACacheManager(File cacheDir, long sizeLimit, int countLimit) {
            this.cacheDir = cacheDir;
            this.sizeLimit = sizeLimit;
            this.countLimit = countLimit;
            cacheSize = new AtomicLong();
            cacheCount = new AtomicInteger();
            calculateCacheSizeAndCacheCount();
        }

        /**
         * 计算 cacheSize和cacheCount
         */
        private void calculateCacheSizeAndCacheCount() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int size = 0;
                    int count = 0;
                    File[] cachedFiles = cacheDir.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return !name.endsWith(".info");
                        }
                    });
                    if (cachedFiles != null) {
                        for (File cachedFile : cachedFiles) {
                            size += calculateSize(cachedFile);
                            count += 1;
                            lastUsageDates.put(cachedFile, cachedFile.lastModified());
                        }
                        cacheSize.set(size);
                        cacheCount.set(count);
                    }
                }
            }).start();
        }

        private void put(File file) {
            int curCacheCount = cacheCount.get();
            while (curCacheCount + 1 > countLimit) {
                long freedSize = removeNext();
                cacheSize.addAndGet(-freedSize);

                curCacheCount = cacheCount.addAndGet(-1);
            }
            cacheCount.addAndGet(1);

            long valueSize = calculateSize(file);
            long curCacheSize = cacheSize.get();
            while (curCacheSize + valueSize > sizeLimit) {
                long freedSize = removeNext();
                curCacheSize = cacheSize.addAndGet(-freedSize);
            }
            cacheSize.addAndGet(valueSize);

            Long currentTime = System.currentTimeMillis();
            lastUsageDates.put(file, currentTime);
        }


        private File cacheFile(String key) {
            File file = new File(cacheDir, key.hashCode() + "");
            lastUsageDates.put(file, System.currentTimeMillis());
            return file;
        }

        @NonNull
        private File cacheInfoFile(String key) {
            return new File(cacheDir, key.hashCode() + ".info");
        }

        @Nullable
        private CacheInfo readCacheInfo(File file) {
            byte[] bytes = readFile(file);
            if (bytes == null) {
                return null;
            }
            try {
                return Utils.convertToCacheInfo(new String(bytes, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Nullable
        private byte[] readFile(File file) {
            if (file == null || !file.exists()) {
                return null;
            }
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] bytes = new byte[4096];
                int read = inputStream.read(bytes);
                while (read > -1) {
                    byteArrayOutputStream.write(bytes, 0, read);
                    read = inputStream.read(bytes);
                }
                inputStream.close();
                return byteArrayOutputStream.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        private boolean writeFile(File file, byte[] data) {
            if (file == null || data == null) {
                return false;
            }
            try {
                if (!file.exists() && !file.createNewFile()) {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file);
                outputStream.write(data);
                outputStream.flush();
                outputStream.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return false;
        }


        private boolean writeCacheInfoFile(File file, CacheInfo cacheInfo) {
            try {
                return writeFile(file, Utils.toJsonString(cacheInfo).getBytes("UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        private boolean remove(String key) {
            File file = cacheFile(key);
            File infoFile = cacheInfoFile(key);
            if (infoFile.exists()) {
                infoFile.delete();
            }
            return !file.exists() || file.delete();
        }

        private void clear() {
            lastUsageDates.clear();
            cacheSize.set(0);
            File[] files = cacheDir.listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
        }

        /**
         * 移除旧的文件
         *
         * @return
         */
        private long removeNext() {
            if (lastUsageDates.isEmpty()) {
                return 0;
            }

            Long oldestUsage = null;
            File mostLongUsedFile = null;
            Set<Entry<File, Long>> entries = lastUsageDates.entrySet();
            synchronized (lastUsageDates) {
                for (Entry<File, Long> entry : entries) {
                    if (mostLongUsedFile == null) {
                        mostLongUsedFile = entry.getKey();
                        oldestUsage = entry.getValue();
                    } else {
                        Long lastValueUsage = entry.getValue();
                        if (lastValueUsage < oldestUsage) {
                            oldestUsage = lastValueUsage;
                            mostLongUsedFile = entry.getKey();
                        }
                    }
                }
            }

            long fileSize = calculateSize(mostLongUsedFile);
            if (mostLongUsedFile.delete()) {
                File infoFile = new File(mostLongUsedFile.getParentFile(), mostLongUsedFile.getName() + ".info");
                if (infoFile.exists()) {
                    infoFile.delete();
                }
                lastUsageDates.remove(mostLongUsedFile);
            }
            return fileSize;
        }

        private long calculateSize(File file) {
            return file.length();
        }
    }

    /**
     * @author 杨福海（michael） www.yangfuhai.com
     * @version 1.0
     * @title 时间计算工具类
     */
    private static class Utils {

        private static boolean createFile(File file) {
            boolean ret = false;
            try {
                ret = file.exists() || file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ret;
        }

        private static boolean isAlive(CacheInfo cacheInfo) {
            if (cacheInfo == null) {
                return false;
            }
            long currentTimeMillis = System.currentTimeMillis();
            if (cacheInfo.liveType == LiveType.NORMAL) {
                return cacheInfo.effectiveTime <= currentTimeMillis;
            } else if (cacheInfo.liveType == LiveType.ONCE) {
                return cacheInfo.effectiveTime <= currentTimeMillis && cacheInfo.expiryTime >= currentTimeMillis;
            } else if (cacheInfo.liveType == LiveType.REFRESH_TIME) {
                return cacheInfo.effectiveTime <= currentTimeMillis && cacheInfo.expiryTime >= currentTimeMillis;
            }
            return false;
        }

        private static boolean outOfDate(CacheInfo cacheInfo) {
            return cacheInfo == null || System.currentTimeMillis() > cacheInfo.expiryTime;
        }

        /**
         * 判断缓存的byte数据是否到期
         *
         * @param data
         * @return true：到期了 false：还没有到期
         */
        private static boolean isDue(byte[] data) {
            String[] strs = getDateInfoFromDate(data);
            if (strs != null && strs.length == 2) {
                String saveTimeStr = strs[0];
                while (saveTimeStr.startsWith("0")) {
                    saveTimeStr = saveTimeStr.substring(1, saveTimeStr.length());
                }
                long saveTime = Long.valueOf(saveTimeStr);
                long deleteAfter = Long.valueOf(strs[1]);
                if (System.currentTimeMillis() > saveTime + deleteAfter * 1000) {
                    return true;
                }
            }
            return false;
        }


        private static byte[] clearDateInfo(byte[] data) {
            if (hasDateInfo(data)) {
                return copyOfRange(data, indexOf(data, mSeparator) + 1, data.length);
            }
            return data;
        }

        private static boolean hasDateInfo(byte[] data) {
            return data != null && data.length > 15 && data[13] == '-' && indexOf(data, mSeparator) > 14;
        }

        private static String[] getDateInfoFromDate(byte[] data) {
            if (hasDateInfo(data)) {
                String saveDate = new String(copyOfRange(data, 0, 13));
                String deleteAfter = new String(copyOfRange(data, 14, indexOf(data, mSeparator)));
                return new String[]{saveDate, deleteAfter};
            }
            return null;
        }

        private static int indexOf(byte[] data, char c) {
            for (int i = 0; i < data.length; i++) {
                if (data[i] == c) {
                    return i;
                }
            }
            return -1;
        }

        private static byte[] copyOfRange(byte[] original, int from, int to) {
            int newLength = to - from;
            if (newLength < 0)
                throw new IllegalArgumentException(from + " > " + to);
            byte[] copy = new byte[newLength];
            System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
            return copy;
        }

        private static final char mSeparator = ' ';

        /*
         * Bitmap → byte[]
         */
        private static byte[] Bitmap2Bytes(Bitmap bm) {
            if (bm == null) {
                return null;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return baos.toByteArray();
        }

        /*
         * byte[] → Bitmap
         */
        private static Bitmap Bytes2Bimap(byte[] b) {
            if (b.length == 0) {
                return null;
            }
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }

        /*
         * Drawable → Bitmap
         */
        private static Bitmap drawable2Bitmap(Drawable drawable) {
            if (drawable == null) {
                return null;
            }
            // 取 drawable 的长宽
            int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();
            // 取 drawable 的颜色格式
            Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
            // 建立对应 bitmap
            Bitmap bitmap = Bitmap.createBitmap(w, h, config);
            // 建立对应 bitmap 的画布
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, w, h);
            // 把 drawable 内容画到画布中
            drawable.draw(canvas);
            return bitmap;
        }

        /*
         * Bitmap → Drawable
         */
        @SuppressWarnings("deprecation")
        private static Drawable bitmap2Drawable(Bitmap bm) {
            if (bm == null) {
                return null;
            }
            BitmapDrawable bd = new BitmapDrawable(bm);
            bd.setTargetDensity(bm.getDensity());
            return new BitmapDrawable(bm);
        }

        private static String toJsonString(CacheInfo cacheInfo) {
            JSONObject json = new JSONObject();
            try {
                json.put("fileType", cacheInfo.fileType);
                json.put("createTime", cacheInfo.createTime);
                json.put("liveType", cacheInfo.liveType);
                json.put("liveTime", cacheInfo.liveTime);
                json.put("lastVisitTime", cacheInfo.lastVisitTime);
                json.put("effectiveTime", cacheInfo.effectiveTime);
                json.put("expiryTime", cacheInfo.expiryTime);
                return json.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "";
        }

        private static CacheInfo convertToCacheInfo(String s) {
            CacheInfo cacheInfo;
            try {
                JSONObject json = new JSONObject(s);
                cacheInfo = new CacheInfo();
                cacheInfo.fileType = json.getString("fileType");
                cacheInfo.createTime = json.getLong("createTime");
                cacheInfo.liveType = json.getInt("liveType");
                cacheInfo.liveTime = json.getLong("liveTime");
                cacheInfo.lastVisitTime = json.getLong("lastVisitTime");
                cacheInfo.effectiveTime = json.getLong("effectiveTime");
                cacheInfo.expiryTime = json.getLong("expiryTime");
            } catch (JSONException e) {
                e.printStackTrace();
                cacheInfo = null;
            }
            return cacheInfo;
        }
    }

    static class CacheInfo {
        String fileType;
        long createTime;
        long liveTime;
        int liveType;
        long lastVisitTime;
        long effectiveTime;
        long expiryTime;

        @Override
        public String toString() {
            return "{" +
                    "fileType='" + fileType + '\'' +
                    ", createTime=" + createTime +
                    ", liveTime=" + liveTime +
                    ", liveType=" + liveType +
                    ", lastVisitTime=" + lastVisitTime +
                    ", effectiveTime=" + effectiveTime +
                    ", expiryTime=" + expiryTime +
                    '}';
        }
    }

    static class LiveType {
        public static final int NORMAL = -1;
        public static final int ONCE = 0;
        public static final int REFRESH_TIME = 1;
    }

}
