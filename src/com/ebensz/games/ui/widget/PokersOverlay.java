package com.ebensz.games.ui.widget;

import android.graphics.PointF;
import com.ebensz.games.model.Dir;
import com.ebensz.games.model.hand.ColoredHand;
import com.ebensz.games.model.poker.ColoredPoker;
import com.ebensz.games.res.LoadRes;
import com.ebensz.games.utils.SleepUtils;
import ice.animation.TranslateAnimation;
import ice.engine.EngineContext;
import ice.graphic.gl_status.DepthController;
import ice.node.OverlayParent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 一副牌的tiles
 * User: Mike.Hu
 * Date: 11-12-2
 * Time: 下午2:44
 */
public class PokersOverlay extends OverlayParent {

    public static final int TOTAL_WIDTH = 800;
    private static final int MARGIN = 160;

    public PokersOverlay() {
        outsidePokerTiles = new OutsidePokerTiles();
        leftPokerTiles = new LeftPokerTiles();
        rightPokerTiles = new RightPokerTiles();

        addChildren(leftPokerTiles, outsidePokerTiles, rightPokerTiles);

        addGlStatusController(new DepthController(true));

        threePokers = new ArrayList<PokerOverlay>(3);
    }

    public void showFaPai(List<Dir> order, Map<Dir, List<ColoredPoker>> shouPaiMap, List<ColoredPoker> leftThree) {

        FaPaiAnimation faPaiAnimation = new FaPaiAnimation();

        addChild(faPaiAnimation);

        faPaiAnimation.start();

        for (int i = 0; i < 17; i++) {

            for (Dir dir : order) {

                getDirPoker(dir).faPai(shouPaiMap.get(dir).get(i));

                SleepUtils.sleep(100);

            }

        }

        faPaiAnimation.stop();
        remove(faPaiAnimation);

        showThree(leftThree);

        //展开剩余的三张牌
        SleepUtils.sleep(700);
    }

    private void showThree(List<ColoredPoker> leftThree) {
        threePokers.clear();
        Collections.sort(leftThree);

        int appWidth = EngineContext.getAppWidth();
        int appHeight = EngineContext.getAppHeight();

        for (int i = 0; i < 3; i++) {
            ColoredPoker coloredPoker = leftThree.get(i);

            PokerOverlay newPoker = new PokerOverlay(coloredPoker);
            newPoker.setPos(
                    (appWidth / 2 - MARGIN) + i * MARGIN,
                    appHeight / 2,
                    size() * 0.2f
            );

            newPoker.setUseBack(true);
            newPoker.setFront(false);

            threePokers.add(newPoker);
        }

        addChildren(threePokers);
    }

    public void showFaPaiLeftThree(Dir loaderDir, List<ColoredPoker> leftThree) {
        showThreeFront();

        remove(threePokers);

        getDirPoker(loaderDir).faPaiRemainThree(threePokers);
    }

    private void showThreeFront() {
        for (PokerOverlay poker : threePokers) {
            poker.rotateToFront();
        }

        SleepUtils.sleep(2000);
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
        outsidePokerTiles.reset();
        leftPokerTiles.reset();
        rightPokerTiles.reset();
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

    private List<PokerOverlay> threePokers;

    private OutsidePokerTiles outsidePokerTiles;
    private DirPokerTiles leftPokerTiles;
    private DirPokerTiles rightPokerTiles;
}
