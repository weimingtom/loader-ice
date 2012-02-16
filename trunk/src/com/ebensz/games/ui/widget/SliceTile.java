package com.ebensz.games.ui.widget;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import ice.node.Overlay;

import javax.microedition.khronos.opengles.GL11;

/**
 * User: jason
 * Date: 12-2-13
 * Time: 上午9:54
 */
public class SliceTile extends Overlay {
    public static final int DEFAULT_RECODE_POINT_SIZE = 20;
    private static final int DEFAULT_WIDTH = 1024;
    private static final int DEFAULT_HEIGHT = 768;
    private static final float H_J_F_G = 0.618f;// 黄金分割点

    public SliceTile() {

        duringSlice = true;

        points = new float[DEFAULT_RECODE_POINT_SIZE * 2];
        pointsDoubleBuffer = new float[points.length];

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linePaint.setShadowLayer(10, 0, 0, Color.RED);
        linePaint.setColor(Color.BLACK);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        updateData(event);

        return false;
    }

    private void updateData(MotionEvent event) {

        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();

        if (action == MotionEvent.ACTION_DOWN) {
            pointSize = 0;
            drawStartIndex = 0;
            duringSlice = true;
        }

        if (action == MotionEvent.ACTION_UP) {
            duringSlice = false;
        }

        pushData(x, y);
    }

    private void pushData(float x, float y) {

        if (pointSize >= DEFAULT_RECODE_POINT_SIZE) {
            if (pointSize > 1) {
                for (int i = 0; i < pointSize - 1; i++) {
                    points[i * 2] = points[(i + 1) * 2];
                    points[i * 2 + 1] = points[(i + 1) * 2 + 1];
                }
            }
        }

        pointSize++;

        if (pointSize > DEFAULT_RECODE_POINT_SIZE)
            pointSize = DEFAULT_RECODE_POINT_SIZE;

        points[pointSize * 2 - 2] = x;
        points[pointSize * 2 - 1] = y;
    }

    @Override
    protected void onDraw(GL11 gl) {

        if (pointSize == 0) return;

        boolean duringSliceTemp = duringSlice;

        if (duringSliceTemp) {
            drawStartIndex = 0;
        }

        int drawSize = pointSize - drawStartIndex;

        if (drawSize <= 0)
            return;


        System.arraycopy(points, drawStartIndex * 2, pointsDoubleBuffer, 0, drawSize * 2);

        for (int i = 1; i < drawSize; i++) {

            if (i < drawSize * H_J_F_G) {
                linePaint.setStrokeWidth(i);
            }
            else {
                linePaint.setStrokeWidth(drawSize - i);
            }

//            canvas.drawLine(
//                    pointsDoubleBuffer[i * 2 - 2], pointsDoubleBuffer[i * 2 - 1],
//                    pointsDoubleBuffer[(i + 1) * 2 - 2], pointsDoubleBuffer[(i + 1) * 2 - 1],
//                    linePaint
//            );
        }

        if (!duringSliceTemp) {
            if (drawStartIndex < pointSize)
                drawStartIndex += 2;
        }
    }


    private boolean duringSlice;

    private float points[];
    private float pointsDoubleBuffer[];
    private int pointSize;
    private int drawStartIndex;
    private Paint linePaint;
}
