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

    long time ;
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
                    CacheUtils.putString("123","gfshargfg",6);
                    CacheUtils.putString("234","g三国杀好人好事a2342fdgdg",19);
                    CacheUtils.putString("345","hts分公司发过火qregadgrh",24);
                    CacheUtils.putString("456","发货sgsghs蛋糕师傅速度发货",13);
                    CacheUtils.putString("567","hwhrhwera挨个fghrhr");
                    CacheUtils.putString("678","sfghrafhr加油向未来！！！Come On ！");
                    CacheUtils.putWithExtensibleTime("789","lalalalalalalala",5);

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
                    builder.append("789").append("->").append(CacheUtils.getString("789")).append("\n");
                    builder.append((System.currentTimeMillis()-time)/1000);
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
