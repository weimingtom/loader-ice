package com.ebensz.games.ui.widget;

import android.graphics.Bitmap;
import com.ebensz.games.R;
import ice.node.OverlayParent;
import ice.node.widget.BitmapOverlay;
import ice.res.Res;
import ice.util.BitmapUtils;

/**
 * User: Mike.Hu
 * Date: 12-2-24
 * Time: 下午4:01
 */
public class LimitNumberOverlay extends OverlayParent{


    public LimitNumberOverlay(int width,int height) {

        super();

        Bitmap bgBitmap = Res.getBitmap(R.drawable.title_block_bg);
       // bg = new BitmapOverlay(bgBitmap.getWidth(),);
    }

    private BitmapOverlay bg;
    private BitmapOverlay leftIcon;
    private BitmapOverlay rightNumber;
}
