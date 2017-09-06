package com.yao.storehomeuidemo.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;

import com.yao.storehomeuidemo.BaseApplication;

/**
 * 获得屏幕相关的辅助类
 *
 * @author zhy
 */
public class ScreenUtils {
    private static float density = -1F;
    private static int widthPixels = -1;
    private static int heightPixels = -1;
    private static int mStatusHeight = 0;

    private ScreenUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static float getDensity() {
        if (density <= 0F) {
            density = BaseApplication.getInstance().getResources().getDisplayMetrics().density;
        }
        return density;
    }

    public static int dpToPx(int dpValue) {
        return (int) (dpValue * getDensity() + 0.5F);
    }

    public static int pxToDp(int pxValue) {
        return (int) (pxValue / getDensity() + 0.5F);
    }

    public static int getScreenWidth() {
        if (widthPixels <= 0) {
            widthPixels = BaseApplication.getInstance().getResources().getDisplayMetrics().widthPixels;
        }
        return widthPixels;
    }


    public static int getScreenHeight() {
        if (heightPixels <= 0) {
            heightPixels = BaseApplication.getInstance().getResources().getDisplayMetrics().heightPixels;
        }
        return heightPixels;
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth();
        int height = getScreenHeight();
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;

    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = getScreenWidth();
        int height = getScreenHeight();
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return bp;

    }
}
