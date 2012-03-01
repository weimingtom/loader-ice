package com.ebensz.games.ui.widget;

import com.ebensz.games.model.hand.ColoredHand;
import com.ebensz.games.model.poker.ColoredPoker;
import com.ebensz.games.utils.SleepUtils;
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

    public abstract void faPai(ColoredPoker coloredPoker);

    public abstract void faPaiRemainThree(List<PokerOverlay> remainThree);

    public abstract void showChuPai(ColoredHand chuPai);

    public abstract void reset();

    public void hideLastChuPai() {
        for (Iterator<PokerOverlay> iterator = this.chuPai.iterator(); iterator.hasNext(); ) {
            PokerOverlay next = iterator.next();
            next.startAnimation(AlphaAnimation.createFadeOut(300));
            iterator.remove();
        }

        SleepUtils.sleep(300);
    }

    public List<PokerOverlay> getChuPai() {
        return chuPai;
    }

    protected DirPositionProvider posProvider;
    protected List<PokerOverlay> chuPai;

}
