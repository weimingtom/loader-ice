package com.ebensz.games.ui.widget;

import com.ebensz.games.model.hand.ColoredHand;
import com.ebensz.games.model.poker.ColoredPoker;
import ice.animation.AlphaAnimation;
import ice.node.OverlayParent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: Mike.Hu
 * Date: 11-11-30
 * Time: 上午10:16
 */
public abstract class DirPokerTiles extends OverlayParent {

    public DirPokerTiles() {
        chuPai = new ArrayList<PokerOverlay>();

        posProvider = onCreatePosProvider();
    }

    protected abstract DirPositionProvider onCreatePosProvider();

    public void clearPokers() {

        for (PokerOverlay pokerOverlay : chuPai) {
            pokerOverlay.setRemovable(true);
        }
        chuPai.clear();
    }

    public void faPai(ColoredPoker coloredPoker) {

    }


    public void faPaiRemainThree(List<ColoredPoker> remainThree) {

    }

    public void tidyShouPai(long time) {

    }


    public void chuPai(ColoredHand chuPai) {

    }

    public void hideLastChuPai() {
        for (Iterator<PokerOverlay> iterator = this.chuPai.iterator(); iterator.hasNext(); ) {
            PokerOverlay next = iterator.next();

            next.startAnimation(AlphaAnimation.createFadeOut(300));
            iterator.remove();

        }
    }

    protected DirPositionProvider posProvider;
    protected List<PokerOverlay> chuPai;
}
