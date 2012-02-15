package com.ebensz.games.ai.bean;


/**
 * User: tosmart
 * Date: 2010-6-28
 * Time: 17:58:44
 */
public enum SplitType {

    PT_10x2(20, HandType.PairLink),
    PT_9x2(18, HandType.PairLink),
    PT_8x2(16, HandType.PairLink),
    PT_7x2(14, HandType.PairLink),
    PT_6x2(12, HandType.PairLink),
    PT_5x2(10, HandType.PairLink),
    PT_4x2(8, HandType.PairLink),
    PT_3x2(6, HandType.PairLink),
    // -
    PT_6x3(18, HandType.ThreeLink),
    PT_5x3(15, HandType.ThreeLink),
    PT_4x3(12, HandType.ThreeLink),
    PT_3x3(9, HandType.ThreeLink),
    PT_2x3(6, HandType.ThreeLink),
    PT_5x3_5x1(20, HandType.ThreeLink_1xn),
    PT_4x3_4x1(16, HandType.ThreeLink_1xn),
    PT_3x3_3x1(12, HandType.ThreeLink_1xn),
    PT_2x3_2x1(8, HandType.ThreeLink_1xn),
    PT_4x3_4x2(20, HandType.ThreeLink_2xn),
    PT_3x3_3x2(15, HandType.ThreeLink_2xn),
    PT_2x3_2x2(10, HandType.ThreeLink_2xn),
    // -
    PT_12x1(12, HandType.SingleLink),
    PT_11x1(11, HandType.SingleLink),
    PT_10x1(10, HandType.SingleLink),
    PT_9x1(9, HandType.SingleLink),
    PT_8x1(8, HandType.SingleLink),
    PT_7x1(7, HandType.SingleLink),
    PT_6x1(6, HandType.SingleLink),
    PT_5x1(5, HandType.SingleLink),
    // -
    PT_1x4(4, HandType.Bomb),
    PT_1x4_2(6, HandType.Type_x4_2),
    PT_1x4_2x2(8, HandType.Type_x4_2x2),
    // -
    PT_1x3(3, HandType.Type_x3),
    PT_1x3_1(4, HandType.Type_x3_1),
    PT_1x3_2x1(5, HandType.Type_x3_2),
    // -
    PT_YZ(2, HandType.Rocket),
    PT_1x2(2, HandType.Pair),
    PT_1(1, HandType.Single);

    SplitType(int length, HandType handType) {
        this.length = length;
        this.handType = handType;
    }

    public int length;
    public HandType handType;
}
