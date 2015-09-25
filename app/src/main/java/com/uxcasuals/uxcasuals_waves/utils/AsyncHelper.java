package com.uxcasuals.uxcasuals_waves.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by Dhakchianandan on 24/09/15.
 */
public class AsyncHelper {
    private static AsyncHelper instance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static Context context;

    private AsyncHelper(Context lContext) {
        context = lContext;
        requestQueue = getRequestQueue();
        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap>
                    cache = new LruCache<String, Bitmap>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    public static synchronized AsyncHelper getInstance(Context context) {
        if (instance == null) instance = new AsyncHelper(context);
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) requestQueue =
                Volley.newRequestQueue(context.getApplicationContext());
        return requestQueue;
    }

    public void addToRequestQueue(Request request) {
        getRequestQueue().add(request);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}
