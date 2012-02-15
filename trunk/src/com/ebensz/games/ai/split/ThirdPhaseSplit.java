package com.ebensz.games.ai.split;

import com.ebensz.games.ai.bean.AiHand;
import com.ebensz.games.ai.bean.AiPokerList;
import com.ebensz.games.ai.bean.HandType;
import com.ebensz.games.ai.bean.Solution;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: tosmart
 * Date: 2010-7-2
 * Time: 8:48:34
 */
public class ThirdPhaseSplit {

    public static Solution parse(Solution solution) {

        if (solution.size() <= 8) return solution;

        List<AiPokerList> bombs = solution.pop(HandType.Bomb);
        if (bombs.size() == 0) return solution;

        // 除第一个炸弹外，保留
        if (bombs.size() > 1) {
            for (int i = 1, size = bombs.size(); i < size; i++) {
                solution.addHand((AiHand) bombs.get(i));
            }
        }

        // 取出其中的单牌和对子，将其与炸弹组合成4带2（对）

        List<AiPokerList> pairs = solution.pop(HandType.Pair);
        List<AiPokerList> singles = solution.pop(HandType.Single);

        int pairCount = pairs.size(), singleCount = singles.size();

        if (pairCount > singleCount && pairCount > 1) {

            Collections.sort(pairs, new Comparator<AiPokerList>() {
                public int compare(AiPokerList o1, AiPokerList o2) {
                    return o1.getMaxChar() - o2.getMaxChar();
                }
            });

            AiPokerList newHand = bombs.get(0);

            newHand.add(pairs.remove(0));
            newHand.add(pairs.remove(1));

            solution.addHand(
                    new AiHand(newHand, HandType.Type_x4_2x2)
            );

            for (AiPokerList pair : pairs) {
                solution.addHand((AiHand) pair);
            }
            for (AiPokerList single : singles) {
                solution.addHand((AiHand) single);
            }

            return solution;
        }

        if (singleCount > 1) {

            Collections.sort(singles, new Comparator<AiPokerList>() {
                public int compare(AiPokerList o1, AiPokerList o2) {
                    return o1.getMaxChar() - o2.getMaxChar();
                }
            });

            AiPokerList newHand = bombs.get(0);

            newHand.add(singles.remove(0));
            newHand.add(singles.remove(1));

            solution.addHand(
                    new AiHand(newHand, HandType.Type_x4_2)
            );

            for (AiPokerList pair : pairs) {
                solution.addHand((AiHand) pair);
            }
            for (AiPokerList single : singles) {
                solution.addHand((AiHand) single);
            }

            return solution;
        }

        for (AiPokerList pair : pairs) {
            solution.addHand((AiHand) pair);
        }
        for (AiPokerList single : singles) {
            solution.addHand((AiHand) single);
        }

        solution.addHand((AiHand) bombs.get(0));

        return solution;
    }
}
