package com.ebensz.games.ai.bean;

import java.util.*;

/**
 * User: tosmart
 * Date: 2010-6-30
 * Time: 9:13:34
 */
public class Solution {

    public Solution() {
        aiHands = new ArrayList<AiHand>();
    }

    public List<AiPokerList> peek(HandType handType) {

        List<AiPokerList> listAi = new ArrayList<AiPokerList>();

        for (AiHand aiHand : aiHands) {
            if (aiHand.getType() == handType) {
                listAi.add(aiHand);
            }
        }

        return listAi;
    }

    public List<AiPokerList> pop(HandType handType) {

        List<AiPokerList> listAi = new ArrayList<AiPokerList>();

        for (Iterator<AiHand> iterator = aiHands.iterator(); iterator.hasNext(); ) {

            AiHand aiHand = iterator.next();

            if (aiHand.getType() == handType) {
                listAi.add(aiHand);
                iterator.remove();
            }
        }

        return listAi;
    }

    public AiHand peek(int index) {
        if (!sorted) throw new RuntimeException("Solution Not Sored!");
        return aiHands.get(index);
    }

    public AiHand pop(int index) {
        if (!sorted) throw new RuntimeException("Solution Not Sored!");
        return aiHands.remove(index);
    }

    public void sort() {
        Collections.sort(aiHands, new SolutionComparator());
        sorted = true;
    }

    public int size() {
        return aiHands.size();
    }

    public void addHand(AiHand aiHand) {
        aiHands.add(aiHand);
        sorted = false;
    }

    public AiHand peekMinSingle() {

        if (!sorted) throw new RuntimeException("Solution Not Sored!");

        AiHand aiHand = aiHands.get(size() - 1);

        if (aiHand.getType() == HandType.Single)
            return aiHand;

        return null;
    }

    public AiHand popMinSingle() {

        if (!sorted) throw new RuntimeException("Solution Not Sored!");

        AiHand aiHand = aiHands.get(size() - 1);

        if (aiHand.getType() == HandType.Single)
            return aiHands.remove(size() - 1);

        return null;
    }

    public AiHand peekMinPair() {

        if (!sorted) throw new RuntimeException("Solution Not Sored!");

        List<AiPokerList> pairs = peek(HandType.Pair);

        int pairCount = pairs.size();
        if (pairCount == 0) return null;

        return (AiHand) pairs.get(pairCount - 1);
    }

    public AiHand popMinPair() {

        if (!sorted) throw new RuntimeException("Solution Not Sored!");

        List<AiPokerList> pairs = peek(HandType.Pair);

        int pairCount = pairs.size();
        if (pairCount == 0) return null;

        return (AiHand) pairs.remove(pairCount - 1);
    }

    public String toString() {

        if (size() == 0) return "";

        StringBuilder buffer = new StringBuilder();

        for (AiHand aiHand : aiHands) {
            buffer.append('+').append(aiHand);
        }

        return buffer.substring(1);
    }

    public boolean isSorted() {
        return sorted;
    }

    public static class SolutionComparator implements Comparator<AiHand> {

        public int compare(AiHand o1, AiHand o2) {

            int sortIndex1 = o1.getType().getSortIndex();
            int sortIndex2 = o2.getType().getSortIndex();

            int diff = sortIndex1 - sortIndex2;
            int value = diff == 0 ? o2.getSortChar() - o1.getSortChar() : diff;

            if (value != 0) {
                return value;
            }
            else {
                List<Character> cListO1 = o1.idArray, cListO2 = o2.idArray;
                int o1Value = 0, o2Value = 0;

                for (Character c : cListO1) {
                    o1Value += c;
                }

                for (Character c : cListO2) {
                    o2Value += c;
                }

                return o2Value - o1Value;
            }
        }
    }

    private List<AiHand> aiHands;
    private boolean sorted;
}
