package com.ebensz.games.ui.widget;

import android.graphics.Bitmap;
import com.ebensz.games.R;
import com.ebensz.games.model.poker.ColoredPoker;
import com.ebensz.games.model.poker.Poker;
import com.ebensz.games.res.LoadRes;
import ice.animation.Animation;
import ice.animation.RotateAnimation;
import ice.animation.TranslateAnimation;
import ice.graphic.texture.Texture;
import ice.node.Overlay;
import ice.node.OverlayParent;
import ice.node.widget.BitmapOverlay;

import java.util.HashMap;
import java.util.Map;

import static com.ebensz.games.ui.widget.OutsidePokerTiles.STAND_UP_Y;


/**
 * User: Mike.Hu
 * Date: 11-11-29
 * Time: 上午10:40
 */
public class PokerOverlay extends OverlayParent implements Cloneable, Comparable<PokerOverlay> {

    private static Texture backTexture;
    private static Map<ColoredPoker, Texture> frontTextureMap;

    static {
        frontTextureMap = new HashMap<ColoredPoker, Texture>(Poker.values().length);
    }

    public PokerOverlay(ColoredPoker coloredPoker) {
        this.coloredPoker = coloredPoker;

        enableDepthTestSwitch(true);

        Bitmap frontPoker = LoadRes.getFrontPoker(coloredPoker);

        front = new BitmapOverlay(frontPoker);
        frontTextureMap.put(coloredPoker, front.getTexture());

        front.setPos(0, 0, 1);

        if (backTexture == null) {
            back = new BitmapOverlay(R.drawable.poker_back_large);
            backTexture = back.getTexture();
        }
        else {
            Bitmap bitmap = backTexture.getBitmap();

            back = new BitmapOverlay(bitmap.getWidth(), bitmap.getHeight());

            back.setTexture(backTexture);
        }

        back.setPos(0, 0, 0.9f);
        back.setRotate(180, 0, 1, 0);

        addChildren(front, back);
    }

    public ColoredPoker getColoredPoker() {
        return coloredPoker;
    }

    public void setColoredPoker(ColoredPoker coloredPoker) {
        this.coloredPoker = coloredPoker;
        front.setTexture(frontTextureMap.get(coloredPoker));
    }

    public float getPokerWidth() {
        return front.getWidth();
    }

    public float getPokerHeight() {
        return front.getHeight();
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        if (selected)
//            canvas.drawARGB(125, 125, 255, 125);
//    }

    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            this.selected = selected;
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public synchronized void standUp() {
        startAnimation(new TranslateAnimation(100, 0, STAND_UP_Y));
    }

    public synchronized void sitDown() {
        startAnimation(new TranslateAnimation(100, 0, -STAND_UP_Y));
    }

    @Override
    public boolean hitTest(float x, float y) {
        return front.hitTest(x, y);
    }

    @Override
    public String toString() {
        return coloredPoker.toString();
    }

    @Override
    public int compareTo(PokerOverlay another) {
        return coloredPoker.compareTo(another.coloredPoker);
    }

    private boolean selected;
    private ColoredPoker coloredPoker;

    private BitmapOverlay front;
    private BitmapOverlay back;

    public void rotateToFront() {
        RotateAnimation rotateAnimation = new RotateAnimation(500, 0, 180);
        rotateAnimation.setRotateVector(0, 1, 0);
        startAnimation(rotateAnimation);

        rotateAnimation.setListener(new Animation.Listener() {
            @Override
            public void onAnimationEnd(Overlay overlay) {
                overlay.setRotate(0, 0, 0, 0);
            }
        });
    }

    public void setFront(boolean front) {
        setRotate(front ? 0 : 180, 0, 1, 0);
    }
}
