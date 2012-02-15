package com.ebensz.games.model.hand;

import com.ebensz.games.model.poker.Poker;

import java.util.Arrays;

/**
 * User: Mike.Hu
 * Date: 11-12-22
 * Time: 下午3:50
 */
public class PairLink extends Hand {

    public PairLink(Poker minPoker, int length) {
        super(minPoker);

        if (length < 3 || Poker.values()[minPoker.ordinal() + length - 1].isBiggerThan(Poker.A))
            throw new IllegalArgumentException("" + minPoker + "," + length);

        this.length = length;
    }

    @Override
    public Poker[] getPokers() {
        Poker[] allPokers = Poker.values();

        Poker[] pokers = new Poker[length * 2];

        System.arraycopy(allPokers, key.ordinal(), pokers, 0, length);
        System.arraycopy(pokers, 0, pokers, length, length);

        Arrays.sort(pokers, Poker.BIGGER_TO_SMALL_COMPARATOR);

        return pokers;
    }

    @Override
    public int size() {
        return length * 2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PairLink pairLink = (PairLink) o;

        if (length != pairLink.length) return false;

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
