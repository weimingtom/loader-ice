package com.ebensz.games.scenes;

import com.ebensz.games.ui.widget.*;
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
    }

    public void reset() {
        outsidePokers.clearPokers();
        leftPokers.clearPokers();
        rightPokers.clearPokers();

        if (packOfCardTiles != null)
            packOfCardTiles.clear();


    }

    protected PackOfCardTiles packOfCardTiles;
    protected OutsidePokerTiles outsidePokers;
    protected LeftPokerTiles leftPokers;
    protected RightPokerTiles rightPokers;
}
