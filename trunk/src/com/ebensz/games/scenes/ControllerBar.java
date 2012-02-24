package com.ebensz.games.scenes;

import com.ebensz.games.R;
import ice.engine.EngineContext;
import ice.node.OverlayParent;
import ice.node.widget.ButtonOverlay;

/**
 * User: Jason
 * Date: 12-2-4
 * Time: 下午4:05
 */
public class ControllerBar extends OverlayParent {


    public ControllerBar() {

        serviceBtn = new ButtonOverlay(R.drawable.service_title, R.drawable.service_title);

        soundBtn = new ButtonOverlay(R.drawable.sound, R.drawable.sound);
        soundBtn.setPos(100, 0);

        exitBtn = new ButtonOverlay(R.drawable.exit, R.drawable.exit);
        exitBtn.setPos(200, 0);

        addChildren(serviceBtn, soundBtn, exitBtn);

        setPos(
                0.7f * EngineContext.getAppWidth(),
                EngineContext.getAppHeight() - 50
        );
    }

    public ButtonOverlay getExitBtn() {
        return exitBtn;
    }

    public ButtonOverlay getSoundBtn() {
        return soundBtn;
    }

    private ButtonOverlay serviceBtn;
    private ButtonOverlay soundBtn;
    private ButtonOverlay exitBtn;
}