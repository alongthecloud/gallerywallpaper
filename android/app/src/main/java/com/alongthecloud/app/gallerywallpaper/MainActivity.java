package com.alongthecloud.app.gallerywallpaper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.Log;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.Menu;
import android.view.View;

import java.io.*;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL_METHOD = "gallerywallpaper.pref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        ConfigUp config = ConfigUp.getInstance();
        SharedPrefUtil sharedprefutil = config.initSharedPref(this);

        // Flutter MethodChannel Example
        MethodChannel mc = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(),CHANNEL_METHOD);
        mc.setMethodCallHandler((methodCall, result) ->
                {
                    Log.i("method came",methodCall.method);
                    if (methodCall.method.equals("save")) {
                        String text = methodCall.argument("text");
                        if (text == null)
                            text = "";

                        sharedprefutil.setStr("config", text);

                        result.success(true);
                    } else if (methodCall.method.equals("load")) {
                        String text = sharedprefutil.getStr("config");
                        result.success(text);
                    } else {
                        // Log.i("new method came",methodCall.method);
                    }
                }
        );
    }
}
