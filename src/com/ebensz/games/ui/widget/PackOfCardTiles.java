package com.ebensz.games.ui.widget;

import android.graphics.Point;
import android.graphics.PointF;
import com.ebensz.games.model.Dir;
import com.ebensz.games.model.poker.ColoredPoker;
import com.ebensz.games.res.LoadRes;
import com.ebensz.games.utils.SleepUtils;
import ice.animation.TranslateAnimation;
import ice.graphic.gl_status.DepthController;
import ice.node.OverlayParent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 一副牌的tiles
 * User: Mike.Hu
 * Date: 11-12-2
 * Time: 下午2:44
 */
public class PackOfCardTiles extends OverlayParent {

    public static final int TOTAL_WIDTH = 800;

    public PackOfCardTiles(List<Dir> order, Map<Dir, List<ColoredPoker>> shouPaiMap, List<ColoredPoker> leftThree) {
        List<PokerOverlay> allPokerOverlays = new ArrayList<PokerOverlay>();

        Point center = new Point(1024 / 2, 768 / 2);

        for (ColoredPoker poker : leftThree) {
            PokerOverlay pokerOverlay = new PokerOverlay(poker);
            pokerOverlay.setPos(center.x, center.y);
            pokerOverlay.setFront(false);
            allPokerOverlays.add(pokerOverlay);
        }

        List<Dir> reverseOrder = new ArrayList(order.size());
        for (int i = order.size() - 1; i >= 0; i--) {
            reverseOrder.add(order.get(i));
        }

        for (int i = 0; i < 17; i++) {
            for (Dir dir : reverseOrder) {
                PokerOverlay pokerOverlay = new PokerOverlay(shouPaiMap.get(dir).get(i));
                pokerOverlay.setPos(center.x, center.y);
                pokerOverlay.setFront(false);
                allPokerOverlays.add(pokerOverlay);
            }
        }

        addChildren(allPokerOverlays);

        addGlStatusController(new DepthController(true));
    }

    public void showLeftThree() {
        for (int i = 0; i < 3; i++) {
            PokerOverlay pokerOverlay = (PokerOverlay) children.get(i);
            pokerOverlay.rotateToFront();
        }

        SleepUtils.sleep(1000);
    }

    public void tidy() {

        for (int i = 0; i < 3; i++) {
            PokerOverlay pokerOverlay = (PokerOverlay) children.get(i);
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

}
