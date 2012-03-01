package com.ebensz.games.ui.widget;

import android.graphics.Bitmap;
import com.ebensz.games.R;
import ice.node.OverlayParent;
import ice.node.widget.BitmapOverlay;
import ice.res.Res;

/**
 * User: Mike.Hu
 * Date: 12-2-24
 * Time: 下午4:01
 */
public class LimitNumberOverlay extends OverlayParent {

    public static final int BG_WIDTH = 75;

    public LimitNumberOverlay(int number, int leftIconIndex) {

        super();

        Bitmap bgBitmap = Res.getBitmap(R.drawable.title_block_bg);
        bg = new BitmapOverlay(bgBitmap);
        bg.setBounds(BG_WIDTH, bgBitmap.getHeight());
        bg.setPos(this.getPosX(), this.getPosY());
        addChild(bg);

        leftIcon = new BitmapOverlay(leftIconIndex);
        leftIcon.setPos(this.getPosX() - 20, this.getPosY());
        addChild(leftIcon);

        String numberString = String.valueOf(number);
        int length = numberString.length();

        rightNumber = new NumberOverlay(number);
        leftIcon.setPos(this.getPosX() + 10, this.getPosY());
        addChild(rightNumber);
    }

    public void setNumber(int number) {

        Bitmap bgBitmap = Res.getBitmap(R.drawable.title_block_bg);
        bg.setBounds(BG_WIDTH, bgBitmap.getHeight());
    }

    private Bitmap createMaxNumber() {

        return null;
    }

    private BitmapOverlay bg;
    private BitmapOverlay leftIcon;
    private NumberOverlay rightNumber;
}
