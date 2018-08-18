package ooo.zuo.cachedemo;

import android.app.Application;
import android.content.Context;

/**
 * @author Zuo Chuanqiang
 * @date 2018/8/18 下午1:37
 */
public class BaseApplication extends Application {

    private static  Application mJDApplication;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mJDApplication = this;
    }

    public static Application getJDApplication(){
        return mJDApplication;
    }
}
