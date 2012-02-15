package com.ebensz.games.model.hand;

import com.ebensz.games.model.poker.Poker;

/**
 * User: Mike.Hu
 * Date: 11-12-22
 * Time: 下午3:54
 */
public class Rocket extends Hand {

    public Rocket() {
        super(Poker.DaWang);
    }

    @Override
    public Poker[] getPokers() {
        return new Poker[]{Poker.DaWang, Poker.XiaoWang};
    }

    @Override
    public int size() {
        return 2;
    }


}
