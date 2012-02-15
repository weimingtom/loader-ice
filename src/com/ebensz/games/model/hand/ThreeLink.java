package com.ebensz.games.model.hand;

import com.ebensz.games.model.poker.Poker;

import java.util.Arrays;

/**
 * User: Mike.Hu
 * Date: 11-12-22
 * Time: 下午4:11
 */
public class ThreeLink extends ExtensibleHand {

    public ThreeLink(Poker key, int length, Hand[] extensions) {
        super(key, extensions);

        if (length < 2 || Poker.values()[key.ordinal() + length - 1].isBiggerThan(Poker.A))
            throw new IllegalArgumentException("" + key + "," + length);

        this.length = length;
    }

    @Override
    public Poker[] getPokers() {
        int size = size();

        Poker[] allPokers = Poker.values();

        Poker[] pokers = new Poker[size];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(allPokers, key.ordinal(), pokers, i * length, length);
        }

        for (int i = 0; i < pokers.length; i++) {
            Poker poker = pokers[i];
            System.out.println("three link poker = " + poker);
        }

        Arrays.sort(pokers, 0, length * 3, Poker.BIGGER_TO_SMALL_COMPARATOR);

        if (size == length * 3)
            return pokers;

        int startIndex = length * 3;
        for (Hand extension : extensions) {
            int extensionSize = extension.size();
            System.arraycopy(extension.getPokers(), 0, pokers, startIndex, extensionSize);
            startIndex += extensionSize;
        }


        return pokers;
    }

    @Override
    public int size() {
        if (extensions == null)
            return 3 * length;

        int eachExtentionSize = extensions[0].size();

        return (3 + eachExtentionSize) * length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ThreeLink threeLink = (ThreeLink) o;

        if (length != threeLink.length) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + length;
        return result;
    }

    private int length;
}
