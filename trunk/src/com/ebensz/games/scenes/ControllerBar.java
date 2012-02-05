package com.ebensz.games.scenes;

import android.graphics.Bitmap;
import android.graphics.Point;
import com.ebensz.games.R;
import ice.engine.EngineContext;
import ice.node.Drawable;
import ice.node.DrawableParent;
import ice.node.widget.Button;

import javax.microedition.khronos.opengles.GL11;

/**
 * User: Jason
 * Date: 12-2-4
 * Time: 下午4:05
 */
public class ControllerBar extends DrawableParent<Drawable> {


    public ControllerBar() {

        serviceBtn = new Button(R.drawable.service, R.drawable.service);

        soundBtn = new Button(R.drawable.sound, R.drawable.sound);
        soundBtn.setPos(100, 0);

        exitBtn = new Button(R.drawable.exit, R.drawable.exit);
        exitBtn.setPos(200, 0);

        addChildren(serviceBtn, soundBtn, exitBtn);

        setPos(
                0.5f * EngineContext.getAppWidth(),
                EngineContext.getAppHeight()
        );
    }

    public Button getExitBtn() {
        return exitBtn;
    }

    public Button getSoundBtn() {
        return soundBtn;
    }

    private Button serviceBtn;
    private Button soundBtn;
    private Button exitBtn;
}