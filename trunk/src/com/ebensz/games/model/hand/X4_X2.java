package com.ebensz.games.model.hand;

import com.ebensz.games.model.poker.Poker;

/**
 * User: Mike.Hu
 * Date: 11-12-22
 * Time: 下午4:25
 */
public class X4_X2 extends ExtensibleHand {

    public X4_X2(Poker key, Hand[] extensions) {
        super(key, extensions);

        if (extensions.length != 2)
            throw new IllegalArgumentException("should not be bomb !");
    }

    @Override
    public Poker[] getPokers() {
        int size = size();

        Poker[] pokers = new Poker[size];
        for (int i = 0; i < 4; i++) {
            pokers[i] = key;
        }


        int startIndex = 4;

        for (Hand extension : extensions) {
            int extensionSize = extension.size();
            System.arraycopy(extension.getPokers(), 0, pokers, startIndex, extensionSize);
            startIndex += extensionSize;
        }

        return pokers;
    }

    @Override
    public int size() {
        int eachExtensionSize = extensions[0].size();

        return 4 + eachExtensionSize * extensions.length;
    }


}
