package com.ebensz.games.ai.engine;

import com.ebensz.games.ai.bean.AiHand;
import com.ebensz.games.ai.bean.AiPokerList;
import com.ebensz.games.ai.bean.SplitType;
import com.ebensz.games.ai.split.EnumSplit;

import java.util.*;

/**
 * User: tosmart
 * Date: 2010-7-2
 * Time: 16:16:39
 */
public class PokerTypeUnit {

    public static List<AiPokerList> tryContact(AiPokerList pokers, AiPokerList toContact) {

        SplitType type = findSplitType(toContact);
        List<AiPokerList> aiPokerLists = enumSameType(pokers, type);

        return enumContactable(aiPokerLists, toContact, type);
    }

    public static SplitType findSplitType(AiPokerList toContact) {

        int length = toContact.size();

        switch (length) {

            case 1:
                return SplitType.PT_1;

            case 2:
                return SplitType.PT_1x2;

            case 3:
                return SplitType.PT_1x3;

            case 4:
                return toContact.charSet().size() == 1
                        ? SplitType.PT_1x4 : SplitType.PT_1x3_1;

            case 5:
                return toContact.charSet().size() == 5
                        ? SplitType.PT_5x1 : SplitType.PT_1x3_2x1;

            case 6: {

                Map<Character, Integer> counter = toContact.getCounter();
                Set<Character> idSet = counter.keySet();

                for (char id : idSet) {
                    if (counter.get(id) == 3) return SplitType.PT_2x3;
                    if (counter.get(id) == 4) return SplitType.PT_1x4_2;
                }

                return (toContact.charSet().size() == 6)
                        ? SplitType.PT_6x1 : SplitType.PT_3x2;
            }

            case 7:
                return SplitType.PT_7x1;

            case 8: {

                Map<Character, Integer> counter = toContact.getCounter();
                Set<Character> idSet = counter.keySet();

                for (char id : idSet) {
                    if (counter.get(id) == 3) return SplitType.PT_2x3_2x1;
                    if (counter.get(id) == 4) return SplitType.PT_1x4_2x2;
                }

                for (char id : idSet) {
                    if (counter.get(id) == 1) return SplitType.PT_8x1;
                    if (counter.get(id) == 2) return SplitType.PT_4x2;
                }

                break;
            }

            case 9:
                return toContact.charSet().size() == 9
                        ? SplitType.PT_9x1 : SplitType.PT_3x3;

            case 10: {

                int charTypes = toContact.charSet().size();

                return charTypes == 10 ? SplitType.PT_10x1
                        : charTypes == 5 ? SplitType.PT_5x2
                        : SplitType.PT_2x3_2x2;
            }

            case 11:
                return SplitType.PT_11x1;

            case 12: {

                Map<Character, Integer> counter = toContact.getCounter();
                Set<Character> idSet = counter.keySet();

                for (char id : idSet) {
                    if (counter.get(id) == 3) return SplitType.PT_3x3_3x1;
                }

                int charTypes = toContact.charSet().size();

                if (charTypes == 12) return SplitType.PT_12x1;
                if (charTypes == 6) return SplitType.PT_6x2;
                if (charTypes == 4) return SplitType.PT_4x3;

                break;
            }

            case 14:
                return SplitType.PT_7x2;

            case 15:
                return toContact.charSet().size() == 5
                        ? SplitType.PT_5x3 : SplitType.PT_3x3_3x2;

            case 16:
                return toContact.charSet().size() == 8
                        ? SplitType.PT_8x2 : SplitType.PT_4x3_4x1;

            case 18:
                return toContact.charSet().size() == 9
                        ? SplitType.PT_9x2 : SplitType.PT_6x3;

            case 20: {

                if (toContact.charSet().size() == 10) return SplitType.PT_10x2;

                Map<Character, Integer> counter = toContact.getCounter();
                Set<Character> idSet = counter.keySet();

                for (char id : idSet) {
                    if (counter.get(id) == 1) return SplitType.PT_5x3_5x1;
                    if (counter.get(id) == 2) return SplitType.PT_4x3_4x2;
                }

                break;
            }
        }

        System.out.println("toContact = " + toContact);
        throw new RuntimeException("Error Logic!");
    }

    private static List<AiPokerList> enumContactable(
            List<AiPokerList> aiPokerLists, AiPokerList toContact, SplitType type) {

        if (aiPokerLists == null) return new ArrayList<AiPokerList>();

        for (Iterator<AiPokerList> iterator = aiPokerLists.iterator(); iterator.hasNext(); ) {

            AiPokerList listAi = iterator.next();

            AiHand aiHand = new AiHand(listAi, type.handType);
            AiHand toContactAiHand = new AiHand(toContact, type.handType);

            if (aiHand.getSortChar() <= toContactAiHand.getSortChar())
                iterator.remove();
        }

        return aiPokerLists;
    }

    private static List<AiPokerList> enumSameType(AiPokerList pokers, SplitType type) {

        switch (type) {

            case PT_10x2:
                return EnumSplit.findLinkType_x2(pokers, 10);

            case PT_9x2:
                return EnumSplit.findLinkType_x2(pokers, 9);

            case PT_8x2:
                return EnumSplit.findLinkType_x2(pokers, 8);

            case PT_7x2:
                return EnumSplit.findLinkType_x2(pokers, 7);

            case PT_6x2:
                return EnumSplit.findLinkType_x2(pokers, 6);

            case PT_5x2:
                return EnumSplit.findLinkType_x2(pokers, 5);

            case PT_4x2:
                return EnumSplit.findLinkType_x2(pokers, 4);

            case PT_3x2:
                return EnumSplit.findLinkType_x2(pokers, 3);

            case PT_6x3:
                return EnumSplit.findLinkType_x3(pokers, 6);

            case PT_5x3:
                return EnumSplit.findLinkType_x3(pokers, 5);

            case PT_4x3:
                return EnumSplit.findLinkType_x3(pokers, 4);

            case PT_3x3:
                return EnumSplit.findLinkType_x3(pokers, 3);

            case PT_2x3:
                return EnumSplit.findLinkType_x3(pokers, 2);

            case PT_5x3_5x1:
                return EnumSplit.find_5x3_5x1(pokers);

            case PT_4x3_4x1:
                return EnumSplit.find_4x3_4x1(pokers);

            case PT_3x3_3x1:
                return EnumSplit.find_3x3_3x1(pokers);

            case PT_2x3_2x1:
                return EnumSplit.find_2x3_2x1(pokers);

            case PT_4x3_4x2:
                return EnumSplit.find_4x3_4x2(pokers);

            case PT_3x3_3x2:
                return EnumSplit.find_3x3_3x2(pokers);

            case PT_2x3_2x2:
                return EnumSplit.find_2x3_2x2(pokers);

            case PT_12x1:
                return EnumSplit.findLinkType(pokers, 12);

            case PT_11x1:
                return EnumSplit.findLinkType(pokers, 11);

            case PT_10x1:
                return EnumSplit.findLinkType(pokers, 10);

            case PT_9x1:
                return EnumSplit.findLinkType(pokers, 9);

            case PT_8x1:
                return EnumSplit.findLinkType(pokers, 8);

            case PT_7x1:
                return EnumSplit.findLinkType(pokers, 7);

            case PT_6x1:
                return EnumSplit.findLinkType(pokers, 6);

            case PT_5x1:
                return EnumSplit.findLinkType(pokers, 5);

            case PT_1x4:
                return EnumSplit.findBomb(pokers);

            case PT_1x4_2:
                return EnumSplit.find_1x4_2(pokers);

            case PT_1x4_2x2:
                return EnumSplit.find_1x4_2x2(pokers);

            case PT_1x3:
                return EnumSplit.find_1x3(pokers);

            case PT_1x3_1:
                return EnumSplit.find_1x3_1(pokers);

            case PT_1x3_2x1:
                return EnumSplit.find_1x3_2x1(pokers);

            case PT_YZ:
                return EnumSplit.find_YZ(pokers);

            case PT_1x2:
                return EnumSplit.find_1x2(pokers);

            case PT_1:
                return EnumSplit.find_1(pokers);
        }

        throw new RuntimeException("Logic Error!");
    }
}
