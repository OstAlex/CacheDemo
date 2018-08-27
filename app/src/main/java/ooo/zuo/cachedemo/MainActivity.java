package ooo.zuo.cachedemo;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ooo.zuo.cachedemo.cache.CacheUtils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    char[] chars = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    long time;
    // v3
    List<String> keys = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (initCache()) {
            // v2
            Button button = findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    time = System.currentTimeMillis();
                    keys.clear();
                    for (int i = 0; i < 10; i++) {
                        char[] c = new char[4];
                        for (int j = 0; j < 4; j++) {
                            c[j] = chars[(int) (Math.random() * 26)];
                        }
                        String key = new String(c);
                        boolean isSuccess ;
                        if (i % 2 == 0) {
                            isSuccess = CacheUtils.putBitmapWithExtendTime(key, Bitmap.createBitmap(20,20, Bitmap.Config.ARGB_8888),2);
                        } else {
                            isSuccess = CacheUtils.putBitmap(key, Bitmap.createBitmap(20,20, Bitmap.Config.ARGB_8888), time + 1000 * 5, (int) (Math.random() * 10 + 5));
                        }
                        Log.d(TAG, "onClick: " + key + " -> " + isSuccess);
                        keys.add(key);
                    }

                    toast("缓存成功 " + (System.currentTimeMillis() - time));
                }
            });
            button = findViewById(R.id.button2);
            final TextView textView = findViewById(R.id.text_view);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringBuilder builder = new StringBuilder();
                    for (String key : keys) {
                        Bitmap value = CacheUtils.getBitmap(key);
                        builder.append(key).append("->").append(value).append("\n");
                    }
                    builder.append("time:").append((System.currentTimeMillis() - time) / 1000).append("\n\n");
                    textView.setText(builder.toString());
                }
            });

        }

    }


    private boolean initCache() {
        String rootPath = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File dir = getExternalFilesDir(null);
            if (dir != null) {
                rootPath = dir.getAbsolutePath();
                toast(rootPath);
            }
        } else {
            toast("没有外置存储器!");
            return false;
        }
        CacheUtils.init(rootPath, "cache");
        return true;
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
