package com.alongthecloud.app.gallerywallpaper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.view.View;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.SharedPreferences;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;
import java.net.*;
import android.net.Uri;

public class LiveWallpaperGalleryService extends WallpaperService implements OnSharedPreferenceChangeListener {
    private static final String TAG = "Service";
    public static final int MSG_STRING = 0;

    private SurfaceEngine engine = null;
    private SharedPrefUtil prefutil = null;

    @Override
    public void onCreate() {
        super.onCreate();

        MyLog.init();
        MyLog.i(TAG, "onCreate");
        // 시작
        MyImageLoaderUtil.initImageLoader(this);

        ConfigUp config = ConfigUp.getInstance();
        prefutil = config.initSharedPref(this);
        prefutil.setChangeListener(this);
    }

    @Override
    public void onDestroy() {
        MyLog.i(TAG, "onDestroy");

        prefutil.unsetChangeListener(this);
        prefutil = null;

        ConfigUp.releaseInstance();

        super.onDestroy();
    }

    @Override
    public Engine onCreateEngine() {
        MyLog.i(TAG, "onCreateEngine");
        engine = new SurfaceEngine();
        return engine;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        MyLog.i(TAG, "onSharedPreferenceChanged");

        if (engine != null) {
            engine.reload();
        }
    }

    public class SurfaceEngine extends Engine {
        private static final String TAG = "Engine";

        private boolean visible = false;
        private int width = 0;
        private int height = 0;

        private Bitmap imageBitmap = null;

        private SimpleImageLoadingListener loadListener = new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedBitmap) {
                imageBitmap = loadedBitmap;
                draw();
            }
        };

        private Timer timer = null;
        private long toEpochMilli(LocalDateTime localDateTime) {
            return localDateTime.atZone(ZoneId.systemDefault())
                    .toInstant().toEpochMilli();
        }

        private long getNowEpochSecond() {
            LocalDateTime datetime_start = LocalDateTime.now();
            long epoch_second = toEpochMilli(datetime_start) / 1000;
            return epoch_second;
        }

        private long startTask(long periodSec) {
            MyLog.i(TAG, "startTask");

            long epoch_second = getNowEpochSecond();
            long rm = epoch_second % periodSec;
            long delay = periodSec - rm;

            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    nextPicture();
                    updateEvent();
                }
            };

            timer.scheduleAtFixedRate(task, delay * 1000, periodSec * 1000);
            return delay;
        }

        private void stopTask() {
            MyLog.i(TAG, "stopTask");

            if (timer != null) {
                timer.cancel();
                timer.purge();
                timer = null;
            }
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            MyLog.i(TAG, "onCreate");

            super.onCreate(surfaceHolder);

            ConfigUp config = ConfigUp.getInstance();
            // Start
            if (config.loadFromSharedPrefs()) {
                stopTask();

                long delay = startTask(config.getTimeSec());
                if (delay >= 1) {
                    nextPicture();
                    updateEvent();
                }
            }
        }

        @Override
        public void onDestroy() {
            MyLog.i(TAG, "onDestroy");

            stopTask();
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            MyLog.i(TAG, "onVisibilityChanged");

            super.onVisibilityChanged(visible);
            this.visible = visible;
            if (visible)
                updateEvent();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            MyLog.i(TAG, "onSurfaceDestroyed");

            this.visible = false;
            super.onSurfaceDestroyed(holder);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format,
                                     int width, int height) {
            MyLog.i(TAG, "onSurfaceChanged");

            this.width = width;
            this.height = height;

            super.onSurfaceChanged(holder, format, width, height);
            if (width > 0 && height > 0) {
                updateEvent();
            }
        }

        public void reload() {
            ConfigUp config = ConfigUp.getInstance();

            if (config.loadFromSharedPrefs()) {
                stopTask();
                startTask(config.getTimeSec());
                nextPicture();
                updateEvent();

                MyLog.d(TAG, "reload-sharedpref");
            }
        }

        private final Object updateEventObject = new Object();
        private long lastestDrawTime = 0;

        private void nextPicture() {
            ConfigUp config = ConfigUp.getInstance();
            config.nextPicture();
        }

        private void updateEvent() {
            // lock
            synchronized (updateEventObject) {
                final String TAG = "Engine";

                MyLog.infoCurrentTime(TAG);

                ConfigUp config = ConfigUp.getInstance();
                String imageUrl = config.getPictureUrl();
                if (imageUrl == null || imageUrl.length() == 0) {
                    MyLog.i(TAG, "ImageURL is None");
                    imageBitmap = null;
                    draw();

                } else {
                    MyLog.i(TAG, "ImageURL " + imageUrl);
                    if (visible) {
                        final ImageLoader imageLoader = ImageLoader.getInstance();

                        // Path 를 넘겨받아 URI 로 변환하여 넘겨줌
                        final File file = new File(imageUrl);
                        final Uri uri = Uri.fromFile(file);
                        final String uriString = uri.toString();
                        // %20 이슈로 강제 변환. 다른 특수 문자는 없는?듯
                        final String replaceString = uriString.replace("%20", " ");

                        imageLoader.loadImage(replaceString, this.loadListener);
                    }
                }
            }
        }

        private Rect destRectWithScaleType(int scaleType, Rect imageRect, Rect canvasRect) {
            int imageWidth = imageRect.width();
            int imageHeight = imageRect.height();
            int screenWidth = canvasRect.width();
            int screenHeight = canvasRect.height();

            int cx = screenWidth / 2;
            int cy = screenHeight / 2;

            switch (scaleType)
            {
                case 0:	// stretch
                case 1: // fit
                {
                    float vf = (float)screenHeight / (float)imageHeight;
                    float hf = (float)screenWidth / (float)imageWidth;

                    float f;
                    if (scaleType == 0)
                    {
                        f = vf > hf ? vf : hf;
                    }
                    else
                    {
                        f = vf < hf ? vf : hf;
                    }

                    int width = (int)(imageWidth*f);
                    int height = (int)(imageHeight*f);

                    int x = cx - (width / 2);
                    int y = cy - (height / 2);

                    return new Rect(x, y, x + width, y + height);
                }
                case 2:	// center
                {
                    int x = cx - (imageWidth / 2);
                    int y = cy - (imageHeight / 2);

                    return new Rect(x, y, x + imageWidth, y + imageHeight);
                }
                default:	// others, fill
                {
                    return canvasRect;
                }
            }
        }

        private final Object drawObject = new Object();
        private void draw() {
            if (!visible) return;

            lastestDrawTime = getNowEpochSecond();

            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = holder.lockCanvas();
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            Bitmap bm = imageBitmap;
            if (bm != null) {
                ConfigUp config = ConfigUp.getInstance();
                int filltype = config.getFillType();

                Paint paint = new Paint();
                paint.setFilterBitmap(true);

                Rect srcRect = new Rect(0, 0, bm.getWidth(), bm.getHeight());
                Rect destRect = destRectWithScaleType(filltype, srcRect, new Rect(0, 0, width, height));

                canvas.drawBitmap(bm, srcRect, destRect, paint);
            }

            holder.unlockCanvasAndPost(canvas);
        }
    }
}