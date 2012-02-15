package com.ebensz.games.model.hand;

import com.ebensz.games.model.poker.Poker;

import java.util.Arrays;

/**
 * User: Mike.Hu
 * Date: 11-12-22
 * Time: 下午3:41
 */
public class SingleLink extends Hand {

    public SingleLink(Poker minPoker, int length) {
        super(minPoker);

        if (length < 5 || Poker.values()[minPoker.ordinal() + length - 1].isBiggerThan(Poker.A))
            throw new IllegalArgumentException("" + minPoker + "," + length);

        this.length = length;
    }

    @Override
    public Poker[] getPokers() {
        Poker[] allPokers = Poker.values();

        Poker[] pokers = new Poker[length];

        System.arraycopy(allPokers, key.ordinal(), pokers, 0, length);

        Arrays.sort(pokers, Poker.BIGGER_TO_SMALL_COMPARATOR);

        return pokers;
    }

    @Override
    public int size() {
        return length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SingleLink that = (SingleLink) o;

        if (length != that.length) return false;

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
