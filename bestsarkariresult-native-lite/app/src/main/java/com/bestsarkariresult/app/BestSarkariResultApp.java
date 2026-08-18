package com.bestsarkariresult.app;

import android.app.Application;

import com.onesignal.OneSignal;

public class BestSarkariResultApp extends Application {
    public static final String ONESIGNAL_APP_ID = "383f59a5-fb0c-4c8d-bd9c-2dbe4b81482c";

    @Override public void onCreate() {
        super.onCreate();
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID);
    }
}
