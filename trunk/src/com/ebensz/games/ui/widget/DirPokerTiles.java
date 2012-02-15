package com.ebensz.games.ui.widget;

import android.graphics.Bitmap;
import android.graphics.Point;
import com.ebensz.games.R;
import com.ebensz.games.model.hand.ColoredHand;
import com.ebensz.games.model.poker.ColoredPoker;
import com.ebensz.games.model.poker.Poker;
import com.ebensz.games.res.LoadRes;
import com.ebensz.games.scenes.GameSceneBase;
import ice.animation.AlphaAnimation;
import ice.animation.Animation;
import ice.animation.TranslateAnimation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: Mike.Hu
 * Date: 11-11-30
 * Time: 上午10:16
 */
public abstract class DirPokerTiles {

    public DirPokerTiles(GameSceneBase gameScene) {
        this.gameScene = gameScene;
        shouPai = new ArrayList<PokerTile>();
        chuPai = new ArrayList<PokerTile>();

        posProvider = onCreatePosProvider();
    }

    protected abstract DirPositionProvider onCreatePosProvider();

    public void clearPokers() {
        for (PokerTile pokerTile : shouPai) {
            pokerTile.setRemovable(true);
        }
        shouPai.clear();

        for (PokerTile pokerTile : chuPai) {
            pokerTile.setRemovable(true);
        }
        chuPai.clear();
    }

    public void faPai(PokerTile pokerTile, int maxSize) {
        PokerTile copy = pokerTile.copy();
        copy.setRemovable(false);
        shouPai.add(copy);
        gameScene.addChild(copy);

        int size = shouPai.size();
        Point point = posProvider.getShouPaiPos(size - 1, maxSize);

        copy.startAnimation(
                TranslateAnimation.createMoveBy(1000, point.x - copy.getPosX(), point.y - copy.getPosY())
        );
    }


    public void faPaiRemainThree(PokerTile[] remainThree) {
        Bitmap bitmap = LoadRes.getBitmap(R.drawable.poker_back_small);
        for (PokerTile pokerTile : remainThree) {
            PokerTile copy = pokerTile.copy();
            copy.setBitmap(bitmap);
            faPai(copy, 20);
            //copy.enableHover();
        }

        tidyShouPai(100);
    }

    public void tidyShouPai(long time) {

        for (int i = 0; i < shouPai.size(); i++) {
            PokerTile tile = shouPai.get(i);

            Animation tileAnimation = tile.getAnimation();
            if (tileAnimation != null) continue;

            Point point = posProvider.getShouPaiPos(i, shouPai.size());

            float deltaX = point.x - tile.getPosX();
            float deltaY = point.y - tile.getPosY();

            tile.startAnimation(TranslateAnimation.createMoveBy(time, deltaX, deltaY));
        }
    }


    public void chuPai(ColoredHand chuPai) {
        Poker[] pokers = chuPai.getHand().getPokers();
        List<PokerTile> chuPaiTiles = new ArrayList<PokerTile>(pokers.length);
        List<ColoredPoker> coloredPokers = chuPai.getColoredPokers();

        for (ColoredPoker coloredPoker : coloredPokers) {

            for (PokerTile tile : shouPai) {

                if (tile.getColoredPoker().equals(coloredPoker)) {
                    chuPaiTiles.add(tile);
                    tile.setRemovable(true);
                }

            }

        }

        shouPai.removeAll(chuPaiTiles);

        for (int i = 0, size = chuPaiTiles.size(); i < size; i++) {
            PokerTile tile = chuPaiTiles.get(i);
            PokerTile tileCopy = tile.copy();

            gameScene.addChild(tileCopy);
            this.chuPai.add(tileCopy);

            Point point = posProvider.getChuPaiPos(i, size);

            tileCopy.startAnimation(
                    TranslateAnimation.createMoveBy(300, point.x - tileCopy.getPosX(), point.y - tileCopy.getPosY())
            );

        }

        tidyShouPai(50);
    }

    public void hideLastChuPai() {
        for (Iterator<PokerTile> iterator = this.chuPai.iterator(); iterator.hasNext(); ) {
            PokerTile next = iterator.next();

            next.startAnimation(AlphaAnimation.createFadeOut(300));
            iterator.remove();

        }
    }

    @Override
    public String toString() {
        return "DirPokerTiles{" +
                "shouPai=" + shouPai +
                '}';
    }

    protected DirPositionProvider posProvider;
    protected List<PokerTile> shouPai;
    protected List<PokerTile> chuPai;
    protected GameSceneBase gameScene;
}
