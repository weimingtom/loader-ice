package com.ebensz.games.model.poker;


import java.util.Comparator;

public enum Poker {
    _3,
    _4,
    _5,
    _6,
    _7,
    _8,
    _9,
    _10,
    J,
    Q,
    K,
    A,
    _2,
    /**
     * 小王
     */
    XiaoWang,
    /**
     * 大王
     */
    DaWang;

    public static final Comparator<Poker> BIGGER_TO_SMALL_COMPARATOR = new Comparator<Poker>() {
        @Override
        public int compare(Poker pokerA, Poker pokerB) {
            return pokerB.ordinal() - pokerA.ordinal();
        }
    };


    public boolean isBiggerThan(Poker poker) {
        return this.ordinal() - poker.ordinal() > 0;
    }

    public boolean isSmallerThan(Poker poker) {
        return this.ordinal() - poker.ordinal() < 0;
    }

    public boolean equals(Poker poker) {
        return this.ordinal() - poker.ordinal() == 0;
    }

}
