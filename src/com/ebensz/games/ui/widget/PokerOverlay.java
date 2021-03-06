package com.ebensz.games.ui.widget;

import android.graphics.Bitmap;
import com.ebensz.games.R;
import com.ebensz.games.model.poker.ColoredPoker;
import com.ebensz.games.model.poker.Poker;
import com.ebensz.games.res.LoadRes;
import ice.animation.Animation;
import ice.animation.RotateAnimation;
import ice.animation.TranslateAnimation;
import ice.graphic.gl_status.ColorController;
import ice.graphic.texture.Texture;
import ice.node.Overlay;
import ice.node.OverlayParent;
import ice.node.widget.BitmapOverlay;
import ice.node.widget.Grid;

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

    private static ColorController colorController;

    private static final float[] SELECTED_COLOR = new float[]{0.5f, 0.5f, 1, 1};

    static {
        frontTextureMap = new HashMap<ColoredPoker, Texture>(Poker.values().length);
        backTexture = new Texture(R.drawable.poker_back_large);
        colorController = new ColorController(SELECTED_COLOR);
    }

    public PokerOverlay(ColoredPoker coloredPoker) {
        this.coloredPoker = coloredPoker;

        Bitmap frontPoker = LoadRes.getFrontPoker(coloredPoker);

        front = new BitmapOverlay(frontPoker);
        frontTextureMap.put(coloredPoker, front.getTexture());

        front.setPosZ(1);

        addChildren(front);
    }

    public void setUseBack(boolean enableBack) {
        if (this.enableBack == enableBack) return;

        this.enableBack = enableBack;

        if (enableBack) {
            if (back == null) {
                back = new Grid(front.getWidth(), front.getHeight(), false);
                back.setTexture(backTexture);
                //back.setRotate(180, 0, 1, 0);
                //back.addGlStatusController(new CullFaceController(Back));
            }

            if (!containsChild(back))
                addChild(back);
        }
        else {
            if (back != null) {
                remove(back);
            }
        }
    }

    public ColoredPoker getColoredPoker() {
        return coloredPoker;
    }

    public void setColoredPoker(ColoredPoker coloredPoker) {
        this.coloredPoker = coloredPoker;
        front.setTexture(frontTextureMap.get(coloredPoker));
    }

    public float getWidth() {
        return front.getWidth();
    }

    public float getHeight() {
        return front.getHeight();
    }

    public void setSelected(boolean selected) {

        if (this.selected != selected) {
            this.selected = selected;

            System.out.println("selected = " + selected);

            if (selected) {
                front.addGlStatusController(colorController);
            }
            else {
                front.removeGlStatusController(colorController);
            }
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public synchronized void standUp() {
        startAnimation(new TranslateAnimation(50, 0, STAND_UP_Y));
    }

    public synchronized void sitDown() {
        startAnimation(new TranslateAnimation(50, 0, -STAND_UP_Y));
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

    public void rotateToFront() {
        RotateAnimation rotateAnimation = new RotateAnimation(500, 0, 180);
        rotateAnimation.setRotateVector(0, 1, 0);

        rotateAnimation.setListener(new Animation.Listener() {
            @Override
            public void onAnimationEnd(Overlay overlay) {
                overlay.setRotate(0, 0, 0, 0);
            }
        });

        startAnimation(rotateAnimation);
    }

    public void setFront(boolean front) {
        setRotate(front ? 0 : 180, 0, 1, 0);
    }

    private boolean enableBack;

    private boolean selected;
    private ColoredPoker coloredPoker;

    private BitmapOverlay front;
    private Grid back;
}
