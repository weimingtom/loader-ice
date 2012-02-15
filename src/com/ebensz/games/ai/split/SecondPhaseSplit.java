package com.ebensz.games.ai.split;

import com.ebensz.games.ai.bean.AiHand;
import com.ebensz.games.ai.bean.AiPokerList;
import com.ebensz.games.ai.bean.HandType;
import com.ebensz.games.ai.bean.Solution;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: tosmart
 * Date: 2010-7-1
 * Time: 17:47:43
 */
public class SecondPhaseSplit {

    public static Solution parse(Solution orgSolution) {

        if (orgSolution.size() <= 8) return orgSolution;

        List<AiPokerList> bombs = orgSolution.pop(HandType.Bomb);
        if (bombs.size() == 0) return orgSolution;

        // 除第一个炸弹外，保留
        if (bombs.size() > 1) {
            for (int i = 1, size = bombs.size(); i < size; i++) {
                orgSolution.addHand((AiHand) bombs.get(i));
            }
        }

        // 利用分散的如下元素
        // 对子 + 单张 + 3连对中的带牌 + 3带1或3带2的带牌 + 第1个炸弹中的3张牌
        // 构造一个PokerList
        AiPokerList pokers = buildPokerListUseHighCards(orgSolution, bombs);

        // 对此进行常规分析
        Solution solution = GeneralSplit.parse(pokers);

        List<AiPokerList> singeLinks = solution.pop(HandType.SingleLink);
        List<AiPokerList> pairLinks = solution.pop(HandType.PairLink);

        List<AiPokerList> singles = solution.pop(HandType.Single);
        List<AiPokerList> pairs = solution.pop(HandType.Pair);

        // 如果生成了一个以上的连子或连对
        if (singeLinks.size() == 0 && pairLinks.size() == 0) {

            orgSolution.addHand((AiHand) bombs.get(0));

            // 没有生成连子或连对，就生成了3带1或3带2

            List<AiPokerList> list_x3_1Ai = solution.pop(HandType.Type_x3_1);
            for (AiPokerList t3b : list_x3_1Ai) {
                singles.add(retrieveDragPart(t3b));
            }

            List<AiPokerList> list_x3_2Ai = solution.pop(HandType.Type_x3_2);
            for (AiPokerList t3b : list_x3_2Ai) {
                pairs.add(retrieveDragPart(t3b));
            }

            output(orgSolution, singles, pairs);
        }
        else {

            for (AiPokerList singleLink : singeLinks) {
                orgSolution.addHand(new AiHand(singleLink, HandType.SingleLink));
            }

            for (AiPokerList pairLink : pairLinks) {
                orgSolution.addHand(new AiHand(pairLink, HandType.PairLink));
            }

            // 把先前炸弹拆剩余的一张牌加入，和最近一次分析剩余的散牌再组成新的PokerList

            AiPokerList newAiPokerList = new AiPokerList();
            newAiPokerList.add(new char[]{bombs.get(0).getMaxChar()});

            for (AiPokerList pl : singles) {
                newAiPokerList.add(pl);
            }

            for (AiPokerList pl : pairs) {
                newAiPokerList.add(pl);
            }

            solution = GeneralSplit.parse(newAiPokerList);

            singles = solution.pop(HandType.Single);
            pairs = solution.pop(HandType.Pair);

            List<AiPokerList> list_x3_1Ai = solution.pop(HandType.Type_x3_1);
            for (AiPokerList x3_1 : list_x3_1Ai) {
                orgSolution.addHand((AiHand) x3_1);
            }

            List<AiPokerList> list_x3_2Ai = solution.pop(HandType.Type_x3_2);
            for (AiPokerList x3_2 : list_x3_2Ai) {
                orgSolution.addHand((AiHand) x3_2);
            }

            output(orgSolution, singles, pairs);
        }

        return orgSolution;
    }

    private static AiPokerList buildPokerListUseHighCards(
            Solution orgSolution, List<AiPokerList> bombs) {

        AiPokerList pokers = new AiPokerList();

        char bombId = bombs.get(0).getMaxChar();
        pokers.add(new char[]{bombId, bombId, bombId});

        List<AiPokerList> orgPairs = orgSolution.pop(HandType.Pair);
        for (AiPokerList pair : orgPairs) {
            pokers.add(pair);
        }

        List<AiPokerList> orgSingles = orgSolution.pop(HandType.Single);
        for (AiPokerList single : orgSingles) {
            pokers.add(single);
        }

        List<AiPokerList> orgThreeLink1xn = orgSolution.pop(HandType.ThreeLink_1xn);
        for (AiPokerList t3b : orgThreeLink1xn) {
            pokers.add(retrieveDragPart(t3b));
            orgSolution.addHand(new AiHand(t3b, HandType.ThreeLink));
        }

        List<AiPokerList> orgThreeLink2xn = orgSolution.pop(HandType.ThreeLink_2xn);
        for (AiPokerList t3b : orgThreeLink2xn) {
            pokers.add(retrieveDragPart(t3b));
            orgSolution.addHand(new AiHand(t3b, HandType.ThreeLink));
        }

        List<AiPokerList> orgThree_x3_1 = orgSolution.pop(HandType.Type_x3_1);
        for (AiPokerList t3b : orgThree_x3_1) {
            pokers.add(retrieveDragPart(t3b));
            orgSolution.addHand(new AiHand(t3b, HandType.Type_x3));
        }

        List<AiPokerList> orgThree_x3_2 = orgSolution.pop(HandType.Type_x3_2);
        for (AiPokerList t3b : orgThree_x3_2) {
            pokers.add(retrieveDragPart(t3b));
            orgSolution.addHand(new AiHand(t3b, HandType.Type_x3));
        }

        return pokers;
    }

    private static void output(Solution orgSolution, List<AiPokerList> singles, List<AiPokerList> pairs) {

        // three links

        List<AiPokerList> threeLinks = orgSolution.pop(HandType.ThreeLink);
        Collections.sort(threeLinks, GeneralSplit.POKER_LIST_COMPARATOR);

        for (AiPokerList link : threeLinks) {

            int length = link.size() / 3;

            int singleCount = singles.size();

            if (singleCount >= length) {

                char[] ids = new char[length];

                for (int i = 0; i < ids.length; i++) {
                    ids[i] = singles.remove(singleCount - 1 - i).getMinChar();
                }

                link.add(new AiPokerList(ids));
                orgSolution.addHand(new AiHand(link, HandType.ThreeLink_1xn));

                continue;
            }

            int pairCount = pairs.size();
            if (pairCount >= length) {

                char[] ids = new char[length * 2];
                for (int i = 0; i < length; i++) {
                    char id = pairs.remove(pairCount - 1 - i).getMinChar();
                    ids[i * 2] = id;
                    ids[i * 2 + 1] = id;
                }

                link.add(new AiPokerList(ids));
                orgSolution.addHand(new AiHand(link, HandType.ThreeLink_2xn));

                continue;
            }

            orgSolution.addHand(new AiHand(link, HandType.ThreeLink));
        }

        // three blocks

        List<AiPokerList> x3Blocks = orgSolution.pop(HandType.Type_x3);
        Collections.sort(x3Blocks, GeneralSplit.POKER_LIST_COMPARATOR);

        for (AiPokerList block : x3Blocks) {

            int singleCount = singles.size();
            if (singleCount != 0) {

                char[] ids = {singles.remove(singleCount - 1).getMinChar()};
                block.add(new AiPokerList(ids));
                orgSolution.addHand(new AiHand(block, HandType.Type_x3_1));

                continue;
            }

            int pairCount = pairs.size();
            if (pairCount > 0) {

                char id = pairs.remove(pairCount - 1).getMinChar();
                block.add(new AiPokerList(new char[]{id, id}));
                orgSolution.addHand(new AiHand(block, HandType.Type_x3_2));

                continue;
            }

            orgSolution.addHand(new AiHand(block, HandType.Type_x3));
        }

        // add the singles and pairs

        for (AiPokerList pair : pairs) {
            orgSolution.addHand(new AiHand(pair, HandType.Pair));
        }

        for (AiPokerList single : singles) {
            orgSolution.addHand(new AiHand(single, HandType.Single));
        }
    }

    private static AiPokerList retrieveDragPart(AiPokerList withDrag) {

        Map<Character, Integer> counter = withDrag.getCounter();
        Set<Character> idSet = counter.keySet();

        AiPokerList drag = new AiPokerList();

        for (char id : idSet) {
            int count = counter.get(id);
            if (count > 0 && count < 3) {
                char[] ids = count < 2 ? new char[]{id} : new char[]{id, id};
                drag.add(ids);
            }
        }

        withDrag.remove(drag);

        return drag;
    }
}
