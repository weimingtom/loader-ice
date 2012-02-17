package com.ebensz.games.ui.widget;

import android.graphics.Point;
import android.graphics.PointF;
import com.ebensz.games.model.Dir;
import com.ebensz.games.model.poker.ColoredPoker;
import com.ebensz.games.scenes.GameScene;
import com.ebensz.games.utils.SleepUtils;
import ice.animation.TranslateAnimation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * 一副牌的tiles
 * User: Mike.Hu
 * Date: 11-12-2
 * Time: 下午2:44
 */
public class PackOfCardTiles {
    public static final int TOTAL_WIDTH = 800;

    public PackOfCardTiles(List<Dir> order, Map<Dir, List<ColoredPoker>> shouPaiMap, List<ColoredPoker> leftThree) {
        allPokerOverlays = new Stack<PokerOverlay>();
        Point center = new Point(1024 >> 1, 768 >> 1);

        for (ColoredPoker poker : leftThree) {
            PokerOverlay pokerOverlay = new PokerOverlay(poker);
            pokerOverlay.setPos(center.x, center.y);
            pokerOverlay.setFront(false);
            allPokerOverlays.push(pokerOverlay);
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
                allPokerOverlays.push(pokerOverlay);
            }
        }

    }

    public void show(GameScene gameScene) {
        gameScene.addChildren(allPokerOverlays);
        SleepUtils.sleep(500);
    }

    public void clear() {
        for (PokerOverlay pokerOverlay : allPokerOverlays) {
            pokerOverlay.setRemovable(true);
        }
        allPokerOverlays.clear();
    }

    public PokerOverlay pop() {
        return allPokerOverlays.pop();
    }

    public void showLeftThree() {
        for (int i = 0; i < 3; i++) {
            PokerOverlay pokerOverlay = allPokerOverlays.get(i);
            pokerOverlay.rotateToFront();
        }

        SleepUtils.sleep(1000);
    }

    public void tidy() {
        for (PokerOverlay overlay : allPokerOverlays) {
            PointF point = calEachPos(overlay);
            overlay.startAnimation(new TranslateAnimation(700, point.x - overlay.getPosX(), 0));
        }
    }

    private PointF calEachPos(PokerOverlay pokerOverlay) {
        float pokerWidth = pokerOverlay.getPokerWidth();
        float maxMargin = pokerWidth;
        int margin = 1;
        float totalWidth = (allPokerOverlays.size() - 1) * margin + pokerWidth;
        while (totalWidth < TOTAL_WIDTH && margin <= maxMargin) {
            margin += 2;
            totalWidth = (allPokerOverlays.size() - 1) * margin + pokerWidth;
        }

        float startX = (1024 - totalWidth) / 2;
        int index = allPokerOverlays.indexOf(pokerOverlay);
        return new PointF(startX + index * margin, pokerOverlay.getPosY());
    }

    private Stack<PokerOverlay> allPokerOverlays;
}
