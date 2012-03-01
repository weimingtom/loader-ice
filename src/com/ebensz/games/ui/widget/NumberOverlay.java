package com.ebensz.games.ui.widget;

import android.graphics.Bitmap;
import com.ebensz.games.R;
import ice.node.OverlayParent;
import ice.node.widget.BitmapOverlay;
import ice.res.Res;
import ice.util.BitmapUtils;

/**
 * User: Mike.Hu
 * Date: 12-3-1
 * Time: 下午4:47
 */
public class NumberOverlay extends OverlayParent {

    public NumberOverlay(int number) {

        String numberString = String.valueOf(number);
        numberLength = numberString.length();
        if (numberLength < 7) {
            Bitmap source = Res.getBitmap(R.drawable.point_numbers);
            int sourceWidth = source.getWidth();
            int sourceHeight = source.getHeight();
            int tempValue = sourceWidth / 10;
            int eachValue = tempValue + (float) (sourceWidth % 10) / (float) tempValue > 0.6 ? 1 : 0;
            numberOverlay = new BitmapOverlay[numberLength];
            Bitmap numbersBitmap[] = new Bitmap[10];
            for (int i = 0; i < 10; i++) {
                int right = (i + 1) * eachValue;
                if (right > sourceWidth)
                    right = sourceWidth;
                numbersBitmap[i] = BitmapUtils.slices(source,i * eachValue, 0, right, sourceHeight);
            }
            for(int i=0;i<numberLength;i++){
                numberOverlay[i] = new BitmapOverlay(numbersBitmap[Integer.valueOf(Character.toString(numberString.charAt(i)))]);
                numberOverlay[i].setPos(this.getPosX(),this.getPosY());
                addChild(numberOverlay[i]);
            }
        } else {

        }
    }

    public void setNumber(int number){

    }

    public int getNumberLength() {
        return numberLength;
    }

    private BitmapOverlay numberOverlay[];
    private int numberLength;
}
