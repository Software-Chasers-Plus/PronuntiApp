package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import android.graphics.Bitmap;
import android.util.LruCache;

public class BitmapCache {
    private static LruCache<String, Bitmap> mMemoryCache;

    public BitmapCache() {
        // Inizializza la cache
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8; // Usa 1/8 della memoria disponibile

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // La cache sarà misurata in kilobytes invece di numero di elementi
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(String key) {
        // Ottieni la bitmap dalla cache
        Bitmap bitmap = mMemoryCache.get(key);
        // Rimuovi la bitmap dalla cache
        if (bitmap != null) {
            mMemoryCache.remove(key);
        }
        // Restituisci la bitmap
        return bitmap;
    }
}
