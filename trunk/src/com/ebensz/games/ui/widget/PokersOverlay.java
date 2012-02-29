package com.ebensz.games.ui.widget;

import android.graphics.PointF;
import com.ebensz.games.model.Dir;
import com.ebensz.games.model.hand.ColoredHand;
import com.ebensz.games.model.poker.ColoredPoker;
import com.ebensz.games.res.LoadRes;
import ice.animation.TranslateAnimation;
import ice.graphic.gl_status.DepthController;
import ice.node.OverlayParent;

/**
 * 一副牌的tiles
 * User: Mike.Hu
 * Date: 11-12-2
 * Time: 下午2:44
 */
public class PokersOverlay extends OverlayParent {

    public static final int TOTAL_WIDTH = 800;

    public PokersOverlay() {
        outsidePokerTiles = new OutsidePokerTiles();
        leftPokerTiles = new LeftPokerTiles();
        rightPokerTiles = new RightPokerTiles();

        addChildren(leftPokerTiles, outsidePokerTiles, rightPokerTiles);

        addGlStatusController(new DepthController(true));
    }

    public void faPai(Dir dir, ColoredPoker coloredPoker) {

        DirPokerTiles dirPokerTiles = getDirPoker(dir);
        dirPokerTiles.faPai(coloredPoker);
    }

    public DirPokerTiles getDirPoker(Dir dir) {
        switch (dir) {
            case Outside:
                return outsidePokerTiles;
            case Left:
                return leftPokerTiles;
            case Right:
                return rightPokerTiles;
        }

        return null;
    }

    public void tidy() {

        for (int i = 0; i < 3; i++) {
            PokerOverlay pokerOverlay = (PokerOverlay) get(i);
            PointF point = calEachPos(i, 3);
            pokerOverlay.startAnimation(new TranslateAnimation(700, point.x - pokerOverlay.getPosX(), 0));
        }
    }

    private PointF calEachPos(int index, int size) {
        float pokerWidth = LoadRes.getPokerWidth();
        float maxMargin = pokerWidth;
        int margin = 1;
        float totalWidth = (size - 1) * margin + pokerWidth;

        while (totalWidth < TOTAL_WIDTH && margin <= maxMargin) {
            margin += 2;
            totalWidth = (size - 1) * margin;
        }

        float startX = (1024 - totalWidth) / 2;

        return new PointF(startX + index * margin, 768 / 2);
    }

    public void clean() {
        outsidePokerTiles.clear();
        leftPokerTiles.clear();
        rightPokerTiles.clear();
    }

    public OutsidePokerTiles getOutsidePokerTiles() {
        return outsidePokerTiles;
    }


    public void showChuPai(Dir dir, ColoredHand chuPai) {
        DirPokerTiles dirPoker = getDirPoker(dir);

        if (dirPoker.getChuPai().size() != 0)
            dirPoker.hideLastChuPai();

        dirPoker.showChuPai(chuPai);
    }

    public void hideLastChuPai(Dir jiePaiDir) {
        getDirPoker(jiePaiDir).hideLastChuPai();
    }

    private OutsidePokerTiles outsidePokerTiles;
    private DirPokerTiles leftPokerTiles;
    private DirPokerTiles rightPokerTiles;
}
