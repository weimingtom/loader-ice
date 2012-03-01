package com.ebensz.games.ui.widget;

import android.graphics.Point;

import java.util.List;

/**
 * User: Mike.Hu
 * Date: 11-11-29
 * Time: 下午12:08
 */
public class RightPokerTiles extends LeftRightBase {
    private static final int CHU_PAI_MARGIN = 40;

    private static final int CHU_PAI_CENTER_X = 700;
    private static final int CHU_PAI_CENTER_Y = 500;

    @Override
    protected DirPositionProvider onCreatePosProvider() {
        return new DirPositionProvider() {
            @Override
            public Point getShouPaiPos(int index, int size) {
                return new Point(900, 400);
            }

            @Override
            public Point getChuPaiPos(int index, int size) {
                return calChuPaiPos(index, size);
            }
        };
    }

    @Override
    public void faPaiRemainThree(List<PokerOverlay> remainThree) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private Point calChuPaiPos(int index, int size) {

        int totalWidth = (size - 1) * CHU_PAI_MARGIN;
        int startX = CHU_PAI_CENTER_X - totalWidth / 2;

        return new Point(startX + index * CHU_PAI_MARGIN, CHU_PAI_CENTER_Y);
    }

}
