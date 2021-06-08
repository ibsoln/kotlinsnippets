package com.example.jsonapisnippets;

import android.app.Application;
import android.content.Context;

public class contextServer extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        contextServer.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return contextServer.context;
    }
}