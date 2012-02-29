package com.ebensz.games.ui.widget;

import android.graphics.Point;
import ice.engine.EngineContext;

/**
 * User: Mike.Hu
 * Date: 11-11-29
 * Time: 下午12:08
 */
public class LeftPokerTiles extends DirPokerTiles {
    private static final int SHOU_PAI_MARGIN = 30;
    private static final int SHOU_PAI_X = 70;

    private static final int CHU_PAI_MARGIN = 40;
    private static final int CHU_PAI_CENTER_X = 400;
    private static final int CHU_PAI_CENTER_Y = 500;

    public void sortAndMakeFront() {

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

        int totalWidth = (size - 1) * CHU_PAI_MARGIN;
        int startX = CHU_PAI_CENTER_X - totalWidth / 2;

        return new Point(startX + index * CHU_PAI_MARGIN, CHU_PAI_CENTER_Y);
    }

    private Point calShouPaiPos(int index, int size) {
        int totalHeight = (size - 1) * SHOU_PAI_MARGIN;
        int startY = (EngineContext.getAppHeight() - totalHeight) / 2;
        return new Point(SHOU_PAI_X, startY + index * SHOU_PAI_MARGIN);
    }
}
