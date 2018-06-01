package com.accton.iot.irrigationv1;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import com.accton.iot.irrigationv1.management.user.UserManager;

public class MainApplication extends Application {
    private final static String TAG = "MainApplication";
    private final static boolean DEBUG = true;

    // Statically global variables...
    private static MainApplication mInstance = null;
    private static Context mContext;
    private static UserManager mUserManager = new UserManager();

    public MainApplication() {
        super();

        if (DEBUG)
            Log.d(TAG, "MainApplication called.");
    }

    @Override
    public void onCreate() {
        if (DEBUG)
            Log.d(TAG, "onCreate called. this: " + this);
        super.onCreate();

        mInstance = this;
        mContext = getApplicationContext();
    }

    @Override
    public void onTerminate() {
        if (DEBUG)
            Log.d(TAG, "onTerminate called.");
        mInstance = null;

        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (DEBUG)
            Log.d(TAG, "onConfigurationChanged called.");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        if (DEBUG)
            Log.d(TAG, "onLowMemory called.");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        if (DEBUG)
            Log.d(TAG, "onTrimMemory called.");
        super.onTrimMemory(level);
    }

    // Account
    public static boolean isUserSignedIn() {
        if (DEBUG)
            Log.d(TAG, "isUserSignedIn called.");
        if(mUserManager==null)
            return false;
        if (DEBUG)
            Log.d(TAG, "isUserSignedIn called 2.");
        if(!mUserManager.isSignInDone())
            return false;
        if (DEBUG)
            Log.d(TAG, "isUserSignedIn called 3. f: " + mUserManager.isSignInSuccess());
        return mUserManager.isSignInSuccess();
    }

    public static UserManager getUserManager()
    {
        return mUserManager;
    }
}
