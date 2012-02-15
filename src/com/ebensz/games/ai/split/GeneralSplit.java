package com.ebensz.games.ai.split;

import com.ebensz.games.ai.bean.*;

import java.util.*;


/**
 * User: tosmart
 * Date: 2010-6-30
 * Time: 11:40:32
 */
public class GeneralSplit {

    public static Solution parse(AiPokerList orgPokers) {

        AiPokerList pokers = orgPokers.getClone();
        Solution solution = new Solution();

        Set<Character> x3Set = new HashSet<Character>();
        Set<Character> x2Set = new HashSet<Character>();

        // 找出火箭（双王），直接加到输出方案中

        AiPokerList rocket = findRocket(pokers);
        if (rocket != null) {
            solution.addHand(new AiHand(rocket, HandType.Rocket));
            pokers.remove(rocket);
        }

        // 找出炸弹（1x4），直接加到输出方案中

        List<AiPokerList> bombs = findBomb(pokers);
        for (AiPokerList bomb : bombs) {
            solution.addHand(new AiHand(bomb, HandType.Bomb));
            pokers.remove(bomb);
        }

        // 找出三个（1x3），暂放在x3List列表中

        List<AiPokerList> x3ListAi = find_1x3(pokers);

        for (AiPokerList x3 : x3ListAi) {
            x3Set.add(x3.getMinChar());
            pokers.remove(x3);
        }

        // 从三个列表（x3ListAi）中找三连对，暂放入threeLinks列表中

        List<AiPokerList> threeLinks = retrieveThreeLinks(x3Set);

        // 找出第一个最短（长度为5）的顺子，取出后再继续找出下一个最短顺子，将它们暂放入singleLinks列表

        List<AiPokerList> singleLinks = retrieveSingleLinks(pokers);

        // 在对最短顺子进行扩展之前，先看看剩下的牌中有没有连对，有就找出来，放到pairLinks列表中
        // 这个操作是为了防止对单顺进行扩展时，将本来可以生成连对的牌拆开，例如：345678899aa
        // 如果不先找出8899aa，在对34567进行扩展时，就会把8899aa拆开

        List<AiPokerList> x2ListAi = find_1x2(pokers);

        for (AiPokerList x2 : x2ListAi) {
            x2Set.add(x2.getMinChar());
            pokers.remove(x2);
        }

        List<AiPokerList> pairLinks = retrievePairLinks(x2Set);

        for (AiPokerList link : pairLinks) {
            pokers.remove(link);
        }

        // 找连对之后，将没有组成连对的对子先还入牌池，准备扩展单顺

        for (char x2 : x2Set) {
            pokers.add(new char[]{x2, x2});
        }
        x2ListAi.clear();
        x2Set.clear();

        // 扩展单顺

        expandLinks(pokers, singleLinks);

        // 如果两个单顺就可无缝连接，就把它们连成一个

        contactSingleLinks(singleLinks);

        // 将三个列表中的牌一手一手的放循环入牌池，看能不能再找出新的最短单顺，
        // 如果找到，就暂时将它加入singleLinks列表中

        singleLinks.addAll(
                retrieveSingleLinksFillX3(pokers, x3Set)
        );

        // 在加入三个并抽取单顺后，看看还能不能找到新的连对，
        // 如果找到，就暂时加入pairLinks列表中

        x2ListAi = find_1x2(pokers);

        for (AiPokerList x2 : x2ListAi) {
            x2Set.add(x2.getMinChar());
            pokers.remove(x2);
        }

        List<AiPokerList> newPairLinks = retrievePairLinks(x2Set);
        for (AiPokerList link : newPairLinks) {
            pokers.remove(link);
        }
        pairLinks.addAll(newPairLinks);

        // 将没有组成连对的对子先还入牌池，准备扩展单顺。因为前边添加的三个牌型，
        // 在查找最小单顺和查找连对之后，还有剩余的牌在牌池中，有可能可以对单顺进行扩展

        for (char x2 : x2Set) {
            pokers.add(new char[]{x2, x2});
        }
        x2ListAi.clear();
        x2Set.clear();

        expandLinks(pokers, singleLinks);

        // 将剩余的三个再次加入牌池，看能不能对单顺进行“优势”扩展

        expandSingleLinksFillX3(pokers, x3Set, singleLinks);

        // 然后在剩余的牌池中，再对单顺进行扩展

        expandLinks(pokers, singleLinks);

        // 单顺已经有变化，再次检查是否两个单顺就可无缝连接，如果可以就把它们连成一个

        contactSingleLinks(singleLinks);

        // 如果有两个完全相同一单顺存在，就把它们合并成一个双顺，将暂时加入pairLinks列表

        pairLinks.addAll(composePairLinks(singleLinks));

        // 前面再次添入三个后，对单顺进行了扩展，可能在牌池中剩下的有对子，
        // 现在将连对还入牌池中，重新找对子，看能不能合成更长的连对

        for (Iterator<AiPokerList> i = pairLinks.iterator(); i.hasNext(); ) {
            AiPokerList pair = i.next();
            pokers.add(pair);
            i.remove();
        }

        // indexOf more pairs

        x2ListAi = find_1x2(pokers);

        for (AiPokerList x2 : x2ListAi) {
            x2Set.add(x2.getMinChar());
            pokers.remove(x2);
        }

        // retrieve pair-links

        pairLinks = retrievePairLinks(x2Set);

        for (AiPokerList link : pairLinks) {
            pokers.remove(link);
        }

        // 将单顺一手一手地循环加入牌池，看能不能对连对进行“优势”扩展

        pairLinks.addAll(
                retrievePairLinksFillSingleLink(
                        pokers,
                        singleLinks,
                        x2Set
                )
        );

        // 输入到拆分方案中，作为首阶段的输出

        outputToSolution(
                pokers,
                solution,
                x3Set,
                x2Set,
                threeLinks,
                singleLinks,
                pairLinks
        );

        return solution;
    }

    private static void outputToSolution(
            AiPokerList pokers, Solution result, Set<Character> x3Set,
            Set<Character> x2Set, List<AiPokerList> threeLinks,
            List<AiPokerList> singleLinks, List<AiPokerList> pairLinks) {

        // output single-links

        Collections.sort(singleLinks, POKER_LIST_COMPARATOR);

        for (AiPokerList link : singleLinks) {
            result.addHand(new AiHand(link, HandType.SingleLink));
        }

        // output pair-links

        for (AiPokerList link : pairLinks) {
            result.addHand(new AiHand(link, HandType.PairLink));
        }

        // output tree-Links (add drag block)

        Collections.sort(threeLinks, POKER_LIST_COMPARATOR);

        for (AiPokerList link : threeLinks) {

            int dragCount = link.size() / 3;

            AiPokerList drag = retrieveDrags(dragCount, pokers, x2Set);
            int dragLength = drag.size();

            link.add(drag);

            HandType handType = (dragLength == 0) ? HandType.ThreeLink
                    : (dragLength == dragCount) ? HandType.ThreeLink_1xn
                    : HandType.ThreeLink_2xn;

            result.addHand(new AiHand(link, handType));
        }

        // output three-block (add drag)

        List<Character> x3Blocks = new ArrayList<Character>(x3Set);

        for (char x3 : x3Blocks) {

            AiPokerList x3Block = new AiPokerList(new char[]{x3, x3, x3});

            AiPokerList drag = retrieveDrags(1, pokers, x2Set);
            int dragLength = drag.size();

            x3Block.add(drag);

            HandType handType = (dragLength == 0) ? HandType.Type_x3
                    : (dragLength == 1) ? HandType.Type_x3_1
                    : HandType.Type_x3_2;

            result.addHand(new AiHand(x3Block, handType));
        }

        // output pairs

        List<Character> pairs = new ArrayList<Character>(x2Set);
        Collections.sort(pairs, CHAR_REVERSE_COMPARATOR);

        for (char x2 : pairs) {
            result.addHand(new AiHand(new char[]{x2, x2}, HandType.Pair));
        }

        // output singles

        for (int n = 0; n < pokers.size(); n++) {
            result.addHand(new AiHand(new char[]{pokers.get(n)}, HandType.Single));
        }
    }

    private static AiPokerList retrieveDrags(
            int dragCount, AiPokerList singles, Set<Character> x2Set) {

        int singleCount = singles.size();
        int pairCount = x2Set.size();

        List<Character> x2List = new ArrayList<Character>(x2Set);
        Collections.sort(x2List);

        AiPokerList result = new AiPokerList();

        boolean usePairs;

        if (pairCount == singleCount) {

            if (pairCount == 0) return result;

            Character maxSingleId = singles.getMaxChar();
            Character maxPairId = x2List.get(pairCount - 1);

            usePairs = maxPairId > maxSingleId;
        }
        else {
            usePairs = pairCount > singleCount;
        }

        if (usePairs) {

            if (pairCount >= dragCount) {

                for (int i = 0; i < dragCount; i++) {
                    char id = x2List.remove(0);
                    result.add(new char[]{id, id});
                    x2Set.remove(id);
                }
            }
        }
        else {

            if (singleCount >= dragCount) {

                for (int i = 0; i < dragCount; i++) {
                    char id = singles.getMinChar();
                    result.add(new char[]{id});
                    singles.remove(new char[]{id});
                }
            }
        }

        return result;
    }

    private static List<AiPokerList> retrieveThreeLinks(Set<Character> x3Set) {

        List<AiPokerList> threeLinks = new ArrayList<AiPokerList>();
        if (x3Set.size() < 2) return threeLinks;

        // 为简化算法，添加'/'作为停止符(charSet里面肯定找不到)
        char[] linkIds = (Phase.LINK_POKERS + '/').toCharArray();

        for (int i = 0, startPos = -1, count = linkIds.length; i < count; i++) {

            if (x3Set.contains(linkIds[i])) {
                if (startPos == -1) startPos = i;
            }
            else {

                int length = i - startPos;

                if (startPos != -1 && length > 1) {

                    char[] pokerIds = new char[length * 3];

                    for (int n = 0; n < length; n++) {

                        pokerIds[n * 3] = linkIds[startPos + n];
                        pokerIds[n * 3 + 1] = linkIds[startPos + n];
                        pokerIds[n * 3 + 2] = linkIds[startPos + n];

                        x3Set.remove(linkIds[startPos + n]);
                    }

                    threeLinks.add(new AiPokerList(pokerIds));
                }

                startPos = -1;
            }
        }

        return threeLinks;
    }

    private static List<AiPokerList> retrieveSingleLinks(AiPokerList pokers) {

        List<AiPokerList> singleLinks = new ArrayList<AiPokerList>();

        while (true) {

            AiPokerList minLink = findFirstMinLink(pokers);
            if (minLink == null) break;

            pokers.remove(minLink);
            singleLinks.add(minLink);
        }

        return singleLinks;
    }

    private static List<AiPokerList> retrieveSingleLinksFillX3(AiPokerList pokers, Set<Character> x3Set) {

        List<AiPokerList> newSingleLinks = new ArrayList<AiPokerList>();
        Iterator<Character> iterator = x3Set.iterator();

        while (iterator.hasNext()) {

            char x3 = iterator.next();
            char[] ids = {x3, x3, x3};

            pokers.add(ids);

            List<AiPokerList> links = retrieveSingleLinks(pokers);

            if (links.size() > 0) {
                newSingleLinks.addAll(links);
                iterator.remove();
            }
            else {
                for (AiPokerList link : links) {
                    pokers.add(link);
                }
                pokers.remove(ids);
            }
        }

        return newSingleLinks;
    }

    private static void expandSingleLinksFillX3(
            AiPokerList pokers, Set<Character> x3Set, List<AiPokerList> singleLinks) {

        if (singleLinks.size() == 0 || x3Set.size() == 0) return;

        Iterator<Character> iterator = x3Set.iterator();

        while (iterator.hasNext()) {

            char x3 = iterator.next();
            char[] ids = {x3, x3, x3};

            pokers.add(ids);
            boolean expanded = false;

            for (AiPokerList link : singleLinks) {

                AiPokerList expandListAi = expandLink(link, pokers);
                pokers.remove(expandListAi);

                int advantage = expandListAi.size();
                for (int i = 0, size = expandListAi.size(); i < size; i++) {
                    if (pokers.contains(expandListAi.get(i))) advantage--;
                }

                if (advantage > 2) {
                    link.add(expandListAi);
                    expanded = true;
                }
                else {
                    pokers.add(expandListAi);
                    pokers.remove(ids);
                }
            }

            if (expanded) iterator.remove();
        }
    }

    private static List<AiPokerList> retrievePairLinks(Set<Character> x2Set) {

        List<AiPokerList> pairLinks = new ArrayList<AiPokerList>();
        if (x2Set.size() < 2) return pairLinks;

        // 为简化算法，添加'/'作为停止符(charSet里面肯定找不到)
        char[] linkIds = (Phase.LINK_POKERS + '/').toCharArray();

        for (int i = 0, startPos = -1, count = linkIds.length; i < count; i++) {

            if (x2Set.contains(linkIds[i])) {
                if (startPos == -1) startPos = i;
            }
            else {

                int length = i - startPos;

                if (startPos != -1 && length > 2) {

                    char[] pokerIds = new char[length * 2];

                    for (int n = 0; n < length; n++) {

                        pokerIds[n * 2] = linkIds[startPos + n];
                        pokerIds[n * 2 + 1] = linkIds[startPos + n];

                        x2Set.remove(linkIds[startPos + n]);
                    }

                    pairLinks.add(new AiPokerList(pokerIds));
                }

                startPos = -1;
            }
        }

        return pairLinks;
    }

    private static List<AiPokerList> retrievePairLinksFillSingleLink(
            AiPokerList pokers, List<AiPokerList> singleLinks, Set<Character> x2Set) {

        List<AiPokerList> newPairLinks = new ArrayList<AiPokerList>();

        for (Iterator<AiPokerList> i = singleLinks.iterator(); i.hasNext(); ) {

            AiPokerList link = i.next();

            // 将一个列表添加到牌池中
            pokers.add(link);

            // 查找新的对子
            List<AiPokerList> x2ListAi = find_1x2(pokers);
            Set<Character> newX2Set = new HashSet<Character>(x2Set);

            // 将新对子添加到原来的对子集合中，形成新的对子集合
            for (AiPokerList x2 : x2ListAi) {
                newX2Set.add(x2.getMinChar());
                pokers.remove(x2);
            }

            // 在新的对子集合中查找连对
            List<AiPokerList> links = retrievePairLinks(newX2Set);

            // 如果没找到，做恢复工作
            if (links.size() == 0) {

                for (AiPokerList x2 : x2ListAi) {
                    pokers.add(x2);
                }

                pokers.remove(link);

                continue;
            }

            // 如果找到连对，还需要判断是否值得拆分原来的连子
            // 判断依据是：
            //      数一下新的连对中的牌的张数
            //      减去在连对中已经出现，并且在牌池中仍然出现的牌的张数，
            //      如果这个数仍然大于原来的连子的长度，那么这个拆分就是值得的
            Set<Character> charSet = new HashSet<Character>();

            for (AiPokerList pair : links) {
                charSet.addAll(pair.charSet());
            }

            int pairLinkCards = charSet.size() * 2;

            for (char id : charSet) {
                if (pokers.contains(id)) pairLinkCards--;
            }

            // 拆分是值得的
            if (pairLinkCards >= link.size()) {

                for (AiPokerList pairLink : links) {
                    newPairLinks.add(pairLink);
                }

                // 清除x2Set中已经被retrievePairLinks抽取出来的项

                for (Iterator<Character> iterator = x2Set.iterator(); iterator.hasNext(); ) {

                    char x2 = iterator.next();

                    if (!newX2Set.contains(x2))
                        iterator.remove();
                }

                // 返还没有能组合成连对的剩余对子
                x2Set.addAll(newX2Set);

                i.remove();
            }
            else { // 拆分不值得的，做恢复工作

                for (AiPokerList x2 : x2ListAi) {
                    pokers.add(x2);
                }

                pokers.remove(link);
            }
        }

        return newPairLinks;
    }

    private static void expandLinks(AiPokerList pokers, List<AiPokerList> singleLinks) {

        for (AiPokerList link : singleLinks) {

            AiPokerList expandListAi = expandLink(link, pokers);

            if (!expandListAi.isEmpty()) {
                link.add(expandListAi);
                pokers.remove(expandListAi);
            }
        }
    }

    private static AiPokerList expandLink(AiPokerList link, AiPokerList pokers) {

        char maxChar = link.getMaxChar();
        char minChar = link.getMinChar();

        int maxIndex = Phase.LINK_POKERS.indexOf(maxChar);
        int minIndex = Phase.LINK_POKERS.indexOf(minChar);

        AiPokerList expandListAi = new AiPokerList();
        char[] linkIds = Phase.LINK_POKERS.toCharArray();

        for (int i = minIndex + 1; i < linkIds.length; i++) {
            char id = linkIds[i];
            if (!pokers.contains(id)) break;
            expandListAi.add(new char[]{id});
        }

        for (int i = maxIndex - 1; i >= 0; i--) {
            char id = linkIds[i];
            if (!pokers.contains(id)) break;
            expandListAi.add(new char[]{id});
        }

        return expandListAi;
    }

    private static void contactSingleLinks(List<AiPokerList> links) {

        if (links.size() < 2) return;

        List<AiPokerList> leftSide = new ArrayList<AiPokerList>();
        List<AiPokerList> rightSide = new ArrayList<AiPokerList>();

        for (AiPokerList link : links) {
            int linkAbility = getLinkAbility(link);
            if (linkAbility == 1) leftSide.add(link);
            if (linkAbility == 2) rightSide.add(link);
        }

        for (AiPokerList left : leftSide) {

            for (Iterator<AiPokerList> iterator = rightSide.iterator(); iterator.hasNext(); ) {

                AiPokerList right = iterator.next();

                int lPos = Phase.LINK_POKERS.indexOf(left.getMinChar());
                int rPos = Phase.LINK_POKERS.indexOf(right.getMaxChar());

                if (lPos + 1 == rPos) {
                    left.add(right);
                    links.remove(right);
                    iterator.remove();
                }
            }
        }
    }

    private static List<AiPokerList> composePairLinks(List<AiPokerList> singleListAi) {

        Map<Integer, List<AiPokerList>> stat = new HashMap<Integer, List<AiPokerList>>();

        for (AiPokerList link : singleListAi) {

            int key = (link.size() << 16) + link.getMinChar();
            List<AiPokerList> listAi = stat.get(key);

            if (listAi == null) {
                listAi = new ArrayList<AiPokerList>();
                stat.put(key, listAi);
            }

            listAi.add(link);
        }

        Collection<List<AiPokerList>> groups = stat.values();
        List<AiPokerList> pairLinks = new ArrayList<AiPokerList>();

        for (List<AiPokerList> group : groups) {

            if (group.size() > 1) {

                AiPokerList link = group.get(0);
                AiPokerList beComposed = group.get(1);

                singleListAi.remove(link);
                singleListAi.remove(beComposed);

                link.add(beComposed);
                pairLinks.add(link);
            }
        }

        return pairLinks;
    }

    public static AiPokerList findRocket(AiPokerList pokers) {

        if (pokers.size() < 2) return null;

        if (pokers.getMaxChar() == 'z' && pokers.get(1) == 'y') {
            return new AiPokerList(new char[]{'y', 'z'});
        }

        return null;
    }

    public static List<AiPokerList> findBomb(AiPokerList pokers) {

        List<AiPokerList> listAi = new ArrayList<AiPokerList>();
        if (pokers.size() < 4) return listAi;


        Map<Character, Integer> counter = pokers.getCounter();
        Set<Character> idSet = counter.keySet();

        for (char id : idSet) {
            if (counter.get(id) == 4) {
                char[] ids = {id, id, id, id};
                listAi.add(new AiPokerList(ids));
            }
        }

        return listAi;
    }


    private static List<AiPokerList> find_1x3(AiPokerList pokers) {

        List<AiPokerList> listAi = new ArrayList<AiPokerList>();

        if (pokers.size() < 3) return listAi;

        Map<Character, Integer> counter = pokers.getCounter();
        Set<Character> idSet = counter.keySet();

        for (char id : idSet) {
            if (counter.get(id) == 3) {
                char[] ids = {id, id, id};
                listAi.add(new AiPokerList(ids));
            }
        }

        return listAi;
    }

    private static List<AiPokerList> find_1x2(AiPokerList pokers) {

        List<AiPokerList> listAi = new ArrayList<AiPokerList>();
        if (pokers.size() < 2) return listAi;

        Map<Character, Integer> counter = pokers.getCounter();
        Set<Character> idSet = counter.keySet();

        for (char id : idSet) {
            if (counter.get(id) == 2) {
                char[] ids = {id, id};
                listAi.add(new AiPokerList(ids));
            }
        }

        return listAi;
    }

    private static AiPokerList findFirstMinLink(AiPokerList pokers) {

        if (pokers.size() < 5) return null;
        Map<Character, Integer> counter = pokers.getCounter();

        char[] linkIds = Phase.LINK_POKERS.toCharArray();

        for (int startPos = 0; startPos < linkIds.length - 4; startPos++) {

            boolean found = true;

            for (int n = 0; n < 5; n++) {
                if (counter.get(linkIds[startPos + n]) == 0) {
                    startPos += n;
                    found = false;
                    break;
                }
            }

            if (found) {
                char[] pokerIds = new char[5];
                System.arraycopy(linkIds, startPos, pokerIds, 0, 5);
                return new AiPokerList(pokerIds);
            }
        }

        return null;
    }

    /**
     * @param link the link
     * @return 0 - none, 1 - left-side, 2 - right-side
     */
    private static int getLinkAbility(AiPokerList link) {
        return link.getMaxChar() < 'a' ? 2 : link.getMinChar() > '7' ? 1 : 0;
    }

    private static final Comparator<Character> CHAR_REVERSE_COMPARATOR =
            new Comparator<Character>() {
                public int compare(Character o1, Character o2) {
                    return o2.compareTo(o1);
                }
            };
    public static final Comparator<AiPokerList> POKER_LIST_COMPARATOR =
            new Comparator<AiPokerList>() {
                public int compare(AiPokerList o1, AiPokerList o2) {
                    return o2.getMaxChar().compareTo(o1.getMaxChar());
                }
            };
}
