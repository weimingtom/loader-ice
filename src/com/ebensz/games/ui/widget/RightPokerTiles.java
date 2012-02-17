package com.ebensz.games.ui.widget;

import android.graphics.Point;
import com.ebensz.games.res.LoadRes;
import com.ebensz.games.scenes.GameSceneBase;
import ice.animation.AnimationGroup;
import ice.animation.RotateAnimation;
import ice.animation.TranslateAnimation;
import ice.engine.EngineContext;

/**
 * User: Mike.Hu
 * Date: 11-11-29
 * Time: 下午12:08
 */
public class RightPokerTiles extends DirPokerTiles {
    private static final int SHOU_PAI_MARGIN = 30;
    private static final int SHOU_PAI_X = 950;
    private static final int CHU_PAI_MARGIN = 40;

    private static final int CHU_PAI_CENTER_X = 700;
    private static final int CHU_PAI_CENTER_Y = 300;

    public RightPokerTiles(GameSceneBase gameScene) {
        super(gameScene);
    }

    public void sortAndMakeFront() {
    }

    @Override
    public void faPai(int index, PokerOverlay pokerOverlay, int maxSize) {
        shouPai.add(pokerOverlay);

        Point point = posProvider.getShouPaiPos(index, maxSize);

        AnimationGroup group = new AnimationGroup();
        group.add(new TranslateAnimation(1000, point.x - pokerOverlay.getPosX(), point.y - pokerOverlay.getPosY()));
        group.add(new RotateAnimation(1000, -90));

        pokerOverlay.startAnimation(group);
    }


    @Override
    protected DirPositionProvider onCreatePosProvider() {
        return new DirPositionProvider() {
            @Override
            public Point getShouPaiPos(int index, int size) {
                return calShouPaiPos(index, size);
            }

            @Override
            public Point getChuPaiPos(int index, int size) {
                return calChuPaiPos(index, size);
            }
        };
    }

    private Point calChuPaiPos(int index, int size) {
        int eachWidth = LoadRes.getPokerWidth();

        int totalWidth = (size - 1) * CHU_PAI_MARGIN + eachWidth;
        int startX = CHU_PAI_CENTER_X - totalWidth / 2;

        return new Point(startX + index * CHU_PAI_MARGIN, CHU_PAI_CENTER_Y);
    }


    private Point calShouPaiPos(int index, int size) {
        int eachHeight = LoadRes.getPokerHeight();

        int totalHeight = (size - 1) * SHOU_PAI_MARGIN;
        int startY = (EngineContext.getAppHeight() - totalHeight) / 2;
        return new Point(SHOU_PAI_X, startY + index * SHOU_PAI_MARGIN);
    }

}
