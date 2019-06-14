package com.zack.intelligent;

import android.app.Application;
import android.content.Context;
import android.os.PowerManager;

import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.OkHttpNetworkExecutor;
import com.yanzhenjie.nohttp.cache.DBCacheStore;
import com.yanzhenjie.nohttp.cookie.DBCookieStore;
import com.zack.intelligent.utils.CrashHandler;
import com.zack.intelligent.utils.LogcatHelper;
import com.zack.intelligent.utils.SoundPlayUtil;

/**
 *
 */

public class App extends Application {
    private static Context context;
    private static App mInstance;
    private PowerManager.WakeLock wl;
//    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context =getApplicationContext();

        LogcatHelper.getInstance(this).start();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "WakeLock");
        wl.acquire();

        NoHttp.initialize(this, new NoHttp.Config()
                .setConnectTimeout(30 * 1000)
                .setReadTimeout(30 * 1000)
                .setCacheStore(new DBCacheStore(this).setEnable(true))
                .setCookieStore(new DBCookieStore(this).setEnable(false))
                .setNetworkExecutor(new OkHttpNetworkExecutor()));
        Logger.setDebug(true);
        Logger.setTag("NoHttpSample");

//        CrashHandler.getInstance().init(getApplicationContext());

        SoundPlayUtil.getInstance().init(getApplicationContext());
//        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
//            return;
//        }
//        refWatcher = LeakCanary.install(this);
        // Normal app init code...
    }

    public static Context getContext(){
        return context;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        wl.release();
        SoundPlayUtil.getInstance().release();
    }

    public static App getInstance() {
        return mInstance;
    }
}
