package com.bber.company.android.tools;

import android.content.Context;

import com.bber.company.android.constants.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by bn on 2017/9/20.
 */

public class GlideConfigModule implements GlideModule {
    public final static String TAG = "GlideConfigModule";

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, Constants.MAX_DISK_CACHE_SIZE));
        builder.setMemoryCache(new LruResourceCache(Constants.MAX_MEMORY_CACHE_SIZE));
        builder.setBitmapPool(new LruBitmapPool(Constants.MAX_MEMORY_CACHE_SIZE));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
