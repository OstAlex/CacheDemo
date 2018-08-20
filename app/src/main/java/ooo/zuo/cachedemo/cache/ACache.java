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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static ooo.zuo.cachedemo.cache.ACache.CacheDatabaseHelper.DatabaseName;

/**
 * @author Michael Yang（www.yangfuhai.com） update at 2013.08.07
 */
public class ACache {
    private static final String TAG = "ACache";


    public static final int TIME_HOUR = 60 * 60;
    public static final int TIME_DAY = TIME_HOUR * 24;
    private static int MAX_SIZE = 1000 * 1000 * 50; // 50 mb
    private static final int MAX_COUNT = Integer.MAX_VALUE; // 不限制存放数据的数量
    private static Map<String, ACache> mInstanceMap = new HashMap<String, ACache>();
    private ACacheManager mCache;
    private static String mCachePath;
    private static String mCacheName = "cache";

    /**
     * 初始化
     *
     * @param cachePath 缓存路径
     * @param cacheName 缓存文件夹名称
     */
    public static void init(String cachePath, String cacheName) {
        mCachePath = cachePath;
        mCacheName = cacheName;
        Log.d(TAG, "init: path:" + mCachePath + " name:" + mCacheName);
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
        Log.d(TAG, "init: path:" + mCachePath + " name:" + mCacheName);
    }

    public static ACache get(Context ctx) {
        return get(ctx, mCacheName);
    }

    public static ACache get(Context ctx, String cacheName) {
        File f = new File(mCachePath, cacheName);
        return get(ctx, f, MAX_SIZE, MAX_COUNT);
    }

    public static ACache get(Context context, File cacheDir) {
        return get(context, cacheDir, MAX_SIZE, MAX_COUNT);
    }

    public static ACache get(Context ctx, long max_zise, int max_count) {
        File f = new File(mCachePath, "cache");
        return get(ctx, f, max_zise, max_count);
    }

    public static ACache get(Context context, File cacheDir, long max_zise, int max_count) {
        ACache manager = mInstanceMap.get(cacheDir.getAbsoluteFile() + myPid());
        if (manager == null) {
            manager = new ACache(context, cacheDir, max_zise, max_count);
            mInstanceMap.put(cacheDir.getAbsolutePath() + myPid(), manager);
        }
        return manager;
    }

    private static String myPid() {
        return "_" + android.os.Process.myPid();
    }

    private ACache(Context context, File cacheDir, long max_size, int max_count) {
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            throw new RuntimeException("can't make dirs in " + cacheDir.getAbsolutePath());
        }
        mCache = new ACacheManager(context, cacheDir, max_size, max_count);
    }

    // =======================================
    // ============ String数据 读写 ==============
    // =======================================

    /**
     * 保存 String数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的String数据
     */
    public void put(String key, String value) {
        put(key, value.getBytes(Charset.forName("UTF-8")));
    }

    /**
     * 保存 String数据 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的String数据
     * @param saveTime 保存的时间，单位：秒
     */
    public void put(String key, String value, int saveTime) {
        put(key, value.getBytes(Charset.forName("UTF-8")), saveTime);
    }

    public void putWithExtensibleTime(String key, String value, int liveTime) {
        putWithExtensibleTime(key, value.getBytes(Charset.forName("UTF-8")), liveTime);
    }

    /**
     * 读取 String数据
     *
     * @param key
     * @return String 数据
     */
    public String getAsString(String key) {
        byte[] bytes = getAsBinary(key);
        if (bytes == null) {
            return null;
        }
        return new String(bytes, Charset.forName("UTF-8"));
    }

    public long getAsCacheTime(String key) {

        File file = file(key);
        if (file != null && file.exists()) {
            CacheModel model = mCache.cacheMap.get(file.getName());
            if (model != null) {
                return model.lastVisitTime;
            }
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
        return put(key, value, 0, LiveType.FOREVER);
    }

    public boolean put(String key, byte[] value, int liveTime) {
        return put(key, value, liveTime, LiveType.ONCE);
    }

    public boolean putWithExtensibleTime(String key, byte[] value, int extendTime) {
        return put(key, value, extendTime, LiveType.EXTEND_PER_VISIT);
    }

    /**
     * 保存 byte数据 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的数据
     * @param liveTime 保存的时间，单位：秒
     */
    private boolean put(String key, byte[] value, int liveTime, int liveType) {
        boolean ret = false;
        File file = mCache.newFile(key);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(value);
            ret = true;
        } catch (Exception e) {
            e.printStackTrace();
            ret = false;
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            CacheModel cacheModel = new CacheModel(file.getName());
            cacheModel.liveType = liveType;
            cacheModel.liveTime = liveTime * 1000;
            mCache.put(file, cacheModel);
        }
        return ret;
    }

    /**
     * 获取 byte 数据
     *
     * @param key
     * @return byte 数据
     */
    public byte[] getAsBinary(String key) {
        RandomAccessFile RAFile = null;
        boolean removeFile = false;
        try {
            File file = mCache.get(key);
            if (file == null) {
                return null;
            }
            if (!file.exists())
                return null;
            RAFile = new RandomAccessFile(file, "r");
            byte[] byteArray = new byte[(int) RAFile.length()];
            RAFile.read(byteArray);
            return byteArray;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (RAFile != null) {
                try {
                    RAFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取缓存文件
     *
     * @param key
     * @return value 缓存的文件
     */
    public File file(String key) {
        File f = mCache.newFile(key);
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

    public void updateCache() {
        mCache.createOrUpdateDatabase();
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
        private final ExecutorService executor;
        protected File cacheDir;
        private CacheDatabaseHelper helper;
        private ConcurrentHashMap<String, CacheModel> cacheMap = new ConcurrentHashMap<>();
        private Context context;

        private ACacheManager(Context context, File cacheDir, long sizeLimit, int countLimit) {
            this.context = context.getApplicationContext();
            this.cacheDir = cacheDir;
            this.sizeLimit = sizeLimit;
            this.countLimit = countLimit;
            cacheSize = new AtomicLong();
            cacheCount = new AtomicInteger();
            executor = Executors.newSingleThreadExecutor();
            helper = new CacheDatabaseHelper(context);
            createOrUpdateDatabase();
        }

        @Override
        protected void finalize() throws Throwable {
            try {
                if (helper != null) {
                    helper.close();
                }
            } finally {
                super.finalize();
            }
        }

        private void createOrUpdateDatabase() {
            String dbFile = mCachePath + File.separator + mCacheName + File.separator + DatabaseName;
            File db = new File(dbFile);
            if (!db.exists()) {
                // 转换旧数据
                Runnable task = new ConvertRunnable(cacheDir, helper);
                task.run();
//                executor.submit(task);
            }
            // 更新数据库
            UpdateDatabaseRunnable task = new UpdateDatabaseRunnable(cacheDir, helper);
            task.run();
//            executor.submit(task);

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    List<CacheModel> caches = helper.queryAll();
                    for (CacheModel cache : caches) {
                        cacheMap.put(cache.name, cache);
                    }
                    Log.d(TAG, "run: 内存缓存准备完成....");
                }
            };
            runnable.run();
//            executor.submit(runnable);

            calculateCacheSizeAndCacheCount();


        }

        /**
         * 计算 cacheSize和cacheCount
         */
        private void calculateCacheSizeAndCacheCount() {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    int size = 0;
                    int count = 0;
                    File[] cachedFiles = cacheDir.listFiles();
                    if (cachedFiles != null) {
                        for (File cachedFile : cachedFiles) {
                            String name = cachedFile.getName();
                            if (name.endsWith(".db") || name.endsWith(".db-journal")) {
                                continue;
                            }
                            size += calculateSize(cachedFile);
                            count += 1;
                            lastUsageDates.put(cachedFile, cachedFile.lastModified());
                        }
                        cacheSize.set(size);
                        cacheCount.set(count);
                    }
                    Log.d(TAG, "run: 缓存大小数量初始化完成.... ");
                }
            };
            runnable.run();
//            executor.submit(runnable);
        }

        private boolean put(File file, CacheModel cacheModel) {
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
            cacheModel.createTime = currentTime;
            cacheModel.lastVisitTime = currentTime;
            cacheModel.name = file.getName();
            insertOrUpdateDatabase(cacheModel);

            file.setLastModified(currentTime);
            lastUsageDates.put(file, currentTime);
            return true;
        }

        private void insertOrUpdateDatabase(final CacheModel cache) {
            cacheMap.put(cache.name, cache);
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    helper.insertOrUpdate(cache);

                }
            });
        }

        private void deleteFromDatabase(final String name) {
            cacheMap.remove(name);
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    int delete = helper.delete(name);
                }
            });
        }

        private void clearDatabase() {
            cacheMap.clear();
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    int clear = helper.clear();
                    Log.d(TAG, "run: clear database:" + clear);
                }
            });
        }

        private File get(String key) {
            File file = newFile(key);
            String name = file.getName();
            CacheModel model = cacheMap.get(name);
            Long currentTime = System.currentTimeMillis();
            if (model != null) {
                Log.d(TAG, "get: key:" + key + "->" + model);
                if (model.liveType == LiveType.ONCE) {
                    if (model.createTime + model.liveTime < currentTime) {
                        if (file.exists()) {
                            file.delete();
                        }
                        deleteFromDatabase(name);
                        return null;
                    }
                } else if (model.liveType == LiveType.EXTEND_PER_VISIT) {
                    if (model.lastVisitTime + model.liveTime < currentTime) {
                        if (file.exists()) {
                            file.delete();
                        }
                        deleteFromDatabase(name);
                        return null;
                    }
                }
                model.lastVisitTime = currentTime;
                insertOrUpdateDatabase(model);
            }
            file.setLastModified(currentTime);
            lastUsageDates.put(file, currentTime);
            return file;
        }

        private File newFile(String key) {
            return new File(cacheDir, key.hashCode() + "");
        }

        private boolean remove(String key) {
            File image = get(key);
            if (image == null) {
                return true;
            }
            deleteFromDatabase(image.getName());
            return image.delete();
        }

        private void clear() {
            lastUsageDates.clear();
            clearDatabase();
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
                deleteFromDatabase(mostLongUsedFile.getName());
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

        /**
         * 判断缓存的String数据是否到期
         *
         * @param str
         * @return true：到期了 false：还没有到期
         */
        private static boolean isDue(String str) {
            return isDue(str.getBytes());
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

        private static String clearDateInfo(String strInfo) {
            if (strInfo != null && hasDateInfo(strInfo.getBytes())) {
                strInfo = strInfo.substring(strInfo.indexOf(mSeparator) + 1, strInfo.length());
            }
            return strInfo;
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
    }

    class UpdateDatabaseRunnable implements Runnable {
        File cacheDir;
        CacheDatabaseHelper helper;

        public UpdateDatabaseRunnable(File cacheDir, CacheDatabaseHelper helper) {
            this.cacheDir = cacheDir;
            this.helper = helper;
        }

        @Override
        public void run() {
            List<CacheModel> caches = helper.queryAll();
            // 删除无效记录
            for (int i = 0; i < caches.size(); i++) {
                CacheModel cacheModel = caches.get(i);
                File file = new File(cacheDir, cacheModel.name);
                if (!file.exists()) {
                    int delete = helper.delete(cacheModel.name);
                    Log.d(TAG, "run: delete:" + delete);
                }
            }
            File[] files = cacheDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return !(name.endsWith(".db") || name.endsWith(".db-journal"));
                }
            });
            // 记录遗失缓存
            for (File file : files) {
                CacheModel model = helper.query(file.getName());
                if (model == null) {
                    CacheModel cache = new CacheModel(file.getName());
                    cache.liveType = LiveType.ONCE;
                    cache.createTime = System.currentTimeMillis();
                    cache.lastVisitTime = System.currentTimeMillis();
                    cache.liveTime = 0;
                    cache.size = file.length();
                    helper.insert(cache);
                }
            }
            Log.d(TAG, "run: 更新数据库完成.... ");
        }
    }

    /**
     * 处理旧缓存
     */
    class ConvertRunnable implements Runnable {
        File cacheDir;
        CacheDatabaseHelper helper;

        public ConvertRunnable(File cacheDir, CacheDatabaseHelper helper) {
            this.cacheDir = cacheDir;
            this.helper = helper;
        }

        @Override
        public void run() {
            // 没有数据库,整理旧缓存
            File[] cachedFiles = cacheDir.listFiles();
            List<CacheModel> caches = new ArrayList<>();
            if (cachedFiles != null) {
                for (File cachedFile : cachedFiles) {
                    try {
                        long createTime = System.currentTimeMillis();
                        long lastVisitTime = cachedFile.lastModified();
                        long expireTime = 0;
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        FileInputStream inputStream = new FileInputStream(cachedFile);
                        byte[] bytes = new byte[4096];
                        int read = inputStream.read(bytes);
                        while (read > -1) {
                            stream.write(bytes, 0, read);
                            read = inputStream.read(bytes);
                        }
                        inputStream.close();
                        byte[] data = stream.toByteArray();
                        if (Utils.hasDateInfo(data)) {
                            String[] date = Utils.getDateInfoFromDate(data);
                            if (date != null && date.length == 2) {
                                String saveTimeStr = date[0];
                                while (saveTimeStr.startsWith("0")) {
                                    saveTimeStr = saveTimeStr.substring(1, saveTimeStr.length());
                                }
                                createTime = Long.valueOf(saveTimeStr);
                                expireTime = Long.valueOf(date[1]);
                            }
                            data = Utils.clearDateInfo(data);

                            if (cachedFile.delete() && cachedFile.createNewFile()) {
                                FileOutputStream outputStream = new FileOutputStream(cachedFile);
                                outputStream.write(data);
                                outputStream.close();
                                CacheModel cache = new CacheModel(cachedFile.getName());
                                cache.createTime = createTime;
                                cache.lastVisitTime = lastVisitTime;
                                cache.liveTime = expireTime * 1000;
                                cache.size = data.length;
                                cache.liveType = LiveType.ONCE;
                                caches.add(cache);
                            }

                        } else {
                            CacheModel cache = new CacheModel(cachedFile.getName());
                            cache.createTime = createTime;
                            cache.liveTime = expireTime;
                            cache.lastVisitTime = lastVisitTime;
                            cache.size = cachedFile.length();
                            cache.liveType = LiveType.ONCE;
                            caches.add(cache);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            int updateCount = helper.updateAll(caches);
            Log.d(TAG, "run: 整理完成 ... updateCache Count:" + updateCount);
        }
    }

    /**
     * 缓存数据库
     */
    class CacheDatabaseHelper extends SQLiteOpenHelper {
        private static final int VERSION = 1;
        static final String DatabaseName = ".cache_database.db";
        private static final String TABLE_NAME = "cache";


        public CacheDatabaseHelper(Context context) {
            super(context, mCachePath + File.separator + mCacheName + File.separator + DatabaseName, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "create table " + TABLE_NAME + " ( " +
                    "name text," +
                    "size long," +
                    "createTime long," +
                    "liveTime long," +
                    "lastVisitTime long," +
                    "liveType int," +
                    "primary key(\"name\")" +
                    " )";
            db.beginTransaction();
            try {
                db.execSQL(sql);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }


        public int updateAll(Collection<CacheModel> caches) {
            Iterator<CacheModel> iterator = caches.iterator();
            int count = 0;
            while (iterator.hasNext()) {
                CacheModel cache = iterator.next();
                count += insertOrUpdate(cache) ? 1 : 0;
            }
            return count;
        }

        public boolean insertOrUpdate(CacheModel cache) {
            if (cache == null || TextUtils.isEmpty(cache.name)) {
                return false;
            }
            try {
                SQLiteDatabase db = getWritableDatabase();

                Cursor cursor = db.query(TABLE_NAME, new String[]{"name"}, "name = ?", new String[]{cache.name}, null, null, null);
                if (cursor == null) {
                    return insert(cache);
                } else {
                    int count = cursor.getCount();
                    cursor.close();
                    if (count > 0) {
                        return update(cache);
                    } else {
                        return insert(cache);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        public List<CacheModel> queryAll() {
            List<CacheModel> caches = new ArrayList<>();
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
            if (cursor == null) {
                return caches;
            }
            int count = cursor.getCount();
            if (count <= 0) {
                cursor.close();
                return caches;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                caches.add(toModel(cursor));
                cursor.moveToNext();
            }
            cursor.close();
            return caches;
        }

        @Nullable
        public CacheModel query(String name) {
            if (TextUtils.isEmpty(name)) {
                return null;
            }
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.query(TABLE_NAME, null, "name = ?", new String[]{name}, null, null, null);
            if (cursor == null) {
                return null;
            }
            if (cursor.getCount() <= 0) {
                cursor.close();
                return null;
            }
            cursor.moveToFirst();
            CacheModel cacheModel = toModel(cursor);
            cursor.close();
            return cacheModel;

        }

        private boolean update(CacheModel cache) {
            if (cache == null) {
                return false;
            }
            SQLiteDatabase db = getWritableDatabase();
            Cursor cursor = db.query(TABLE_NAME, new String[]{"name"}, "name = ?", new String[]{cache.name}, null, null, null);
            if (cursor == null) {
                return false;
            } else {
                int count = cursor.getCount();
                cursor.close();
                if (count <= 0) {
                    return false;
                }
                db.beginTransaction();
                int update = 0;
                try {
                    update = db.update(TABLE_NAME, toContentValues(cache), "name = ?", new String[]{cache.name});
                    db.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }
                return update > 0;
            }
        }

        boolean insert(CacheModel cache) {
            if (cache == null) {
                return false;
            }
            boolean success = false;
            SQLiteDatabase db = getWritableDatabase();
            db.beginTransaction();
            try {
                long diff = db.insert(TABLE_NAME, null, toContentValues(cache));
                if (diff > 0) {
                    success = true;
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
            return success;
        }

        private int clear() {
            int count = 0;
            SQLiteDatabase db = getWritableDatabase();
            try {
                count = db.delete(TABLE_NAME, null, null);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
            return count;
        }

        private int delete(String name) {
            if (TextUtils.isEmpty(name)) {
                return 0;
            }
            int ret = 0;
            SQLiteDatabase db = getWritableDatabase();
            db.beginTransaction();
            try {
                ret = db.delete(TABLE_NAME, "name = ?", new String[]{name});
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
            Log.d(TAG, "delete: " + name + " -> " + ret);
            return ret;
        }

        private CacheModel toModel(Cursor cursor) {
            CacheModel cache = new CacheModel();
            if (cursor != null) {
                cache.name = cursor.getString(cursor.getColumnIndex("name"));
                cache.createTime = cursor.getLong(cursor.getColumnIndex("createTime"));
                cache.liveTime = cursor.getLong(cursor.getColumnIndex("liveTime"));
                cache.size = cursor.getLong(cursor.getColumnIndex("size"));
                cache.lastVisitTime = cursor.getLong(cursor.getColumnIndex("lastVisitTime"));
                cache.liveType = cursor.getInt(cursor.getColumnIndex("liveType"));
            }
            return cache;
        }

        private ContentValues toContentValues(CacheModel cache) {
            ContentValues contentValues = new ContentValues();
            if (cache != null) {
                contentValues.put("name", cache.name);
                contentValues.put("size", cache.size);
                contentValues.put("createTime", cache.createTime);
                contentValues.put("liveTime", cache.liveTime);
                contentValues.put("lastVisitTime", cache.lastVisitTime);
                contentValues.put("liveType", cache.liveType);
            }
            return contentValues;
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    static class CacheModel {
        CacheModel() {

        }

        CacheModel(String name) {
            this.name = name;
        }

        String name;
        long size;
        long createTime;
        long liveTime;
        long lastVisitTime;
        /**
         * 0 ： 一次性有效期
         * 1 : 有效期内访问延长有效期
         */
        int liveType;

        @Override
        public String toString() {
            return "CacheModel{" +
                    "name='" + name + '\'' +
                    ", size=" + size +
                    ", createTime=" + createTime +
                    ", liveTime=" + liveTime +
                    ", lastVisitTime=" + lastVisitTime +
                    ", liveType=" + liveType +
                    '}';
        }
    }

    public static class LiveType {
        public static final int FOREVER = -1;
        public static final int ONCE = 0;
        public static final int EXTEND_PER_VISIT = 1;
    }

}
