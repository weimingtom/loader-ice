package com.ebensz.games.model.hand;

import com.ebensz.games.model.poker.Poker;

/**
 * User: Mike.Hu
 * Date: 11-12-22
 * Time: 下午3:39
 */
public class Pair extends Hand {

    public Pair(Poker key) {
        super(key);
    }

    @Override
    public Poker[] getPokers() {
        return new Poker[]{key, key};
    }

    @Override
    public int size() {
        return 2;
    }


}
