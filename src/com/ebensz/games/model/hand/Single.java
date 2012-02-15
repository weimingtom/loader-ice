package com.ebensz.games.model.hand;

import com.ebensz.games.model.poker.Poker;

/**
 * User: Mike.Hu
 * Date: 11-12-22
 * Time: 下午3:32
 */
public class Single extends Hand {

    public Single(Poker poker) {
        super(poker);
    }

    @Override
    public Poker[] getPokers() {
        return new Poker[]{key};
    }

    @Override
    public int size() {
        return 1;
    }

}
