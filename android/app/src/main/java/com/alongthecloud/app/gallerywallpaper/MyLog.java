package com.alongthecloud.app.gallerywallpaper;

import java.io.File;
import java.io.IOException;

import android.os.Environment;

import java.util.Calendar;
import java.util.Date;

import android.util.Log;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;

// https://github.com/orhanobut/logger
// Logger 가 왜 동작 안하지 ?

public class MyLog {
    private static final String TAG = "GalleryWallpaperF";

    private static final int FLAG_ERROR = 0x01;
    private static final int FLAG_WARNING = 0x02;
    private static final int FLAG_DEBUG = 0x04;
    private static final int FLAG_INFO = 0x08;

    private static int logLevel = FLAG_ERROR | FLAG_WARNING | FLAG_DEBUG | FLAG_INFO;
    private static boolean isLogLevel(int logFlag) {
        return (logLevel & logFlag) != 0;
    }

    private static boolean isInit = false;
    public static boolean init() {
        if (logLevel != 0) return false;
        if (isInit) return true;

        Logger.addLogAdapter(new AndroidLogAdapter());
        // Logger.addLogAdapter(new AndroidLogAdapter() {
        // @Override public boolean isLoggable(int priority, String tag) {
        //     return BuildConfig.DEBUG;
        // }
        // });

        isInit = true;
        return true;
    }

    public static void d(String prefix, Object message) {
        if (isLogLevel(FLAG_DEBUG) && isInit) {
            Logger.d(prefix, message);
        }
    }

    public static void i(String prefix, Object message) {
        if (isLogLevel(FLAG_INFO) && isInit) {
            Logger.i(prefix, message);
        }
    }

    public static void infoCurrentTime(String prefix) {
        if (isLogLevel(FLAG_INFO) && isInit) {
            Date currentTime = Calendar.getInstance().getTime();
            Logger.i(prefix, currentTime);
        }
    }
}
