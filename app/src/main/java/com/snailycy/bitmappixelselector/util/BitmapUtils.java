package com.snailycy.bitmappixelselector.util;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.snailycy.bitmappixelselector.bean.BitmapPoint;

import java.util.TreeSet;

/**
 * @author snailycy
 */
public class BitmapUtils {
    private static final String TAG = "BitmapUtils";
    private static final int TOLERANCE_PIXEL_NUM = 20;

    /**
     * 获取bitmap指定颜色的连续区域
     *
     * @param bitmap
     * @return BitmapPoint数组，BitmapPoint[0]选择区域左上角坐标，BitmapPoint[1]选择区域右下角坐标
     */
    public static BitmapPoint[] getContentFromBitmap(@NonNull Bitmap bitmap) throws Exception {
        // 获取指定区域颜色范围
        int[] baseColorRange = getCenterColorRange(bitmap);
        int bitmapW = bitmap.getWidth();
        int bitmapH = bitmap.getHeight();
        BitmapPoint leftTopPoint = new BitmapPoint();
        BitmapPoint rightBottomPoint = new BitmapPoint();
        // 获取左上角坐标x值
        for (int x = bitmapW / 2; x > 0; x--) {
            int color = bitmap.getPixel(x, bitmapH / 2);
            if (color >= baseColorRange[0] && color <= baseColorRange[1]) {
                leftTopPoint.setX(x);
            } else {
                break;
            }
        }
        int baseColorPixelCountByX = getPixelCountByX(bitmap, baseColorRange, leftTopPoint.getX());
        // 获取右下角坐标x值
        for (int x = bitmapW / 2; x < bitmapW; x++) {
            int color = bitmap.getPixel(x, bitmapH / 2);
            if (color >= baseColorRange[0] && color <= baseColorRange[1]) {
                int pixelCount = getPixelCountByX(bitmap, baseColorRange, x);
                if (baseColorPixelCountByX - pixelCount < 8 * TOLERANCE_PIXEL_NUM) {
                    rightBottomPoint.setX(x);
                } else {
                    break;
                }
            } else {
                // 尝试跳过障碍点是否还是内容区域
                x += TOLERANCE_PIXEL_NUM;
            }
        }
        int baseColorPixelCountByY = getPixelCountByY(bitmap, baseColorRange, bitmapH / 2);
        // 获取左上角坐标y值
        for (int y = bitmapH / 2; y > 0; y--) {
            int color = bitmap.getPixel(bitmapW / 2, y);
            if (color >= baseColorRange[0] && color <= baseColorRange[1]) {
                int pixelCount = getPixelCountByY(bitmap, baseColorRange, y);
                if (baseColorPixelCountByY - pixelCount < 5 * TOLERANCE_PIXEL_NUM) {
                    leftTopPoint.setY(y);
                } else {
                    break;
                }
            } else {
                // 尝试跳过障碍点是否还是内容区域
                y -= TOLERANCE_PIXEL_NUM;
            }
        }
        // 获取右下角坐标y值
        for (int y = bitmapH / 2; y < bitmapH; y++) {
            int color = bitmap.getPixel(bitmapW / 2, y);
            if (color >= baseColorRange[0] && color <= baseColorRange[1]) {
                int pixelCount = getPixelCountByY(bitmap, baseColorRange, y);
                if (Math.abs(pixelCount - baseColorPixelCountByY) < 5 * TOLERANCE_PIXEL_NUM) {
                    rightBottomPoint.setY(y);
                } else {
                    break;
                }
            } else {
                // 尝试跳过障碍点是否还是内容区域
                y += TOLERANCE_PIXEL_NUM;
            }
        }

        BitmapPoint[] contentPoints = new BitmapPoint[2];
        contentPoints[0] = leftTopPoint;
        contentPoints[1] = rightBottomPoint;

        Log.d(TAG, "左上角坐标：(" + contentPoints[0].getX() + "," + contentPoints[0].getY()
                + ")，右下角坐标：(" + contentPoints[1].getX() + "," + contentPoints[1].getY() + ")");
        Log.d(TAG, "指定颜色的连续区域：宽度 = " + (contentPoints[1].getX() - contentPoints[0].getX())
                + "，高度 = " + (contentPoints[1].getY() - contentPoints[0].getY()));
        return contentPoints;
    }


    /**
     * 获取bitmap中间区域的颜色范围
     *
     * @param bitmap
     * @return
     */
    private static int[] getCenterColorRange(Bitmap bitmap) {
        int bitmapW = bitmap.getWidth();
        int bitmapH = bitmap.getHeight();
        TreeSet<Integer> tempColorSet = new TreeSet<>();
        int offset = 100;
        for (int x = bitmapW / 2 - offset; x <= bitmapW / 2 + offset; x++) {
            for (int y = bitmapH / 2 - offset; y <= bitmapH / 2 + offset; y++) {
                tempColorSet.add(bitmap.getPixel(x, y));
            }
        }
        int[] centerBaseColors = new int[2];
        // 最小值
        centerBaseColors[0] = tempColorSet.first();
        // 最大值
        centerBaseColors[1] = tempColorSet.last();
        return centerBaseColors;
    }

    /**
     * 根据指定颜色范围，指定坐标y，获取bitmap一行的像素点数量
     *
     * @param bitmap
     * @param baseColorRangle
     * @param y
     * @return
     */
    private static int getPixelCountByY(Bitmap bitmap, int[] baseColorRangle, int y) {
        int pixelCount = 0;
        int bitmapW = bitmap.getWidth();
        for (int x = 0; x < bitmapW; x++) {
            int color = bitmap.getPixel(x, y);
            if (color >= baseColorRangle[0] && color <= baseColorRangle[1]) {
                pixelCount++;
            }
        }
        return pixelCount;
    }

    /**
     * 根据指定颜色范围，指定坐标x，获取bitmap一列的像素点数量
     *
     * @param bitmap
     * @param baseColorRangle
     * @param x
     * @return
     */
    private static int getPixelCountByX(Bitmap bitmap, int[] baseColorRangle, int x) {
        int pixelCount = 0;
        int bitmapH = bitmap.getHeight();
        for (int y = 0; y < bitmapH; y++) {
            int color = bitmap.getPixel(x, y);
            if (color >= baseColorRangle[0] && color <= baseColorRangle[1]) {
                pixelCount++;
            }
        }
        return pixelCount;
    }
}
