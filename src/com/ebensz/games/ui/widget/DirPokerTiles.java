package com.ebensz.games.ui.widget;

import android.graphics.Point;
import com.ebensz.games.model.hand.ColoredHand;
import com.ebensz.games.model.poker.ColoredPoker;
import com.ebensz.games.model.poker.Poker;
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
        shouPai = new ArrayList<PokerOverlay>();
        chuPai = new ArrayList<PokerOverlay>();

        posProvider = onCreatePosProvider();
    }

    protected abstract DirPositionProvider onCreatePosProvider();

    public void clearPokers() {
        for (PokerOverlay pokerOverlay : shouPai) {
            pokerOverlay.setRemovable(true);
        }
        shouPai.clear();

        for (PokerOverlay pokerOverlay : chuPai) {
            pokerOverlay.setRemovable(true);
        }
        chuPai.clear();
    }

    public abstract void faPai(int index, PokerOverlay pokerOverlay, int maxSize);


    public void faPaiRemainThree(PokerOverlay[] remainThree) {

        int i = 17;

        for (PokerOverlay pokerOverlay : remainThree) {
            faPai(i++, pokerOverlay, 20);
            //copy.enableHover();
        }

        tidyShouPai(100);
    }

    public void tidyShouPai(long time) {

        for (int i = 0; i < shouPai.size(); i++) {
            PokerOverlay overlay = shouPai.get(i);

            Animation tileAnimation = overlay.getAnimation();
            if (tileAnimation != null) continue;

            Point point = posProvider.getShouPaiPos(i, shouPai.size());

            float deltaX = point.x - overlay.getPosX();
            float deltaY = point.y - overlay.getPosY();

            overlay.startAnimation(new TranslateAnimation(time, deltaX, deltaY));
        }
    }


    public void chuPai(ColoredHand chuPai) {
        Poker[] pokers = chuPai.getHand().getPokers();
        List<PokerOverlay> chuPaiOverlays = new ArrayList<PokerOverlay>(pokers.length);
        List<ColoredPoker> coloredPokers = chuPai.getColoredPokers();

        for (ColoredPoker coloredPoker : coloredPokers) {

            for (PokerOverlay overlay : shouPai) {

                if (overlay.getColoredPoker().equals(coloredPoker)) {
                    chuPaiOverlays.add(overlay);
                }

            }

        }

        shouPai.removeAll(chuPaiOverlays);

        for (int i = 0, size = chuPaiOverlays.size(); i < size; i++) {
            PokerOverlay overlay = chuPaiOverlays.get(i);
            this.chuPai.add(overlay);

            Point point = posProvider.getChuPaiPos(i, size);

            overlay.startAnimation(
                    new TranslateAnimation(300, point.x - overlay.getPosX(), point.y - overlay.getPosY())
            );

        }

        tidyShouPai(50);
    }

    public void hideLastChuPai() {
        for (Iterator<PokerOverlay> iterator = this.chuPai.iterator(); iterator.hasNext(); ) {
            PokerOverlay next = iterator.next();

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
    protected List<PokerOverlay> shouPai;
    protected List<PokerOverlay> chuPai;
    protected GameSceneBase gameScene;
}
