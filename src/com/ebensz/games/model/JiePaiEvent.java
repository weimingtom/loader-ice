package com.ebensz.games.model;

import com.ebensz.games.model.hand.ColoredHand;

/**
 * User: Mike.Hu
 * Date: 11-12-5
 * Time: 上午10:16
 */
public class JiePaiEvent {

    public JiePaiEvent(Dir chuPaiDir, ColoredHand chuPai, Dir jiePaiDir) {
        this.chuPaiDir = chuPaiDir;
        this.chuPai = chuPai;
        this.jiePaiDir = jiePaiDir;
    }

    public Dir chuPaiDir;
    public ColoredHand chuPai;

    public Dir jiePaiDir;
    public ColoredHand jiePai;

}
