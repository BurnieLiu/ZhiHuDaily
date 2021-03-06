package com.burnie.zhihu;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by liuli on 2018/3/20.
 */

public class ZhihuApp extends Application {
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        refWatcher = LeakCanary.install(this);
        // Normal app init code...
    }

    public static RefWatcher getRefWatcher(Context context) {
        ZhihuApp application = (ZhihuApp) context.getApplicationContext();
        return application.refWatcher;
    }

}
