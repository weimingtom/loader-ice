package com.ebensz.games.ui.widget;

import android.graphics.Bitmap;
import com.ebensz.games.R;
import ice.animation.Interpolator.LinearInterpolator;
import ice.animation.TranslateAnimation;
import ice.engine.EngineContext;
import ice.graphic.texture.Texture;
import ice.node.OverlayParent;
import ice.node.widget.Grid;
import ice.res.Res;

/**
 * User: jason
 * Date: 12-3-1
 * Time: 下午2:31
 */
public class FaPaiAnimation extends OverlayParent {

    public FaPaiAnimation() {
        Bitmap bitmap = Res.getBitmap(R.drawable.f_p_anim);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int x = EngineContext.getAppWidth() / 2;
        int y = EngineContext.getAppHeight() / 2 - 100;

        left = new Grid(width, height);
        left.setPos(x, y);

        right = new Grid(width, height);
        right.setPos(x, y);

        outside = new Grid(width, height);
        outside.setPos(x, y);

        Texture texture = new Texture(bitmap);

        left.setTexture(texture);
        right.setTexture(texture);
        outside.setTexture(texture);
    }

    public void start() {

        int duration = 600;
        LinearInterpolator linearInterpolator = new LinearInterpolator();

        TranslateAnimation leftAnimation = new TranslateAnimation(duration, -200, 0);
        leftAnimation.setInterpolator(linearInterpolator);
        leftAnimation.setLoop(true);

        TranslateAnimation rightAnimation = new TranslateAnimation(duration, 200, 0);
        rightAnimation.setInterpolator(linearInterpolator);
        rightAnimation.setLoop(true);

        TranslateAnimation outsideAnimation = new TranslateAnimation(duration, 0, -100);
        outsideAnimation.setInterpolator(linearInterpolator);
        outsideAnimation.setLoop(true);

        left.startAnimation(leftAnimation);
        right.startAnimation(rightAnimation);
        outside.startAnimation(outsideAnimation);

        addChildren(left, right, outside);
    }

    public void stop() {
        left.cancelAnimation();
        right.cancelAnimation();
        outside.cancelAnimation();
        clear();
    }

    private Grid left, right, outside;
}
