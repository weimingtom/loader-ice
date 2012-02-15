package com.ebensz.games.ai.engine;

import com.ebensz.games.ai.bean.AiHand;
import com.ebensz.games.ai.bean.AiPokerList;
import com.ebensz.games.ai.bean.HandType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class PromptDeliverUnit {

    public static final String ALL_POKERS = "3456789abcdexyz";
    public static final String LINK_POKERS = "3456789abcde";

    public static AiPokerList deliverPrompt(AiPokerList remainder, AiPokerList hadChosedPokers) {

        //只找符合连子顺子的情况
        if (hadChosedPokers.size() == 2) {
            return whenCurrSizeIsTwoForDeliver(remainder, hadChosedPokers);
        }
        else if (hadChosedPokers.size() == 3) {
            return whenCurrSizeIsThreeForDeliver(remainder, hadChosedPokers);
        }
        else if (hadChosedPokers.size() == 4) {
            return whenCurrSizeIsFourForDeliver(remainder, hadChosedPokers);
        }

        return null;
    }

    private static AiPokerList whenCurrSizeIsFourForDeliver(
            AiPokerList remainder, AiPokerList hadChosedPokers) {

        //只提为顺子的情况
        Map<Character, Integer> counter = remainder.getCounter();

        if (counter.get(hadChosedPokers.get(0)) > 1
                || counter.get(hadChosedPokers.get(3)) > 1) {

            return null;
        }

        int cMaxIndex = ALL_POKERS.indexOf(hadChosedPokers.get(0));
        int cMinIndex = ALL_POKERS.indexOf(hadChosedPokers.get(3));
        char[] allPokers = ALL_POKERS.toCharArray();

        if (cMaxIndex - cMinIndex == 3) {

            for (int i = cMinIndex; i < cMaxIndex; i++) {
                if (hadChosedPokers.getCounter().get(allPokers[i]) > 1) {
                    return null;
                }
            }

            //向后扩展
            if (cMinIndex > 0) {

                for (char c = allPokers[cMinIndex - 1];
                     ALL_POKERS.indexOf(c) >= 0;
                     c = allPokers[ALL_POKERS.indexOf(c) - 1]) {

                    if (counter.get(c) != 1) break;
                    hadChosedPokers.add(c);

                    if (ALL_POKERS.indexOf(c) == 0) break;
                }
            }

            //向前扩展
            if (cMaxIndex < LINK_POKERS.length() - 1) {

                for (char c = allPokers[cMaxIndex + 1];
                     ALL_POKERS.indexOf(c) <= LINK_POKERS.length() - 1;
                     c = allPokers[ALL_POKERS.indexOf(c) + 1]) {

                    int cOfCounter = counter.get(c);
                    if (cOfCounter != 1 && (hadChosedPokers.size() >= 5 || cOfCounter < 1)) {
                        break;
                    }

                    hadChosedPokers.add(c);
                }
            }

            if (hadChosedPokers.size() >= 5) return hadChosedPokers;
        }

        return null;
    }

    public static AiPokerList contactPrompt(
            AiPokerList remainder, AiHand lastAiHand, AiPokerList hadChosedPokers) {

        if (lastAiHand.getType() == HandType.Single) return null;

        if ((hadChosedPokers.contains('y') || hadChosedPokers.contains('z'))
                && (remainder.contains('y') && remainder.contains('z'))) {

            return new AiPokerList(new char[]{'y', 'z'});
        }

        if (lastAiHand.getType() == HandType.Pair) {

            if (hadChosedPokers.getMaxChar() > lastAiHand.getMaxChar()
                    && hadChosedPokers.size() == 1
                    && remainder.getCounter().get(hadChosedPokers.get(0)) >= 2) {

                hadChosedPokers.add(hadChosedPokers.get(0));
                return hadChosedPokers;
            }
            else if (hadChosedPokers.getMaxChar() < lastAiHand.getMaxChar()
                    && hadChosedPokers.size() == 1
                    && remainder.getCounter().get(hadChosedPokers.get(0)) == 4) {

                char bomb_c = hadChosedPokers.get(0);
                return new AiPokerList(new char[]{bomb_c, bomb_c, bomb_c, bomb_c});
            }
            else {
                return null;
            }
        }
        else {

            List<AiPokerList> plans = PokerTypeUnit.tryContact(remainder, lastAiHand);
            if (lastAiHand.getType() != HandType.Bomb) {

                Set<Entry<Character, Integer>> entries = remainder.getCounter().entrySet();
                for (Entry<Character, Integer> entry : entries) {

                    if (entry.getValue() == 4) {

                        Character bomb_c = entry.getKey();
                        AiPokerList bomb = new AiPokerList(new char[]{bomb_c, bomb_c, bomb_c, bomb_c});

                        if (bomb.getList().containsAll(hadChosedPokers.getList())) {
                            plans.add(bomb);
                            break;
                        }
                    }
                }
            }

            if (plans.size() == 0) return null;

            List<AiPokerList> containsChosedPokeListAi = new ArrayList<AiPokerList>();

            for (AiPokerList plan : plans) {
                if (plan.getList().containsAll(hadChosedPokers.getList())) {
                    containsChosedPokeListAi.add(plan);
                }
            }

            if (containsChosedPokeListAi.size() == 0) return null;

            AiPokerList plan0 = containsChosedPokeListAi.get(0);
            for (int j = 1; j < containsChosedPokeListAi.size(); j++) {
                AiPokerList plan = containsChosedPokeListAi.get(j);
                plan0.getList().retainAll(plan.getList());
            }

            if (plan0.size() == 0) return null;

            return plan0;
        }
    }

    private static AiPokerList whenCurrSizeIsTwoForDeliver(
            AiPokerList remainderOfChar, AiPokerList hadChosedPokers) {

        Map<Character, Integer> counter = remainderOfChar.getCounter();
        int c1Index = ALL_POKERS.indexOf(hadChosedPokers.get(0));
        int c0Index = ALL_POKERS.indexOf(hadChosedPokers.get(1));

        char c1 = hadChosedPokers.get(0);
        char c0 = hadChosedPokers.get(1);
        char[] allPokers = ALL_POKERS.toCharArray();

        if (c1Index == ALL_POKERS.indexOf('x'))
            return null;

        if (c1Index == c0Index) {
            //相等的两张牌   如果不是连子  提三张提起

            boolean isLiangZi = false;
            if (c0Index >= ALL_POKERS.indexOf('5')) {
                isLiangZi = counter.get(allPokers[c0Index - 1]) >= 2 && counter.get(allPokers[c0Index - 2]) >= 2;
            }

            if (c0Index <= ALL_POKERS.indexOf('c')) {
                isLiangZi = isLiangZi || (counter.get(allPokers[c0Index + 1]) >= 2 && counter.get(allPokers[c0Index + 2]) >= 2);
            }

            if (c0Index >= ALL_POKERS.indexOf('4') && c0Index <= ALL_POKERS.indexOf('d')) {
                isLiangZi = isLiangZi || (counter.get(allPokers[c0Index - 1]) >= 2 && counter.get(allPokers[c0Index + 1]) >= 2);
            }

            if (!isLiangZi) {
                char c = allPokers[c0Index];
                return new AiPokerList(new char[]{c, c, c});
            }
            else {
                return null;
            }
        }

        if (counter.get(c1) >= 3 || counter.get(c0) >= 3) {
            //排除三带的情况
            return null;
        }

        if (c1Index - c0Index <= 2) {                //连子、顺子

            boolean c0HasTwo = counter.get(c0) >= 2;
            boolean c1HasTwo = counter.get(c1) >= 2;
            boolean cAnotherHasTwo;

            if (c1Index - c0Index == 1) {
                cAnotherHasTwo = c0Index > 0 && counter.get(ALL_POKERS.charAt(c0Index - 1)) >= 2;
                cAnotherHasTwo = cAnotherHasTwo || (c1Index < LINK_POKERS.length() - 1 && counter.get(ALL_POKERS.charAt(c1Index + 1)) >= 2);
            }
            else {
                cAnotherHasTwo = counter.get(ALL_POKERS.charAt(c1Index - 1)) >= 2;
            }

            if (c0HasTwo && c1HasTwo && cAnotherHasTwo) {
                //排除连子情况
                return null;
            }
        }

        //向后扩展
        if (c0Index > 0) {

            for (char c = allPokers[c0Index - 1];
                 ALL_POKERS.indexOf(c) >= 0;
                 c = allPokers[ALL_POKERS.indexOf(c) - 1]) {

                if (counter.get(c) != 1) break;
                hadChosedPokers.add(c);

                if (ALL_POKERS.indexOf(c) == 0) break;
            }
        }

        //向中间扩展
        if (c1Index - c0Index > 1) {

            for (char c = allPokers[c1Index - 1];
                 ALL_POKERS.indexOf(c) > c0Index;
                 c = allPokers[ALL_POKERS.indexOf(c) - 1]) {

                int cOfCounter = counter.get(c);
                if (cOfCounter < 1) return null;

                hadChosedPokers.add(c);
            }
        }

        //向前扩展
        if (c1Index < LINK_POKERS.length() - 1) {

            for (char c = allPokers[c1Index + 1];
                 ALL_POKERS.indexOf(c) <= LINK_POKERS.length() - 1;
                 c = allPokers[ALL_POKERS.indexOf(c) + 1]) {

                int cOfCounter = counter.get(c);
                if (cOfCounter != 1 && (hadChosedPokers.size() >= 5 || cOfCounter < 1)) {
                    break;
                }

                hadChosedPokers.add(c);
            }
        }

        if (hadChosedPokers.size() >= 5) return hadChosedPokers;

        return null;
    }
    //三带二问题  、三张独立的可以提起  --- 提连子时 别人可能想三带 99 888 77

    private static AiPokerList whenCurrSizeIsThreeForDeliver(AiPokerList remainderOfChar, AiPokerList pokesOfChar) {

        int c2Index = ALL_POKERS.indexOf(pokesOfChar.get(0));
        int c1Index = ALL_POKERS.indexOf(pokesOfChar.get(1));
        int c0Index = ALL_POKERS.indexOf(pokesOfChar.get(2));

        Map<Character, Integer> counter = remainderOfChar.getCounter();
        char[] allPokers = ALL_POKERS.toCharArray();

        if (c1Index == c2Index && c1Index == c0Index) return null;

        if (c1Index != c2Index && c1Index != c0Index && c2Index != ALL_POKERS.indexOf('x')) {                 //三张不相等的牌、只可能为顺子 （如果为连子中的牌  则不提）

            if (c2Index - c0Index == 2) {

                boolean isLianZi = counter.get(allPokers[c2Index]) >= 2
                        && counter.get(allPokers[c1Index]) >= 2
                        && counter.get(allPokers[c0Index]) >= 2;

                //排除连子情况
                if (isLianZi) return null;
            }

            //向后扩展
            if (c0Index > 0) {

                for (char c = allPokers[c0Index - 1];
                     ALL_POKERS.indexOf(c) >= 0;
                     c = allPokers[ALL_POKERS.indexOf(c) - 1]) {

                    if (counter.get(c) != 1) break;
                    pokesOfChar.add(c);

                    if (ALL_POKERS.indexOf(c) == 0) break;
                }
            }
            //向中间扩展
            boolean midDisconnection = false;
            if (c2Index - c0Index > 1) {

                for (char c = allPokers[c2Index - 1];
                     ALL_POKERS.indexOf(c) > c0Index;
                     c = allPokers[ALL_POKERS.indexOf(c) - 1]) {

                    int cOfCounter = counter.get(c);
                    if (cOfCounter >= 1) {
                        if (ALL_POKERS.indexOf(c) != c1Index) pokesOfChar.add(c);
                    }
                    else {
                        midDisconnection = true;
                        break;
                    }
                }
            }

            //向前扩展
            if (c2Index < LINK_POKERS.length() - 1) {

                for (char c = allPokers[c2Index + 1];
                     ALL_POKERS.indexOf(c) <= LINK_POKERS.length() - 1;
                     c = allPokers[ALL_POKERS.indexOf(c) + 1]) {

                    int cOfCounter = counter.get(c);
                    if (cOfCounter != 1 && (pokesOfChar.size() >= 5 || cOfCounter < 1)) {
                        break;
                    }

                    pokesOfChar.add(c);
                }
            }

            if (!midDisconnection && pokesOfChar.contains(allPokers[c2Index])
                    && pokesOfChar.contains(allPokers[c1Index])
                    && pokesOfChar.size() >= 5) {

                return pokesOfChar;
            }
        }

        //三带、连子的情况（如果某张牌有四张或者为双顺的情况  则return null，）
        if (c0Index == c1Index || c1Index == c2Index) {                    // 77 8 或 7 88

            if (counter.get(allPokers[c0Index]) == 4
                    || counter.get(allPokers[c1Index]) == 4
                    || counter.get(allPokers[c2Index]) == 4) {
                //是炸弹牌型的不加入预选
                return null;
            }

            if (counter.get(allPokers[c0Index]) == 3
                    && counter.get(allPokers[c2Index]) == 3) {
                //存在双顺情况的不加入预选
                return null;
            }

            if (c2Index - c0Index <= 2 && c2Index != ALL_POKERS.indexOf('x')) {                                                    // 存在连子的情况

                boolean isLianZi;
                boolean c0HasTwo = counter.get(allPokers[c0Index]) >= 2;
                boolean c2HasTwo = counter.get(allPokers[c2Index]) >= 2;
                AiPokerList cAnother = new AiPokerList();

                // 取更小部分
                boolean cAnotherHasTwo = false;
                if (c0Index > 0) {

                    int c2LastIndexCount = counter.get(allPokers[c0Index - 1]);
                    cAnotherHasTwo = c0Index > 0 && (c2LastIndexCount >= 2 && c2LastIndexCount < 4);

                    if (cAnotherHasTwo) {
                        char c = allPokers[c0Index - 1];
                        cAnother.add(new char[]{c, c});
                    }
                }

                //取中间的
                if (c2Index - c0Index > 1) {
                    for (int i = c0Index; i <= c2Index; i++) {
                        char c = allPokers[i];
                        if (counter.get(c) < 2) return null;
                        cAnother.add(new char[]{c, c});
                    }
                }

                // 取更大部分
                if (c2Index < LINK_POKERS.length() - 1) {

                    int c2NextIndexCount = counter.get(allPokers[c2Index + 1]);
                    if (cAnother.size() > 0) {
                        cAnotherHasTwo = c2NextIndexCount >= 2 && c2NextIndexCount < 4;
                    }
                    else {
                        cAnotherHasTwo = c2NextIndexCount >= 2;
                    }
                }

                if (cAnotherHasTwo) {
                    char c = allPokers[c2Index + 1];
                    cAnother.add(new char[]{c, c});
                }
                isLianZi = c0HasTwo && c2HasTwo && cAnother.size() > 0;

                char c0 = allPokers[c0Index], c2 = allPokers[c2Index];
                if (isLianZi && (counter.get(c0) == 3 || counter.get(c2) == 3)) {
                    return new AiPokerList(new char[]{c0, c0, c2, c2});
                }

                if (isLianZi) {

                    if (pokesOfChar.getCounter().get(allPokers[c2Index]) == 2) {
                        pokesOfChar.add(allPokers[c0Index]);
                    }
                    else {
                        pokesOfChar.add(allPokers[c2Index]);
                    }

                    pokesOfChar.add(cAnother);
                    if (pokesOfChar.size() > 5) return pokesOfChar;
                }
            }

            //三带
            if (counter.get(allPokers[c0Index]) == 3) {

                char c_3 = allPokers[c0Index];
                pokesOfChar.add(c_3);

                if (pokesOfChar.getCounter().get(c_3) == 2) {
                    pokesOfChar.add(c_3);
                }

                return pokesOfChar;
            }
            else if (counter.get(allPokers[c2Index]) == 3) {

                char c_3 = allPokers[c2Index];
                pokesOfChar.add(c_3);

                if (pokesOfChar.getCounter().get(c_3) == 2) {
                    pokesOfChar.add(c_3);
                }

                return pokesOfChar;
            }
        }

        return null;
    }
}
