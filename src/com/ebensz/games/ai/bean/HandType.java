package com.ebensz.games.ai.bean;

/**
 * User: tosmart
 * Date: 2010-6-28
 * Time: 17:58:44
 */
public enum HandType {

    Rocket(0),
    Bomb(1),
    Type_x4_2x2(2),
    Type_x4_2(3),
    ThreeLink(4),
    ThreeLink_2xn(5),
    ThreeLink_1xn(6),
    Type_x3(7),
    Type_x3_2(8),
    Type_x3_1(9),
    PairLink(10),
    SingleLink(11),
    Pair(12),
    Single(13);

    HandType(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    public int getSortIndex() {
        return sortIndex;
    }

    private int sortIndex;
}
