package com.ebensz.games.ui.widget;

import android.graphics.Bitmap;
import com.ebensz.games.model.poker.ColoredPoker;
import ice.animation.TranslateAnimation;
import ice.node.widget.TextureGrid;

import static com.ebensz.games.ui.widget.OutsidePokerTiles.STAND_UP_Y;


/**
 * User: Mike.Hu
 * Date: 11-11-29
 * Time: 上午10:40
 */
public class PokerTile extends TextureGrid implements Cloneable, Comparable<PokerTile> {

    public PokerTile(ColoredPoker coloredPoker, Bitmap pokerBitmap) {
        super(pokerBitmap);
        this.coloredPoker = coloredPoker;
    }

    public ColoredPoker getColoredPoker() {
        return coloredPoker;
    }

    public void setColoredPoker(ColoredPoker coloredPoker) {
        this.coloredPoker = coloredPoker;
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        if (selected)
//            canvas.drawARGB(125, 125, 255, 125);
//    }

    public PokerTile copy() {

        PokerTile copy = new PokerTile(coloredPoker, texture.getBitmap());

        copy.setPos(getPosX(), getPosY());

        return copy;
    }

    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            this.selected = selected;
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public synchronized void standUp() {
        startAnimation(
                TranslateAnimation.createMoveBy(100, 0, -STAND_UP_Y)
        );
    }

    public synchronized void sitDown() {
        startAnimation(
                TranslateAnimation.createMoveBy(100, 0, STAND_UP_Y)
        );
    }

    @Override
    public String toString() {
        return coloredPoker.toString();
    }

    @Override
    public int compareTo(PokerTile another) {
        return coloredPoker.compareTo(another.coloredPoker);
    }

    private boolean selected;
    private ColoredPoker coloredPoker;


}
