package com.ebensz.games.scenes;

import com.ebensz.games.ui.widget.LeftPokerTiles;
import com.ebensz.games.ui.widget.OutsidePokerTiles;
import com.ebensz.games.ui.widget.PackOfCardTiles;
import com.ebensz.games.ui.widget.RightPokerTiles;
import ice.engine.Scene;

/**
 * User: Mike.Hu
 * Date: 11-11-14
 * Time: 下午3:11
 */
public abstract class GameSceneBase extends Scene {

    public GameSceneBase() {
        outsidePokers = new OutsidePokerTiles(this);
        leftPokers = new LeftPokerTiles(this);
        rightPokers = new RightPokerTiles(this);
        gameControllerBar = new GameControllerBar();
        gameControllerBar.setPos(getWidth() / 2, 748);
    }

    public void reset() {
        outsidePokers.clearPokers();
        leftPokers.clearPokers();
        rightPokers.clearPokers();

        if (packOfCardTiles != null)
            packOfCardTiles.clear();
    }

    public GameControllerBar getGameControllerBar() {
        return gameControllerBar;
    }

    protected GameControllerBar gameControllerBar;
    protected PackOfCardTiles packOfCardTiles;
    protected OutsidePokerTiles outsidePokers;
    protected LeftPokerTiles leftPokers;
    protected RightPokerTiles rightPokers;
}
