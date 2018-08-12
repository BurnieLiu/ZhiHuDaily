package com.burnie.zhihu;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

/**
 * Created by liuli on 2018/3/12.
 */

public class BolderTransformation extends BitmapTransformation {

    private final int mBorderWidth;

    public BolderTransformation(int borderWidth) {
        mBorderWidth = borderWidth;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap source, int outWidth, int outHeight) {
        if (source == null) {
            return null;
        }
        Bitmap result = pool.get(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        RectF dest = new RectF(mBorderWidth, mBorderWidth, outWidth - mBorderWidth, outHeight - mBorderWidth);
        Rect rect = new Rect(0, 0, outWidth, outHeight);
        canvas.drawBitmap(source, rect, dest, paint);
        return result;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }
}
