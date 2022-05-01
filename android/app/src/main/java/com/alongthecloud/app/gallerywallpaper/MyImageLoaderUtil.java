package com.alongthecloud.app.gallerywallpaper;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

public class MyImageLoaderUtil {
    public static void initImageLoader(Context context) {
        ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
        if (!imageLoader.isInited()) {
            File cacheDir = StorageUtils.getCacheDirectory(context);

            final int memCacheSize = 3 * 1024*1024;
            final int diskCacheSize = 50 * 1024*1024;
            final boolean enableDiskCache = false;

            DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(enableDiskCache)
                    .build();

            ImageLoaderConfiguration.Builder configBuilder = new ImageLoaderConfiguration.Builder(context);
            configBuilder.defaultDisplayImageOptions(imageOptions)
                    .threadPoolSize(2)
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(new LruMemoryCache(memCacheSize))
                    .memoryCacheSize(memCacheSize);

            if (enableDiskCache) {
                    configBuilder.diskCache(new UnlimitedDiskCache(cacheDir)) // default
                        .diskCacheSize(diskCacheSize)
                        .diskCacheFileCount(100)
                        .diskCacheFileNameGenerator(new HashCodeFileNameGenerator());
            }

            ImageLoaderConfiguration config = configBuilder.writeDebugLogs().build();
            imageLoader.init(config);
        }
    }
}
