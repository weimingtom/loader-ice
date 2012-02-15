package com.ebensz.games.ui.widget;

import android.graphics.Bitmap;
import android.graphics.Point;
import com.ebensz.games.model.hand.ColoredHand;
import com.ebensz.games.model.poker.ColoredPoker;
import com.ebensz.games.res.LoadRes;
import com.ebensz.games.scenes.GameSceneBase;
import ice.engine.EngineContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: Mike.Hu
 * Date: 11-11-29
 * Time: 下午12:08
 */
public class LeftPokerTiles extends DirPokerTiles {
    private static final int SHOU_PAI_MARGIN = 30;
    private static final int SHOU_PAI_X = 0;
    private static final int CHU_PAI_MARGIN = 40;
    private static final int CHU_PAI_CENTER_X = 400;
    private static final int CHU_PAI_CENTER_Y = 300;


    public LeftPokerTiles(GameSceneBase gameScene) {
        super(gameScene);
    }

    public void sortAndMakeFront() {

        List<PokerTile> copy = new ArrayList<PokerTile>(shouPai.size());
        for (PokerTile pokerTile : shouPai) {
            copy.add(pokerTile.copy());
        }

        Collections.sort(copy);

        for (int i = 0; i < shouPai.size(); i++) {

            PokerTile pokerTile = copy.get(i);

            PokerTile shouPaiTile = shouPai.get(i);

            shouPaiTile.setColoredPoker(pokerTile.getColoredPoker());
            shouPaiTile.setBitmap(LoadRes.getFrontPoker(shouPaiTile.getColoredPoker()));
        }

    }

    @Override
    public void chuPai(ColoredHand chuPai) {
        List<ColoredPoker> coloredPokers = chuPai.getColoredPokers();

        for (PokerTile tile : shouPai) {
            if (coloredPokers.contains(tile.getColoredPoker())) {
                Bitmap bitmap = LoadRes.getFrontPoker(tile.getColoredPoker());
                tile.setBitmap(bitmap);
            }
        }

        super.chuPai(chuPai);
    }

    @Override
    protected DirPositionProvider onCreatePosProvider() {
        return new DirPositionProvider() {
            @Override
            public Point getShouPaiPos(int index, int size) {
                return calShouPaiPos(index, size);
            }

            @Override
            public Point getChuPaiPos(int index, int size) {
                return calChuPaiPos(index, size);
            }
        };
    }

    private Point calChuPaiPos(int index, int size) {
        int eachWidth = LoadRes.getOutsidePokerWidth();

        int totalWidth = (size - 1) * CHU_PAI_MARGIN + eachWidth;
        int startX = CHU_PAI_CENTER_X - totalWidth / 2;

        return new Point(startX + index * CHU_PAI_MARGIN, CHU_PAI_CENTER_Y);
    }

    private Point calShouPaiPos(int index, int size) {
        int eachHeight = LoadRes.getLeftShouPaiHeight();

        int totalHeight = (size - 1) * SHOU_PAI_MARGIN + eachHeight;
        int startY = (EngineContext.getAppHeight() - totalHeight) / 2;
        return new Point(SHOU_PAI_X, startY + index * SHOU_PAI_MARGIN);
    }
}
