package com.ebensz.games.ui.widget;

import android.graphics.Point;

/**
 * User: Mike.Hu
 * Date: 11-12-2
 * Time: 下午5:20
 */
public interface DirPositionProvider {

    Point getShouPaiPos(int index, int size);

    Point getChuPaiPos(int index, int size);

}
