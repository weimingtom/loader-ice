package com.ebensz.games.model.hand;

import com.ebensz.games.model.poker.Poker;

/**
 * User: Mike.Hu
 * Date: 11-12-22
 * Time: 下午3:56
 */
public class X3 extends ExtensibleHand {

    public X3(Poker key, Hand[] extensions) {
        super(key, extensions);
    }

    @Override
    public Poker[] getPokers() {

        int size = size();

        Poker[] pokers = new Poker[size];
        pokers[0] = key;
        pokers[1] = key;
        pokers[2] = key;

        if (size == 3)
            return pokers;

        System.arraycopy(extensions[0].getPokers(), 0, pokers, 3, size - 3);

        return pokers;
    }

    @Override
    public int size() {
        if (extensions == null)
            return 3;

        int extentionSize = extensions[0].size();

        return 3 + extentionSize;
    }


}
