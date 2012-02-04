package com.ebensz.games.scenes;

import android.graphics.*;
import android.view.animation.*;
import ice.node.Drawable;
import ice.node.DrawableParent;
import ice.util.TextDrawer;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Jason
 * Date: 12-2-4
 * Time: 下午6:17
 */
public class AnimationTextBox extends DrawableParent<Drawable> {

    public AnimationTextBox(int maxWidth, int eachLineHeight, long lineDuring) {
        // super(new Point(), maxWidth, eachLineHeight, true);

        this.eachLineHeight = eachLineHeight;
        this.lineDuring = lineDuring;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);

        lineRegion = new Rect(0, 0, maxWidth, eachLineHeight);
        textInterpolator = new AccelerateDecelerateInterpolator();
    }

    public void setText(String oneLineText) {
        ArrayList<String> lines = new ArrayList<String>();
        lines.add(oneLineText);
        setTexts(lines, 0);
    }

    public void setTexts(List<String> lines, int linesMargin) {
        this.lines = lines;
        this.linesMargin = linesMargin;

        int lineCount = lines.size();

        if (lineCount > 1) {
            //setHeight(eachLineHeight * lineCount); //todo 可以加点动画
        }

        startStamp = AnimationUtils.currentAnimationTimeMillis();

        activeLine = 0;
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//
//        if (lines == null || lines.size() == 0) return;
//
//        if (activeLine >= 0 && activeLine < lines.size()) {
//            drawWithAnimation(canvas);
//        }
//        else {
//            drawNormal(canvas);
//        }
//
//        //canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
//    }

    private void drawWithAnimation(Canvas canvas) {

        int lineCount = lines.size();

        Rect region = new Rect(lineRegion);

        for (int i = 0; i < lineCount; i++) {

            String line = lines.get(i);

            if (i == activeLine) {

                Rect clipRegion = new Rect(region);
                long sub = AnimationUtils.currentAnimationTimeMillis() - startStamp;

                boolean clip = sub < lineDuring;

                if (clip) {
                    canvas.save(Canvas.CLIP_SAVE_FLAG);

                    float interpolatedTime = textInterpolator.getInterpolation(sub / (float) lineDuring);

                    clipRegion.right = clipRegion.left + (int) (getWidth() * interpolatedTime);
                    canvas.clipRect(clipRegion);
                }
                else {
                    activeLine++;
                }

                TextDrawer.drawTextInLine(canvas, paint, line, region, false);

                if (clip) {
                    canvas.restore();
                }

            }
            else {
                TextDrawer.drawTextInLine(canvas, paint, line, region, false);
            }

            region.offset(0, eachLineHeight + linesMargin);
        }

    }

    private void drawNormal(Canvas canvas) {
        Rect region = new Rect(lineRegion);

        for (String line : lines) {

            TextDrawer.drawTextInLine(canvas, paint, line, region, false);

            region.offset(0, eachLineHeight + linesMargin);
        }
    }

    private int activeLine;
    private long startStamp;
    private long lineDuring;
    private android.view.animation.Interpolator textInterpolator;

    private Rect lineRegion;
    private Paint paint;
    private int linesMargin;
    private List<String> lines;
    private int eachLineHeight;
}
