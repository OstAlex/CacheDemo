package ooo.zuo.cachedemo;

import android.os.Environment;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import ooo.zuo.cachedemo.cache.CacheUtils;

public class MainActivity extends AppCompatActivity {

    long time ;
    char[] chars = new char[]{'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (initCache()) {
            Button button =  findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    time = System.currentTimeMillis();
                    for (int i = 0; i < 10; i++) {
                        char[] c = new char[3];
                        double random = Math.random();
                        int position = (int) (26*random);
                        c[0] = chars[position];
                        position = (int) (Math.random()*26);
                        c[1] = chars[position];
                        c[2] = chars[(int)(Math.random()*26)];
                        String key = new String(c);
                        char[] values = new char[100];
                        for (int j = 0; j < 100; j++) {
                            values[j] = chars[(int)(Math.random()*26)];
                        }
                        String value = new String(values);
                        if (value.contains("a")){
                            CacheUtils.putString(key, value+"😝asd😝😜jafasdw😘👨‍👘👘哈哈💄💄💄哈😝µ∆˚∆˙©ƒ®ƒ");
                        }else if (value.contains("b")){
                            CacheUtils.putWithExtensibleTime(key+"😆😆😆", value,15);
                        }else {
                            CacheUtils.putString(key, value);
                        }
                    }

                    toast("缓存成功 "+(System.currentTimeMillis()-time));
                }
            });
            button = findViewById(R.id.button2);
            final TextView textView = findViewById(R.id.text_view);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    time = System.currentTimeMillis();
                    Map<String, String> cache = CacheUtils.getAllCacheAsString();
                    time = System.currentTimeMillis()-time;
                    Iterator<String> iterator = cache.keySet().iterator();
                    StringBuilder builder = new StringBuilder();
                    while (iterator.hasNext()){
                        String key = iterator.next();
                        String value = cache.get(key);
                        builder.append(key).append("->").append(value).append("\n");
                    }
                    builder.append("time:").append(time).append("\n\n");
                    textView.setText(builder.toString());
                }
            });

        }

    }


    private boolean initCache(){
        String rootPath = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File dir = getExternalFilesDir(null);
            if (dir!=null){
                rootPath = dir.getAbsolutePath();
                toast(rootPath);
            }
        }else {
            toast("没有外置存储器!");
            return false;
        }
        CacheUtils.init(rootPath,"cache");
        return true;
    }

    private void toast(String text){
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }
}
