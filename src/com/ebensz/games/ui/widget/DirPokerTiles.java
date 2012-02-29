package com.ebensz.games.ui.widget;

import android.graphics.Point;
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


    public void showChuPai(ColoredHand chuPai) {

        List<ColoredPoker> coloredPokers = chuPai.getColoredPokers();

        for (int i = 0, size = coloredPokers.size(); i < size; i++) {

            ColoredPoker coloredPoker = coloredPokers.get(i);

            PokerOverlay poker = new PokerOverlay(coloredPoker);

            Point point = posProvider.getChuPaiPos(i, coloredPokers.size());
            poker.setPos(point.x, point.y, size() * 0.2f);

            addChild(poker);
            this.chuPai.add(poker);

            poker.startAnimation(AlphaAnimation.createFadeIn(100));
        }
    }

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
