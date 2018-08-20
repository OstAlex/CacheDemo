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

import ooo.zuo.cachedemo.cache.CacheUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (initCache()) {
            Button button =  findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CacheUtils.putString("key","qwertyuioioplkjhgfdsazxcvbnm",5);
                    CacheUtils.putString("123","qwertyuioiosdfgadgplkjhgfdsazxcvbnm",1);
                    CacheUtils.putString("234","adgqrhwrhsfhar",43);
                    CacheUtils.putString("345","htsqregadgrh",60);
                    CacheUtils.putString("456","hrwewergsdg",3);
                    CacheUtils.putString("567","hwhrhwerafghrhr");
                    CacheUtils.putString("678","加油向未来！！！Come On ！");

                    toast("缓存成功");
                }
            });
            button = findViewById(R.id.button2);
            final TextView textView = findViewById(R.id.text_view);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("123").append("->").append(CacheUtils.getString("123")).append("\n");
                    builder.append("234").append("->").append(CacheUtils.getString("234")).append("\n");
                    builder.append("345").append("->").append(CacheUtils.getString("345")).append("\n");
                    builder.append("456").append("->").append(CacheUtils.getString("456")).append("\n");
                    builder.append("567").append("->").append(CacheUtils.getString("567")).append("\n");
                    builder.append("678").append("->").append(CacheUtils.getString("678")).append("\n");
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
