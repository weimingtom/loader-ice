package com.ebensz.games.scenes;

import com.ebensz.games.ui.widget.PokersOverlay;
import ice.engine.Scene;

/**
 * User: Mike.Hu
 * Date: 11-11-14
 * Time: 下午3:11
 */
public abstract class GameSceneBase extends Scene {

    public GameSceneBase() {
        gameControllerBar = new GameControllerBar();
        gameControllerBar.setPos(getWidth() / 2, 748);
    }


    public GameControllerBar getGameControllerBar() {
        return gameControllerBar;
    }

    protected GameControllerBar gameControllerBar;
}
