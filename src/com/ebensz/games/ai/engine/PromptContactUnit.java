package com.ebensz.games.ai.engine;

import com.ebensz.games.ai.bean.AiHand;
import com.ebensz.games.ai.bean.AiPokerList;
import com.ebensz.games.ai.bean.HandType;
import com.ebensz.games.ai.bean.Solution;

import java.util.*;
import java.util.Map.Entry;

public class PromptContactUnit {

    public static List<Entry<AiPokerList, Integer>> promptContact(
            AiPokerList remainderListAi, AiHand lastAiHand) {

        List<AiPokerList> plans = new ArrayList<AiPokerList>();
        plans.addAll(PokerTypeUnit.tryContact(remainderListAi, lastAiHand));

        List<AiPokerList> bombPlan = promptContactWithBomb(remainderListAi, lastAiHand);
        if (bombPlan != null) plans.addAll(plans.size(), bombPlan);
        if (plans.size() == 0) return null;

        List<AiHand> allContactPlan = new ArrayList<AiHand>();
        for (AiPokerList aiPokerList : plans) {
            allContactPlan.add(new AiHand(aiPokerList, lastAiHand.getType()));
        }

        Collections.sort(allContactPlan, new Solution.SolutionComparator());

        Map<AiPokerList, Integer> planMap = new TreeMap<AiPokerList, Integer>(
                new Comparator<AiPokerList>() {
                    @Override
                    public int compare(AiPokerList o1, AiPokerList o2) {
                        String s1 = o1.toDisplay(), s2 = o2.toDisplay();
                        return s1.equals(s2) ? 0 : -1;
                    }
                }
        );

        Solution solution;
        for (int i = 0; i < allContactPlan.size(); i++) {

            AiHand plan = allContactPlan.get(i);
            int size;

            remainderListAi.remove(plan);
            if (plan.contains('z') && plan.contains('y')) { //对鬼排序在最后
                size = 100 + i;
            }
            else {
                solution = Engine.getDefault().split(remainderListAi);
                size = solution.size();

                //对于存在的炸弹或者三张或者飞机的 手数值对应减少
                int bombSize = solution.peek(HandType.Bomb).size();
                int rocketSize = solution.peek(HandType.Rocket).size();
                int type_x3_size = solution.peek(HandType.Type_x3).size();
                int threeLink_size = solution.peek(HandType.ThreeLink).size();
                size = size - ((bombSize + rocketSize) * 2 + type_x3_size + threeLink_size * 2);
            }

            planMap.put(plan, size);
            remainderListAi.add(plan);
        }

        List<Entry<AiPokerList, Integer>> entryList = new ArrayList<Entry<AiPokerList, Integer>>(
                planMap.entrySet()
        );

        Collections.sort(entryList,
                new Comparator<Entry<AiPokerList, Integer>>() {
                    @Override
                    public int compare(Entry<AiPokerList, Integer> o1, Entry<AiPokerList, Integer> o2) {
                        return (o1.getValue() - o2.getValue());
                    }
                }
        );

        return entryList;
    }

    private static List<AiPokerList> promptContactWithBomb(AiPokerList remainderListAi, AiHand lastAiHand) {

        List<AiPokerList> bombsPlans = new ArrayList<AiPokerList>();
        Solution solution = Engine.getDefault().split(remainderListAi);
        List<AiPokerList> bombs = solution.peek(HandType.Bomb);

        if (lastAiHand.getType() == HandType.Bomb && bombs.size() > 0) {

            for (AiPokerList aiPokerList : bombs) {

                AiHand bomb = (AiHand) aiPokerList;

                if (bomb.getMaxChar() > lastAiHand.getMaxChar()) {
                    bombsPlans.add(bomb);
                }
            }
        }
        else if (bombs.size() > 0) {
            bombsPlans.addAll(bombs);
        }

        List<AiPokerList> rocket = solution.peek(HandType.Rocket);
        if (rocket.size() > 0) bombsPlans.add(rocket.get(0));

        //没有接得起炸弹的牌
        return bombsPlans;
    }
}
