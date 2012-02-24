package com.ebensz.games.scenes;

import android.graphics.Bitmap;
import com.ebensz.games.R;
import ice.node.OverlayParent;
import ice.node.widget.BitmapOverlay;
import ice.node.widget.ButtonOverlay;
import ice.res.Res;

/**
 * User: Mike.Hu
 * Date: 12-2-23
 * Time: 下午4:06
 */
public class GameControllerBar extends OverlayParent {

    public GameControllerBar() {

        BitmapOverlay controllerBg = new BitmapOverlay(Res.getBitmap(R.drawable.title));
        controllerBg.setPos(this.getPosX(), this.getPosY());
        addChild(controllerBg);

        float componentHeight = this.getPosY() - 10;

        Bitmap pointBgBitmap = Res.getBitmap(R.drawable.title_block_bg);
        BitmapOverlay pointBg = new BitmapOverlay(100, pointBgBitmap.getHeight());
        pointBg.setBitmap(R.drawable.title_block_bg);
        pointBg.setPos(this.getPosX() - 306, componentHeight);
        addChild(pointBg);

        BitmapOverlay pointIconBitmap = new BitmapOverlay(Res.getBitmap(R.drawable.point_title));
        pointIconBitmap.setPos(this.getPosX() - 336, componentHeight);
        addChild(pointIconBitmap);

        BitmapOverlay bombBg = new BitmapOverlay(76, pointBgBitmap.getHeight());
        bombBg.setBitmap(R.drawable.title_block_bg);
        bombBg.setPos(this.getPosX() - 200, componentHeight);
        addChild(bombBg);

        BitmapOverlay bombIcon = new BitmapOverlay(Res.getBitmap(R.drawable.bomb));
        bombIcon.setPos(this.getPosX() - 210, componentHeight);
        addChild(bombIcon);

        serviceButton = new ButtonOverlay(R.drawable.service_title,R.drawable.service_title,R.drawable.service_title);
        serviceButton.setPos(this.getPosX()+150,componentHeight);
        addChild(serviceButton);

        soundButton = new ButtonOverlay(R.drawable.sound_title,R.drawable.sound_title,R.drawable.sound_title);
        soundButton.setPos(this.getPosX()+240,componentHeight);
        addChild(soundButton);

        backButton = new ButtonOverlay(R.drawable.back_title,R.drawable.back_title,R.drawable.back_title);
        backButton.setPos(this.getPosX()+330,componentHeight);
        addChild(backButton);

    }


    public ButtonOverlay getServiceButton() {
        return serviceButton;
    }

    public ButtonOverlay getSoundButton() {
        return soundButton;
    }

    public ButtonOverlay getBackButton() {
        return backButton;
    }

    private ButtonOverlay serviceButton;
    private ButtonOverlay soundButton;
    private ButtonOverlay backButton;
}
