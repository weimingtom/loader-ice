package com.ebensz.games.model.hand;

import com.ebensz.games.model.poker.Poker;

/**
 * User: Mike.Hu
 * Date: 11-12-22
 * Time: 下午2:55
 */
public abstract class Hand implements Comparable<Hand> {
    public static final Class<? extends Hand>[] DEFAULT_ORDER = new Class[]{
            Rocket.class, Bomb.class, X4_X2.class, ThreeLink.class,
            PairLink.class, X3.class, SingleLink.class, Pair.class, Single.class
    };

    public static final Hand BU_YAO = null;

    protected Hand(Poker key) {
        if (key == null) throw new IllegalStateException("getKey must not be null !");

        this.key = key;
    }

    public final Poker getKey() {
        return key;
    }

    public abstract Poker[] getPokers();

    public abstract int size();

    @Override
    public String toString() {
        return getClass().getSimpleName() + " --> " + key;
    }

    @Override
    public int compareTo(Hand another) {
        Class<? extends Hand> myClass = getClass();
        Class<? extends Hand> anotherClass = another.getClass();

        if (myClass == anotherClass) {
            return key.ordinal() - another.key.ordinal();
        }
        else {
            int myTypeIndex = findTypeIndex(myClass, DEFAULT_ORDER);

            int anotherTypeIndex = findTypeIndex(anotherClass, DEFAULT_ORDER);

            return anotherTypeIndex - myTypeIndex;
        }

    }

    private int findTypeIndex(Class<? extends Hand> target, Class<? extends Hand>[] defaultOrder) {

        for (int i = 0; i < defaultOrder.length; i++) {
            if (defaultOrder[i] == target)
                return i;
        }

        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hand hand = (Hand) o;

        if (key != hand.key) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }


    protected Poker key;
}
