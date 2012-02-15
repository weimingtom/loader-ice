package com.ebensz.games.ai.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: tosmart
 * Date: 2010-6-30
 * Time: 9:11:15
 */
public class AiHand extends AiPokerList {

    public AiHand(char[] pokers, HandType type) {
        super(pokers);
        this.type = type;
    }

    public AiHand(AiPokerList aiPokerList, HandType type) {
        this.type = type;
        add(aiPokerList);
    }

    public HandType getType() {
        return type;
    }

    public Character getSortChar() {

        if (type == HandType.Type_x3_1
                || type == HandType.Type_x3_2
                || type == HandType.ThreeLink_1xn
                || type == HandType.ThreeLink_2xn) {

            Map<Character, Integer> counter = getCounter();
            List<Character> idList = new ArrayList<Character>(counter.keySet());
            Collections.sort(idList);

            char maxX3Id = 0;

            for (char id : idList) {
                if (counter.get(id) == 3) {
                    if (id > maxX3Id) maxX3Id = id;
                }
            }

            return maxX3Id;
        }

        if (type == HandType.Type_x4_2
                || type == HandType.Type_x4_2x2) {

            Map<Character, Integer> counter = getCounter();
            List<Character> idList = new ArrayList<Character>(counter.keySet());
            Collections.sort(idList);

            for (char id : idList) {
                if (counter.get(id) == 4) return id;
            }
        }

        return getMaxChar();
    }

    private HandType type;
}
