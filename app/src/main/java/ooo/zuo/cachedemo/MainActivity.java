package ooo.zuo.cachedemo;

import android.os.Environment;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.File;

import ooo.zuo.cachedemo.cache.CacheUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (initCache()) {
            CacheUtils.putString("key","qwertyuioioplkjhgfdsazxcvbnm",8);
            CacheUtils.putString("123","qwertyuioiosdfgadgplkjhgfdsazxcvbnm",100);
            CacheUtils.putString("234","adgqrhwrhsfhar",43);
            CacheUtils.putString("345","htsqregadgrh",60);
            CacheUtils.putString("456","hrwewergsdg",30);
            CacheUtils.putString("567","hwhrhwer交际盘丝洞😒😑😏🤠😑🙄🙄afghrhr");
            CacheUtils.putString("678","加油向未来！！！Come On ！");
            toast("缓存成功");
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
