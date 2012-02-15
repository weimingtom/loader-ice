package com.ebensz.games.model;

import com.ebensz.games.model.hand.ColoredHand;

/**
 * User: Mike.Hu
 * Date: 11-12-5
 * Time: 上午10:16
 */
public class ChuPaiEvent {
    public ChuPaiEvent(Dir chuPaiDir) {
        this.chuPaiDir = chuPaiDir;
    }

    public Dir chuPaiDir;
    public ColoredHand chuPai;
}
