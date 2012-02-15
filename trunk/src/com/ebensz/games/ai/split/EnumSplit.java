package com.ebensz.games.ai.split;

import com.ebensz.games.ai.bean.AiPokerList;
import com.ebensz.games.ai.bean.SplitType;

import java.util.*;

/**
 * User: tosmart
 * Date: 2010-6-29
 * Time: 8:43:21
 */
public class EnumSplit {

    public static final char[] LINK_TYPE_POKER_IDS = {
            '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e'
    };

    public static final int RESULT_CUT = 0x01;
    public static final int RESULT_COMPLETE = 0x02;

    public static void main(String[] args) {

        // AiPokerList pokers = new AiPokerList("334556778999abcdexyz");
        AiPokerList pokers = new AiPokerList("3345679abcde".toCharArray());
        // AiPokerList pokers = new AiPokerList("33334567999abcdexx");

        List<String> solutions = new ArrayList<String>();
        LinkedList<AiPokerList> tokens = new LinkedList<AiPokerList>();

        split(pokers, solutions, tokens);
    }

    private static List<AiPokerList> enumBlocks(AiPokerList pokers) {

        Map<SplitType, List<AiPokerList>> map = enumerate(pokers);
        Collection<List<AiPokerList>> values = map.values();

        List<AiPokerList> blocks = new ArrayList<AiPokerList>();

        for (List<AiPokerList> value : values) {
            blocks.addAll(value);
        }

        return blocks;
    }

    public static int split(AiPokerList pokers, List<String> solutions, LinkedList<AiPokerList> hands) {

        int minBlocks = 20;

        if (solutions.size() > 0) {
            minBlocks = solutions.get(0).split("\\+").length;
        }

        int currentBlocks = hands.size();

        if (currentBlocks > minBlocks) return RESULT_CUT;
        if (currentBlocks == minBlocks && !pokers.isEmpty()) return RESULT_CUT;

        if (pokers.isEmpty()) {

            if (currentBlocks < minBlocks) solutions.clear();

            if (currentBlocks <= minBlocks) {

                StringBuffer solution = new StringBuffer();
                for (AiPokerList hand : hands) {
                    solution.append('+').append(hand);
                }
                solutions.add(solution.substring(1));
            }

            return RESULT_COMPLETE;
        }

        List<AiPokerList> blocks = enumBlocks(pokers);

        for (AiPokerList block : blocks) {

            pokers.remove(block);
            hands.add(block);

            int result = split(pokers, solutions, hands);

            pokers.add(block);
            hands.removeLast();

            if (result == RESULT_CUT) break;
        }

        return RESULT_COMPLETE;
    }

    public static Map<SplitType, List<AiPokerList>> enumerate(AiPokerList pokers) {

        Map<SplitType, List<AiPokerList>> map = new HashMap<SplitType, List<AiPokerList>>();

        List<AiPokerList> blocks = findLinkType_x2(pokers, 10);
        if (blocks != null) map.put(SplitType.PT_10x2, blocks);

        blocks = findLinkType_x2(pokers, 9);
        if (blocks != null) map.put(SplitType.PT_9x2, blocks);

        blocks = findLinkType_x2(pokers, 8);
        if (blocks != null) map.put(SplitType.PT_8x2, blocks);

        blocks = findLinkType_x2(pokers, 7);
        if (blocks != null) map.put(SplitType.PT_7x2, blocks);

        blocks = findLinkType_x2(pokers, 6);
        if (blocks != null) map.put(SplitType.PT_6x2, blocks);

        blocks = findLinkType_x2(pokers, 5);
        if (blocks != null) map.put(SplitType.PT_5x2, blocks);

        blocks = findLinkType_x2(pokers, 4);
        if (blocks != null) map.put(SplitType.PT_4x2, blocks);

        blocks = findLinkType_x2(pokers, 3);
        if (blocks != null) map.put(SplitType.PT_3x2, blocks);

        blocks = findLinkType_x3(pokers, 6);
        if (blocks != null) map.put(SplitType.PT_6x3, blocks);

        blocks = findLinkType_x3(pokers, 5);
        if (blocks != null) map.put(SplitType.PT_5x3, blocks);

        blocks = findLinkType_x3(pokers, 4);
        if (blocks != null) map.put(SplitType.PT_4x3, blocks);

        blocks = findLinkType_x3(pokers, 3);
        if (blocks != null) map.put(SplitType.PT_3x3, blocks);

        blocks = findLinkType_x3(pokers, 2);
        if (blocks != null) map.put(SplitType.PT_2x3, blocks);

        blocks = find_5x3_5x1(pokers);
        if (blocks != null) map.put(SplitType.PT_5x3_5x1, blocks);

        blocks = find_4x3_4x1(pokers);
        if (blocks != null) map.put(SplitType.PT_4x3_4x1, blocks);

        blocks = find_3x3_3x1(pokers);
        if (blocks != null) map.put(SplitType.PT_3x3_3x1, blocks);

        blocks = find_2x3_2x1(pokers);
        if (blocks != null) map.put(SplitType.PT_2x3_2x1, blocks);

        blocks = find_4x3_4x2(pokers);
        if (blocks != null) map.put(SplitType.PT_4x3_4x2, blocks);

        blocks = find_3x3_3x2(pokers);
        if (blocks != null) map.put(SplitType.PT_3x3_3x2, blocks);

        blocks = find_2x3_2x2(pokers);
        if (blocks != null) map.put(SplitType.PT_2x3_2x2, blocks);

        blocks = findLinkType(pokers, 12);
        if (blocks != null) map.put(SplitType.PT_12x1, blocks);

        blocks = findLinkType(pokers, 11);
        if (blocks != null) map.put(SplitType.PT_11x1, blocks);

        blocks = findLinkType(pokers, 10);
        if (blocks != null) map.put(SplitType.PT_10x1, blocks);

        blocks = findLinkType(pokers, 9);
        if (blocks != null) map.put(SplitType.PT_9x1, blocks);

        blocks = findLinkType(pokers, 8);
        if (blocks != null) map.put(SplitType.PT_8x1, blocks);

        blocks = findLinkType(pokers, 7);
        if (blocks != null) map.put(SplitType.PT_7x1, blocks);

        blocks = findLinkType(pokers, 6);
        if (blocks != null) map.put(SplitType.PT_6x1, blocks);

        blocks = findLinkType(pokers, 5);
        if (blocks != null) map.put(SplitType.PT_5x1, blocks);

        blocks = findBomb(pokers);
        if (blocks != null) map.put(SplitType.PT_1x4, blocks);

        blocks = find_1x4_2(pokers);
        if (blocks != null) map.put(SplitType.PT_1x4_2, blocks);

        blocks = find_1x4_2x2(pokers);
        if (blocks != null) map.put(SplitType.PT_1x4_2x2, blocks);

        blocks = find_1x3(pokers);
        if (blocks != null) map.put(SplitType.PT_1x3, blocks);

        blocks = find_1x3_1(pokers);
        if (blocks != null) map.put(SplitType.PT_1x3_1, blocks);

        blocks = find_1x3_2x1(pokers);
        if (blocks != null) map.put(SplitType.PT_1x3_2x1, blocks);

        blocks = find_YZ(pokers);
        if (blocks != null) map.put(SplitType.PT_YZ, blocks);

        blocks = find_1x2(pokers);
        if (blocks != null) map.put(SplitType.PT_1x2, blocks);

        blocks = find_1(pokers);
        if (blocks != null) map.put(SplitType.PT_1, blocks);

        return map;
    }

    public static List<AiPokerList> find_1(AiPokerList pokers) {

        if (pokers.size() < 1) return null;

        Set<Character> charSet = new HashSet<Character>();

        for (int i = 0, size = pokers.size(); i < size; i++) {
            charSet.add(pokers.get(i));
        }

        if (charSet.size() == 0) return null;

        List<AiPokerList> listAi = new ArrayList<AiPokerList>();

        for (Character c : charSet) {
            listAi.add(new AiPokerList(new char[]{c}));
        }

        return listAi;
    }

    public static List<AiPokerList> find_1x2(AiPokerList pokers) {

        if (pokers.size() < 2) return null;

        Set<Character> charSet = new HashSet<Character>();
        Set<Character> pairSet = new HashSet<Character>();

        for (int i = 0, size = pokers.size(); i < size; i++) {

            char c = pokers.get(i);

            if (charSet.contains(c)) {
                pairSet.add(c);
            }

            charSet.add(c);
        }

        if (pairSet.size() == 0) return null;

        List<AiPokerList> listAi = new ArrayList<AiPokerList>();

        for (Character c : pairSet) {
            listAi.add(new AiPokerList(new char[]{c, c}));
        }

        return listAi;
    }

    public static List<AiPokerList> find_YZ(AiPokerList pokers) {

        if (pokers.size() < 2) return null;

        boolean foundY = false, foundZ = false;

        for (int i = 0, size = pokers.size(); i < size; i++) {
            char c = pokers.get(i);
            if (c == 'y') foundY = true;
            if (c == 'z') foundZ = true;
            if (foundY && foundZ) break;
        }

        if (!foundY || !foundZ) return null;

        ArrayList<AiPokerList> listAi = new ArrayList<AiPokerList>();
        listAi.add(new AiPokerList(new char[]{'y', 'z'}));

        return listAi;
    }

    public static List<AiPokerList> find_1x3_2x1(AiPokerList pokers) {

        if (pokers.size() < 5) return null;

        Set<Character> oneTime = new HashSet<Character>();
        Set<Character> twoTimes = new HashSet<Character>();
        Set<Character> treeTimes = new HashSet<Character>();

        for (int i = 0, size = pokers.size(); i < size; i++) {

            char c = pokers.get(i);

            if (oneTime.contains(c)) {
                if (twoTimes.contains(c)) {
                    treeTimes.add(c);
                }
                twoTimes.add(c);
            }
            oneTime.add(c);
        }

        if (treeTimes.size() == 0) return null;

        ArrayList<AiPokerList> listAi = new ArrayList<AiPokerList>();

        for (Character t2 : twoTimes) {
            for (Character t3 : treeTimes) {
                if (t3 != t2) {
                    listAi.add(new AiPokerList(new char[]{t3, t3, t3, t2, t2}));
                }
            }
        }

        return listAi;
    }

    public static List<AiPokerList> find_1x3_1(AiPokerList pokers) {

        if (pokers.size() < 4) return null;

        Set<Character> oneTime = new HashSet<Character>();
        Set<Character> twoTimes = new HashSet<Character>();
        Set<Character> treeTimes = new HashSet<Character>();

        for (int i = 0, size = pokers.size(); i < size; i++) {

            char c = pokers.get(i);

            if (oneTime.contains(c)) {
                if (twoTimes.contains(c)) {
                    treeTimes.add(c);
                }
                twoTimes.add(c);
            }
            oneTime.add(c);
        }

        if (treeTimes.size() == 0) return null;

        ArrayList<AiPokerList> listAi = new ArrayList<AiPokerList>();

        for (Character t1 : oneTime) {
            for (Character t3 : treeTimes) {
                if (t3 != t1) {
                    listAi.add(new AiPokerList(new char[]{t3, t3, t3, t1}));
                }
            }
        }

        return listAi;
    }

    public static List<AiPokerList> find_1x3(AiPokerList pokers) {

        if (pokers.size() < 3) return null;

        Set<Character> oneTime = new HashSet<Character>();
        Set<Character> twoTimes = new HashSet<Character>();
        Set<Character> treeTimes = new HashSet<Character>();

        for (int i = 0, size = pokers.size(); i < size; i++) {

            char c = pokers.get(i);

            if (oneTime.contains(c)) {
                if (twoTimes.contains(c)) {
                    treeTimes.add(c);
                }
                twoTimes.add(c);
            }
            oneTime.add(c);
        }

        if (treeTimes.size() == 0) return null;

        ArrayList<AiPokerList> listAi = new ArrayList<AiPokerList>();

        for (Character t3 : treeTimes) {
            listAi.add(new AiPokerList(new char[]{t3, t3, t3}));
        }

        return listAi;
    }

    public static List<AiPokerList> find_1x4_2x2(AiPokerList pokers) {

        if (pokers.size() < 8) return null;

        Set<Character> oneTime = new HashSet<Character>();
        Set<Character> twoTimes = new HashSet<Character>();
        Set<Character> treeTimes = new HashSet<Character>();
        Set<Character> fourTimes = new HashSet<Character>();

        for (int i = 0, size = pokers.size(); i < size; i++) {

            char c = pokers.get(i);

            if (oneTime.contains(c)) {
                if (twoTimes.contains(c)) {
                    if (treeTimes.contains(c)) {
                        fourTimes.add(c);
                    }
                    treeTimes.add(c);
                }
                twoTimes.add(c);
            }
            oneTime.add(c);
        }

        if (fourTimes.size() == 0 || twoTimes.size() < 2) return null;

        ArrayList<AiPokerList> listAi = new ArrayList<AiPokerList>();

        Character[] twoTimesArray = new Character[twoTimes.size()];
        twoTimesArray = twoTimes.toArray(twoTimesArray);

        for (int i = 0, size = twoTimesArray.length; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                Character t2i = twoTimesArray[i], t2j = twoTimesArray[j];
                for (Character t4 : fourTimes) {
                    if (t4 != t2i && t4 != t2j) {
                        char[] pokerIds = {t4, t4, t4, t4, t2i, t2i, t2j, t2j};
                        listAi.add(new AiPokerList(pokerIds));
                    }
                }
            }
        }

        return listAi.size() == 0 ? null : listAi;
    }

    public static List<AiPokerList> find_1x4_2(AiPokerList pokers) {

        if (pokers.size() < 6) return null;

        Set<Character> oneTime = new HashSet<Character>();
        Set<Character> twoTimes = new HashSet<Character>();
        Set<Character> treeTimes = new HashSet<Character>();
        Set<Character> fourTimes = new HashSet<Character>();

        for (int i = 0, size = pokers.size(); i < size; i++) {

            char c = pokers.get(i);

            if (oneTime.contains(c)) {
                if (twoTimes.contains(c)) {
                    if (treeTimes.contains(c)) {
                        fourTimes.add(c);
                    }
                    treeTimes.add(c);
                }
                twoTimes.add(c);
            }
            oneTime.add(c);
        }

        if (fourTimes.size() == 0 || oneTime.size() < 2) return null;

        ArrayList<AiPokerList> listAi = new ArrayList<AiPokerList>();

        Character[] oneTimeArray = new Character[oneTime.size()];
        oneTimeArray = oneTime.toArray(oneTimeArray);

        for (int i = 0, size = oneTimeArray.length; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                Character t1i = oneTimeArray[i], t1j = oneTimeArray[j];
                for (Character t4 : fourTimes) {
                    if (t4 != t1i && t4 != t1j) {
                        char[] pokerIds = {t4, t4, t4, t4, t1i, t1j};
                        listAi.add(new AiPokerList(pokerIds));
                    }
                }
            }
        }

        return listAi.size() == 0 ? null : listAi;
    }

    public static List<AiPokerList> find_2x3_2x2(AiPokerList pokers) {

        if (pokers.size() < 10) return null;

        Set<Character> oneTime = new HashSet<Character>();
        Set<Character> twoTimes = new HashSet<Character>();
        Set<Character> treeTimes = new HashSet<Character>();

        for (int i = 0, size = pokers.size(); i < size; i++) {

            char c = pokers.get(i);

            if (oneTime.contains(c)) {
                if (twoTimes.contains(c)) {
                    treeTimes.add(c);
                }
                twoTimes.add(c);
            }
            oneTime.add(c);
        }

        if (treeTimes.size() < 2) return null;

        ArrayList<AiPokerList> listAi = new ArrayList<AiPokerList>();

        Character[] twoTimesArray = new Character[twoTimes.size()];
        twoTimesArray = twoTimes.toArray(twoTimesArray);

        for (int i = 0, t2Length = twoTimesArray.length; i < t2Length - 1; i++) {

            if (treeTimes.contains(twoTimesArray[i]))
                continue;                //add Roy  枚举所带牌型 需排除飞机中的牌

            for (int j = i + 1; j < t2Length; j++) {

                Character t2i = twoTimesArray[i], t2j = twoTimesArray[j];

                if (treeTimes.contains(t2j))
                    continue;           //add Roy

                for (int s = 0; s < LINK_TYPE_POKER_IDS.length - 1; s++) {

                    boolean found = true;
                    char[] linkTypeChars = new char[2];

                    for (int n = 0; n < 2; n++) {
                        linkTypeChars[n] = LINK_TYPE_POKER_IDS[s + n];
                        if (!treeTimes.contains(linkTypeChars[n])) {
                            found = false;
                            break;
                        }
                    }

                    if (found) {

                        char[] pokerIds = {
                                linkTypeChars[0], linkTypeChars[0], linkTypeChars[0],
                                linkTypeChars[1], linkTypeChars[1], linkTypeChars[1],
                                t2i, t2i, t2j, t2j
                        };

                        listAi.add(new AiPokerList(pokerIds));
                    }
                }
            }
        }

        return listAi.size() == 0 ? null : listAi;
    }

    public static List<AiPokerList> find_3x3_3x2(AiPokerList pokers) {

        if (pokers.size() < 15) return null;

        Set<Character> oneTime = new HashSet<Character>();
        Set<Character> twoTimes = new HashSet<Character>();
        Set<Character> treeTimes = new HashSet<Character>();

        for (int i = 0, size = pokers.size(); i < size; i++) {

            char c = pokers.get(i);

            if (oneTime.contains(c)) {
                if (twoTimes.contains(c)) {
                    treeTimes.add(c);
                }
                twoTimes.add(c);
            }
            oneTime.add(c);
        }

        if (treeTimes.size() < 3) return null;

        ArrayList<AiPokerList> listAi = new ArrayList<AiPokerList>();

        Character[] twoTimesArray = new Character[twoTimes.size()];
        twoTimesArray = twoTimes.toArray(twoTimesArray);

        for (int i = 0, t2Length = twoTimesArray.length; i < t2Length - 2; i++) {

            for (int j = i + 1; j < t2Length - 1; j++) {

                for (int k = j + 1; k < t2Length; k++) {

                    Character t2i = twoTimesArray[i], t2j = twoTimesArray[j], t2k = twoTimesArray[k];

                    for (int s = 0; s < LINK_TYPE_POKER_IDS.length - 2; s++) {

                        boolean found = true;
                        char[] linkTypeChars = new char[3];

                        for (int n = 0; n < 3; n++) {

                            linkTypeChars[n] = LINK_TYPE_POKER_IDS[s + n];

                            if (linkTypeChars[n] == t2i ||
                                    linkTypeChars[n] == t2j ||
                                    linkTypeChars[n] == t2k) {

                                found = false;
                                break;
                            }

                            if (!treeTimes.contains(linkTypeChars[n])) {
                                found = false;
                                break;
                            }
                        }

                        if (found) {

                            char[] pokerIds = {
                                    linkTypeChars[0], linkTypeChars[0], linkTypeChars[0],
                                    linkTypeChars[1], linkTypeChars[1], linkTypeChars[1],
                                    linkTypeChars[2], linkTypeChars[2], linkTypeChars[2],
                                    t2i, t2i, t2j, t2j, t2k, t2k
                            };

                            listAi.add(new AiPokerList(pokerIds));
                        }
                    }
                }
            }
        }

        return listAi.size() == 0 ? null : listAi;
    }

    public static List<AiPokerList> find_4x3_4x2(AiPokerList pokers) {

        if (pokers.size() < 20) return null;

        Set<Character> oneTime = new HashSet<Character>();
        Set<Character> twoTimes = new HashSet<Character>();
        Set<Character> treeTimes = new HashSet<Character>();

        for (int i = 0, size = pokers.size(); i < size; i++) {

            char c = pokers.get(i);

            if (oneTime.contains(c)) {
                if (twoTimes.contains(c)) {
                    treeTimes.add(c);
                }
                twoTimes.add(c);
            }
            oneTime.add(c);
        }

        if (treeTimes.size() < 4) return null;

        ArrayList<AiPokerList> listAi = new ArrayList<AiPokerList>();

        Character[] twoTimesArray = new Character[twoTimes.size()];
        twoTimesArray = twoTimes.toArray(twoTimesArray);

        for (int i = 0, t2Length = twoTimesArray.length; i < t2Length - 3; i++) {

            for (int j = i + 1; j < t2Length - 2; j++) {

                for (int k = j + 1; k < t2Length - 1; k++) {

                    for (int l = k + 1; l < t2Length; l++) {

                        Character t2i = twoTimesArray[i], t2j = twoTimesArray[j];
                        Character t2k = twoTimesArray[k], t2l = twoTimesArray[l];

                        for (int s = 0; s < LINK_TYPE_POKER_IDS.length - 3; s++) {

                            boolean found = true;
                            char[] linkTypeChars = new char[4];

                            for (int n = 0; n < 4; n++) {

                                linkTypeChars[n] = LINK_TYPE_POKER_IDS[s + n];

                                if (linkTypeChars[n] == t2i ||
                                        linkTypeChars[n] == t2j ||
                                        linkTypeChars[n] == t2k ||
                                        linkTypeChars[n] == t2l) {

                                    found = false;
                                    break;
                                }

                                if (!treeTimes.contains(linkTypeChars[n])) {
                                    found = false;
                                    break;
                                }
                            }

                            if (found) {

                                char[] pokerIds = {
                                        linkTypeChars[0], linkTypeChars[0], linkTypeChars[0],
                                        linkTypeChars[1], linkTypeChars[1], linkTypeChars[1],
                                        linkTypeChars[2], linkTypeChars[2], linkTypeChars[2],
                                        linkTypeChars[3], linkTypeChars[3], linkTypeChars[3],
                                        t2i, t2i, t2j, t2j, t2k, t2k, t2l, t2l
                                };

                                listAi.add(new AiPokerList(pokerIds));
                            }
                        }
                    }
                }
            }
        }

        return listAi.size() == 0 ? null : listAi;
    }

    public static List<AiPokerList> find_2x3_2x1(AiPokerList pokers) {

        if (pokers.size() < 8) return null;

        Set<Character> oneTime = new HashSet<Character>();
        Set<Character> twoTimes = new HashSet<Character>();
        Set<Character> treeTimes = new HashSet<Character>();

        for (int i = 0, size = pokers.size(); i < size; i++) {

            char c = pokers.get(i);

            if (oneTime.contains(c)) {
                if (twoTimes.contains(c)) {
                    treeTimes.add(c);
                }
                twoTimes.add(c);
            }
            oneTime.add(c);
        }

        if (treeTimes.size() < 2) return null;

        ArrayList<AiPokerList> listAi = new ArrayList<AiPokerList>();

        Character[] oneTimeArray = new Character[oneTime.size()];
        oneTimeArray = oneTime.toArray(oneTimeArray);

        for (int i = 0, t1Length = oneTimeArray.length; i < t1Length - 1; i++) {

            for (int j = i + 1; j < t1Length; j++) {

                Character t2i = oneTimeArray[i], t2j = oneTimeArray[j];

                for (int s = 0; s < LINK_TYPE_POKER_IDS.length - 1; s++) {

                    boolean found = true;
                    char[] linkTypeChars = new char[2];

                    for (int n = 0; n < 2; n++) {
                        linkTypeChars[n] = LINK_TYPE_POKER_IDS[s + n];
                        if (!treeTimes.contains(linkTypeChars[n])) {
                            found = false;
                            break;
                        }
                    }

                    if (found) {

                        char[] pokerIds = {
                                linkTypeChars[0], linkTypeChars[0], linkTypeChars[0],
                                linkTypeChars[1], linkTypeChars[1], linkTypeChars[1],
                                t2i, t2j
                        };

                        listAi.add(new AiPokerList(pokerIds));
                    }
                }
            }
        }

        return listAi.size() == 0 ? null : listAi;
    }

    public static List<AiPokerList> find_3x3_3x1(AiPokerList pokers) {

        if (pokers.size() < 12) return null;

        Set<Character> oneTime = new HashSet<Character>();
        Set<Character> twoTimes = new HashSet<Character>();
        Set<Character> treeTimes = new HashSet<Character>();

        for (int i = 0, size = pokers.size(); i < size; i++) {

            char c = pokers.get(i);

            if (oneTime.contains(c)) {
                if (twoTimes.contains(c)) {
                    treeTimes.add(c);
                }
                twoTimes.add(c);
            }
            oneTime.add(c);
        }

        if (treeTimes.size() < 3) return null;

        ArrayList<AiPokerList> listAi = new ArrayList<AiPokerList>();

        Character[] oneTimeArray = new Character[oneTime.size()];
        oneTimeArray = oneTime.toArray(oneTimeArray);

        for (int i = 0, t1Length = oneTimeArray.length; i < t1Length - 2; i++) {

            for (int j = i + 1; j < t1Length - 1; j++) {

                for (int k = j + 1; k < t1Length; k++) {

                    Character t2i = oneTimeArray[i], t2j = oneTimeArray[j], t2k = oneTimeArray[k];

                    for (int s = 0; s < LINK_TYPE_POKER_IDS.length - 2; s++) {

                        boolean found = true;
                        char[] linkTypeChars = new char[3];

                        for (int n = 0; n < 3; n++) {

                            linkTypeChars[n] = LINK_TYPE_POKER_IDS[s + n];

                            if (linkTypeChars[n] == t2i ||
                                    linkTypeChars[n] == t2j ||
                                    linkTypeChars[n] == t2k) {

                                found = false;
                                break;
                            }

                            if (!treeTimes.contains(linkTypeChars[n])) {
                                found = false;
                                break;
                            }
                        }

                        if (found) {

                            char[] pokerIds = {
                                    linkTypeChars[0], linkTypeChars[0], linkTypeChars[0],
                                    linkTypeChars[1], linkTypeChars[1], linkTypeChars[1],
                                    linkTypeChars[2], linkTypeChars[2], linkTypeChars[2],
                                    t2i, t2j, t2k
                            };

                            listAi.add(new AiPokerList(pokerIds));
                        }
                    }
                }
            }
        }

        return listAi.size() == 0 ? null : listAi;
    }

    public static List<AiPokerList> find_4x3_4x1(AiPokerList pokers) {

        if (pokers.size() < 16) return null;

        Set<Character> oneTime = new HashSet<Character>();
        Set<Character> twoTimes = new HashSet<Character>();
        Set<Character> treeTimes = new HashSet<Character>();

        for (int i = 0, size = pokers.size(); i < size; i++) {

            char c = pokers.get(i);

            if (oneTime.contains(c)) {
                if (twoTimes.contains(c)) {
                    treeTimes.add(c);
                }
                twoTimes.add(c);
            }
            oneTime.add(c);
        }

        if (treeTimes.size() < 4) return null;

        ArrayList<AiPokerList> listAi = new ArrayList<AiPokerList>();

        Character[] oneTimesArray = new Character[oneTime.size()];
        oneTimesArray = oneTime.toArray(oneTimesArray);

        for (int i = 0, t1Length = oneTimesArray.length; i < t1Length - 3; i++) {

            for (int j = i + 1; j < t1Length - 2; j++) {

                for (int k = j + 1; k < t1Length - 1; k++) {

                    for (int l = k + 1; l < t1Length; l++) {

                        Character t2i = oneTimesArray[i], t2j = oneTimesArray[j];
                        Character t2k = oneTimesArray[k], t2l = oneTimesArray[l];

                        for (int s = 0; s < LINK_TYPE_POKER_IDS.length - 3; s++) {

                            boolean found = true;
                            char[] linkTypeChars = new char[4];

                            for (int n = 0; n < 4; n++) {

                                linkTypeChars[n] = LINK_TYPE_POKER_IDS[s + n];

                                if (linkTypeChars[n] == t2i ||
                                        linkTypeChars[n] == t2j ||
                                        linkTypeChars[n] == t2k ||
                                        linkTypeChars[n] == t2l) {

                                    found = false;
                                    break;
                                }

                                if (!treeTimes.contains(linkTypeChars[n])) {
                                    found = false;
                                    break;
                                }
                            }

                            if (found) {

                                char[] pokerIds = {
                                        linkTypeChars[0], linkTypeChars[0], linkTypeChars[0],
                                        linkTypeChars[1], linkTypeChars[1], linkTypeChars[1],
                                        linkTypeChars[2], linkTypeChars[2], linkTypeChars[2],
                                        linkTypeChars[3], linkTypeChars[3], linkTypeChars[3],
                                        t2i, t2j, t2k, t2l
                                };

                                listAi.add(new AiPokerList(pokerIds));
                            }
                        }
                    }
                }
            }
        }

        return listAi.size() == 0 ? null : listAi;
    }

    public static List<AiPokerList> find_5x3_5x1(AiPokerList pokers) {

        if (pokers.size() < 20) return null;

        Set<Character> oneTime = new HashSet<Character>();
        Set<Character> twoTimes = new HashSet<Character>();
        Set<Character> treeTimes = new HashSet<Character>();

        for (int i = 0, size = pokers.size(); i < size; i++) {

            char c = pokers.get(i);

            if (oneTime.contains(c)) {
                if (twoTimes.contains(c)) {
                    treeTimes.add(c);
                }
                twoTimes.add(c);
            }
            oneTime.add(c);
        }

        if (treeTimes.size() < 5) return null;

        ArrayList<AiPokerList> listAi = new ArrayList<AiPokerList>();

        Character[] oneTimesArray = new Character[oneTime.size()];
        oneTimesArray = oneTime.toArray(oneTimesArray);

        for (int i = 0, t1Length = oneTimesArray.length; i < t1Length - 4; i++) {

            for (int j = i + 1; j < t1Length - 3; j++) {

                for (int k = j + 1; k < t1Length - 2; k++) {

                    for (int l = k + 1; l < t1Length - 1; l++) {

                        for (int m = l + 1; m < t1Length; m++) {

                            Character t2i = oneTimesArray[i], t2j = oneTimesArray[j];
                            Character t2k = oneTimesArray[k], t2l = oneTimesArray[l];
                            Character t2m = oneTimesArray[m];

                            for (int s = 0; s < LINK_TYPE_POKER_IDS.length - 4; s++) {

                                boolean found = true;
                                char[] linkTypeChars = new char[5];

                                for (int n = 0; n < 5; n++) {

                                    linkTypeChars[n] = LINK_TYPE_POKER_IDS[s + n];

                                    if (linkTypeChars[n] == t2i ||
                                            linkTypeChars[n] == t2j ||
                                            linkTypeChars[n] == t2k ||
                                            linkTypeChars[n] == t2l ||
                                            linkTypeChars[n] == t2m) {

                                        found = false;
                                        break;
                                    }

                                    if (!treeTimes.contains(linkTypeChars[n])) {
                                        found = false;
                                        break;
                                    }
                                }

                                if (found) {

                                    char[] pokerIds = {
                                            linkTypeChars[0], linkTypeChars[0], linkTypeChars[0],
                                            linkTypeChars[1], linkTypeChars[1], linkTypeChars[1],
                                            linkTypeChars[2], linkTypeChars[2], linkTypeChars[2],
                                            linkTypeChars[3], linkTypeChars[3], linkTypeChars[3],
                                            linkTypeChars[4], linkTypeChars[4], linkTypeChars[4],
                                            t2i, t2j, t2k, t2l, t2m
                                    };

                                    listAi.add(new AiPokerList(pokerIds));
                                }
                            }
                        }
                    }
                }
            }
        }

        return listAi.size() == 0 ? null : listAi;
    }

    public static List<AiPokerList> findLinkType(AiPokerList pokers, int length) {

        if (pokers.size() < length) return null;

        Set<Character> charSet = new HashSet<Character>();

        for (int i = 0, size = pokers.size(); i < size; i++) {
            charSet.add(pokers.get(i));
        }

        if (charSet.size() < length) return null;

        List<AiPokerList> listAi = new ArrayList<AiPokerList>();

        for (int s = 0; s < LINK_TYPE_POKER_IDS.length - length + 1; s++) {

            boolean found = true;
            char[] pokerIds = new char[length];

            for (int n = 0; n < length; n++) {
                pokerIds[n] = LINK_TYPE_POKER_IDS[s + n];
                if (!charSet.contains(pokerIds[n])) {
                    s += n;
                    found = false;
                    break;
                }
            }

            if (found) listAi.add(new AiPokerList(pokerIds));
        }

        return listAi.size() == 0 ? null : listAi;
    }

    public static List<AiPokerList> findBomb(AiPokerList pokers) {

        if (pokers.size() < 4) return null;

        Map<Character, Integer> counter = pokers.getCounter();
        Set<Character> idSet = counter.keySet();

        List<AiPokerList> listAi = new ArrayList<AiPokerList>();

        for (char id : idSet) {
            if (counter.get(id) == 4) {
                char[] ids = {id, id, id, id};
                listAi.add(new AiPokerList(ids));
            }
        }

        return (listAi.size() == 0) ? null : listAi;
    }

    public static List<AiPokerList> findLinkType_x2(AiPokerList pokers, int length) {

        if (pokers.size() < length * 2) return null;

        Set<Character> oneTime = new HashSet<Character>();
        Set<Character> twoTimes = new HashSet<Character>();

        for (int i = 0, size = pokers.size(); i < size; i++) {

            char c = pokers.get(i);

            if (oneTime.contains(c)) {
                twoTimes.add(c);
            }
            oneTime.add(c);
        }

        if (twoTimes.size() < length) return null;

        ArrayList<AiPokerList> listAi = new ArrayList<AiPokerList>();

        for (int s = 0; s < LINK_TYPE_POKER_IDS.length - length + 1; s++) {

            boolean found = true;
            char[] linkTypeIds = new char[length];

            for (int n = 0; n < length; n++) {
                linkTypeIds[n] = LINK_TYPE_POKER_IDS[s + n];
                if (!twoTimes.contains(linkTypeIds[n])) {
                    s += n;
                    found = false;
                    break;
                }
            }

            if (found) {

                char[] pokerIds = new char[length * 2];

                for (int i = 0; i < length; i++) {
                    pokerIds[i * 2] = pokerIds[i * 2 + 1] = linkTypeIds[i];
                }

                listAi.add(new AiPokerList(pokerIds));
            }
        }

        return listAi.size() == 0 ? null : listAi;
    }

    public static List<AiPokerList> findLinkType_x3(AiPokerList pokers, int length) {

        if (pokers.size() < length * 3) return null;

        Set<Character> oneTime = new HashSet<Character>();
        Set<Character> twoTimes = new HashSet<Character>();
        Set<Character> treeTimes = new HashSet<Character>();

        for (int i = 0, size = pokers.size(); i < size; i++) {

            char c = pokers.get(i);

            if (oneTime.contains(c)) {
                if (twoTimes.contains(c)) {
                    treeTimes.add(c);
                }
                twoTimes.add(c);
            }
            oneTime.add(c);
        }

        if (treeTimes.size() < length) return null;

        ArrayList<AiPokerList> listAi = new ArrayList<AiPokerList>();

        for (int s = 0; s < LINK_TYPE_POKER_IDS.length - length + 1; s++) {

            boolean found = true;
            char[] linkTypeIds = new char[length];

            for (int n = 0; n < length; n++) {
                linkTypeIds[n] = LINK_TYPE_POKER_IDS[s + n];
                if (!treeTimes.contains(linkTypeIds[n])) {
                    s += n;
                    found = false;
                    break;
                }
            }

            if (found) {

                char[] pokerIds = new char[length * 3];

                for (int i = 0; i < length; i++) {
                    pokerIds[i * 3] = pokerIds[i * 3 + 1] = pokerIds[i * 3 + 2] = linkTypeIds[i];
                }

                listAi.add(new AiPokerList(pokerIds));
            }
        }

        return listAi.size() == 0 ? null : listAi;
    }
}
