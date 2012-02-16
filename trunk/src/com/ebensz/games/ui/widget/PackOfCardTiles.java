package com.ebensz.games.ui.widget;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import com.ebensz.games.R;
import com.ebensz.games.model.Dir;
import com.ebensz.games.model.poker.ColoredPoker;
import com.ebensz.games.res.LoadRes;
import com.ebensz.games.scenes.GameScene;
import com.ebensz.games.utils.SleepUtils;
import ice.animation.TranslateAnimation;
import ice.node.Overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 一副牌的tiles
 * User: Mike.Hu
 * Date: 11-12-2
 * Time: 下午2:44
 */
public class PackOfCardTiles {
    private static final String TAG = PackOfCardTiles.class.getSimpleName();
    public static final int TOTAL_WIDTH = 800;

    public PackOfCardTiles(List<Dir> order, Map<Dir, List<ColoredPoker>> shouPaiMap, List<ColoredPoker> leftThree) {
        allPokerTiles = new ArrayList<PokerTile>(54);
        Point center = new Point(1024 >> 1, 768 >> 1);

        Bitmap bitmap = LoadRes.getBitmap(R.drawable.poker_back_small);
        for (ColoredPoker poker : leftThree) {
            PokerTile pokerTile = new PokerTile(poker, bitmap);
            pokerTile.setPos(center.x, center.y);
            allPokerTiles.add(pokerTile);
        }

        List<Dir> reverseOrder = new ArrayList(order.size());
        for (int i = order.size() - 1; i >= 0; i--) {
            reverseOrder.add(order.get(i));
        }


        for (int i = 0; i < 17; i++) {
            for (Dir dir : reverseOrder) {
                PokerTile pokerTile = new PokerTile(shouPaiMap.get(dir).get(i), bitmap);
                pokerTile.setPos(center.x, center.y);
                allPokerTiles.add(pokerTile);
            }
        }

    }

    public void show(GameScene gameScene) {
        gameScene.addChildren(new ArrayList<Overlay>(allPokerTiles));
        SleepUtils.sleep(500);
    }

    public void clear() {
        for (PokerTile pokerTile : allPokerTiles) {
            pokerTile.setRemovable(true);
        }
        allPokerTiles.clear();
    }

    public PokerTile sendOut() {
        PokerTile top = top();
        top.setRemovable(true);
        //tidy();
        return top;
    }

    public void showLeftThree() {
        for (int i = 0; i < 3; i++) {
            PokerTile pokerTile = allPokerTiles.get(i);
            Bitmap bitmap = LoadRes.getFrontPoker(pokerTile.getColoredPoker());
            pokerTile.setBitmap(bitmap);
        }

        tidy();

        SleepUtils.sleep(1000);
    }

    private PokerTile top() {
        return allPokerTiles.remove(allPokerTiles.size() - 1);
    }

    public void tidy() {
        for (PokerTile tile : allPokerTiles) {
            PointF point = calEachPos(tile);
            tile.startAnimation(TranslateAnimation.createMoveBy(50, point.x - tile.getPosX(), 0));
        }
    }

    private PointF calEachPos(PokerTile tile) {
        float maxMargin = tile.getWidth();
        int margin = 1;
        float totalWidth = (allPokerTiles.size() - 1) * margin + tile.getWidth();
        while (totalWidth < TOTAL_WIDTH && margin <= maxMargin) {
            margin += 2;
            totalWidth = (allPokerTiles.size() - 1) * margin + tile.getWidth();
        }

        float startX = (1024 - totalWidth) / 2;
        int index = allPokerTiles.indexOf(tile);
        return new PointF(startX + index * margin, tile.getPosY());
    }

    private List<PokerTile> allPokerTiles;
}
