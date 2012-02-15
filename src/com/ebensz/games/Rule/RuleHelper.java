package com.ebensz.games.Rule;

import com.ebensz.games.model.hand.*;
import com.ebensz.games.model.poker.Poker;
import com.ebensz.games.model.poker.PokerUtils;
import ice.util.MathUtil;

import java.util.*;

import static com.ebensz.games.model.poker.PokerUtils.counter;

/**
 * User: jason
 * Date: 11-12-21
 * Time: 下午6:08
 */
public class RuleHelper {

    public static Hand validateX4_X2Series(List<Poker> pokers) {
        int size = pokers.size();

        if (size <= 4) return null;

        Map<Poker, Integer> counter = PokerUtils.counter(pokers);
        Set<Poker> pokerSet = counter.keySet();

        int setSize = pokerSet.size();
        switch (size) {
            case 6:
                Poker x4 = findCount(counter, 4);
                if (x4 != null) {
                    Set<Poker> set = counter.keySet();
                    set.remove(x4);

                    if (set.size() == 2) {
                        Iterator<Poker> iterator = set.iterator();

                        Hand[] extensions = new Hand[2];

                        extensions[0] = new Single(iterator.next());
                        extensions[1] = new Single(iterator.next());

                        return new X4_X2(x4, extensions);
                    }
                    else if (setSize == 1) {
                        Iterator<Poker> iterator = set.iterator();

                        Hand[] extensions = new Hand[2];

                        extensions[0] = new Single(iterator.next());
                        extensions[1] = extensions[0];

                        return new X4_X2(x4, extensions);
                    }

                }
                break;
            case 8:
                if (setSize == 2) {

                    Iterator<Poker> iterator = pokerSet.iterator();

                    Poker p1 = iterator.next();
                    Poker p2 = iterator.next();

                    Poker biggerPoker = p1.isBiggerThan(p2) ? p1 : p2;
                    Poker smallerPoker = p1.isBiggerThan(p2) ? p2 : p1;

                    Hand[] extensions = new Hand[2];

                    extensions[0] = new Single(smallerPoker);
                    extensions[1] = extensions[0];

                    return new X4_X2(biggerPoker, extensions);
                }


                x4 = findCount(counter, 4);

                if (x4 != null) {
                    if (setSize == 3) {
                        Poker x2 = findCount(counter, 2);
                        if (x2 != null) {
                            Set<Poker> set = counter.keySet();
                            set.remove(x4);

                            Iterator<Poker> iterator = set.iterator();

                            Hand[] extensions = new Hand[2];

                            extensions[0] = new Pair(iterator.next());
                            extensions[1] = new Pair(iterator.next());

                            return new X4_X2(x4, extensions);
                        }
                    }
                }
                break;

        }


        return null;
    }


    public static Hand isThreeLinkSeries(List<Poker> pokers) {
        int pokerSize = pokers.size();
        if (pokerSize < 3) return null;

        List<Integer> x3 = new ArrayList<Integer>();
        Map<Poker, Integer> counter = PokerUtils.counter(pokers);
        Set<Poker> pokerSet = counter.keySet();

        for (Poker poker : pokerSet) {

            if (!poker.isSmallerThan(Poker._2))
                continue;

            if (counter.get(poker) >= 3) {
                x3.add(poker.ordinal());
            }
        }

        Collections.sort(x3);

        int minPokerId = 0;
        int longest = 0;

        for (int length = 2, size = pokerSet.size(); length <= size; length++) {
            int result = MathUtil.findIncreasingSequence(x3, length);
            if (result != MathUtil.NOT_FOUND) {
                minPokerId = result;
                longest = length;
            }
        }

        if (longest == 0)
            return null;

        int x3_0case = longest * (3 + 0);
        int x3_1_case = longest * (3 + 1);
        int x3_2_case = longest * (3 + 2);

        if (pokerSize == x3_0case || pokerSize == x3_1_case) {
            if (pokerSize == x3_0case) {
                return new ThreeLink(Poker.values()[minPokerId], longest, null);
            }
            else {
                Hand[] extensions = new Hand[longest];

                Poker[] allPokers = Poker.values();
                List<Poker> pokersCopy = new ArrayList<Poker>(pokers);
                for (int i = 0; i < longest; i++) {
                    Poker poker = Poker.values()[minPokerId + i];
                    pokersCopy.remove(poker);
                    pokersCopy.remove(poker);
                    pokersCopy.remove(poker);
                }

                int index = 0;
                for (Poker poker : pokersCopy) {
                    extensions[index++] = new Single(poker);
                }

                return new ThreeLink(Poker.values()[minPokerId], longest, extensions);
            }

        }
        else if (pokerSize == x3_2_case) {
            List<Poker> pokersCopy = new ArrayList<Poker>(pokers);

            for (int i = 0; i < longest; i++) {
                Poker poker = Poker.values()[minPokerId + i];
                pokersCopy.remove(poker);
                pokersCopy.remove(poker);
                pokersCopy.remove(poker);
            }

            Map<Poker, Integer> remainCounter = PokerUtils.counter(pokersCopy);
            for (Poker poker : remainCounter.keySet()) {
                int size = remainCounter.get(poker);
                if (size != 2 && size != 4)
                    return null;
            }


            int index = 0;
            Hand[] extensions = new Hand[longest];
            for (Poker poker : remainCounter.keySet()) {
                int size = remainCounter.get(poker);
                if (size == 2) {
                    extensions[index++] = new Pair(poker);
                }
                else {//size == 4
                    extensions[index++] = new Pair(poker);
                    extensions[index++] = new Pair(poker);
                }

            }

            return new ThreeLink(Poker.values()[minPokerId], longest, extensions);
        }

        return null;
    }

    public static List<PairLink> findBiggerPairLink(PairLink chuPai, List<Poker> playerPoker) {

        List<PairLink> linkPokers = new ArrayList<PairLink>();

        int length = chuPai.size() / 2;
        Poker linkMin = chuPai.getKey();

        List<Integer> x2 = new ArrayList<Integer>();
        Map<Poker, Integer> counter = counter(playerPoker);

        for (Poker poker : counter.keySet()) {

            if (poker.isSmallerThan(Poker._2) && counter.get(poker) >= 2 && poker.isBiggerThan(linkMin)) {
                x2.add(poker.ordinal());
            }
        }

        Collections.sort(x2);

        int min = MathUtil.findIncreasingSequence(x2, length);

        List<Integer> mins = new ArrayList<Integer>();
        while (min != MathUtil.NOT_FOUND) {
            mins.add(min);
            x2.remove((Integer) min);
            min = MathUtil.findIncreasingSequence(x2, length);
        }

        Poker[] allPokers = Poker.values();
        for (int startPoker : mins) {
            linkPokers.add(new PairLink(allPokers[startPoker], length));
        }

        return linkPokers;
    }

    public static List<SingleLink> findBiggerSingleLink(SingleLink chuPai, List<Poker> playerPoker) {

        List<SingleLink> linkPokers = new ArrayList<SingleLink>();

        int length = chuPai.size();
        Poker linkMin = chuPai.getKey();

        List<Integer> x1 = new ArrayList<Integer>();
        Map<Poker, Integer> counter = counter(playerPoker);
        Set<Poker> pokerSet = counter.keySet();

        for (Poker poker : pokerSet) {

            if (!poker.isSmallerThan(Poker._2))
                continue;

            if (counter.get(poker) >= 1 && poker.isBiggerThan(linkMin)) {
                x1.add(poker.ordinal());
            }
        }

        Collections.sort(x1);

        List<Integer> mins = new ArrayList<Integer>();
        int min = MathUtil.findIncreasingSequence(x1, length);
        while (min != MathUtil.NOT_FOUND) {
            mins.add(min);
            x1.remove((Integer) min);
            min = MathUtil.findIncreasingSequence(x1, length);
        }


        Poker[] allPokers = Poker.values();
        for (int startPoker : mins) {
            linkPokers.add(
                    new SingleLink(
                            allPokers[startPoker],
                            length
                    )
            );
        }

        return linkPokers;
    }

    public static List<ThreeLink> findBiggerThreeLinks(ThreeLink chuPai, List<Poker> playerPoker) {

        List<ThreeLink> biggerThreeLinks = new ArrayList<ThreeLink>();

        if (playerPoker.size() < chuPai.size())
            return biggerThreeLinks;

        List<Integer> x3ChuPai = new ArrayList<Integer>(chuPai.size() / 3);
        Map<Poker, Integer> counter = PokerUtils.counter(chuPai.getPokers());
        for (Poker poker : counter.keySet()) {
            if (poker.isBiggerThan(Poker.A)) continue;

            if (counter.get(poker) == 3) {
                x3ChuPai.add(poker.ordinal());
            }
        }
        Collections.sort(x3ChuPai);
        int longestChuPai = MathUtil.findLongestIncreasingSequence(x3ChuPai);
        int minX3PokerChuPai = MathUtil.findIncreasingSequence(x3ChuPai, longestChuPai);

        List<Integer> x3JiePai = new ArrayList<Integer>(playerPoker.size() / 3);
        counter = counter(playerPoker);
        for (Poker poker : counter.keySet()) {
            if (poker.isBiggerThan(Poker.A)) continue;

            if (counter.get(poker) == 3) {
                x3JiePai.add(poker.ordinal());
            }
        }

        Collections.sort(x3JiePai);

        List<Integer> mins = new ArrayList<Integer>();
        int min = MathUtil.findIncreasingSequence(x3JiePai, longestChuPai);
        while (min != MathUtil.NOT_FOUND) {
            mins.add(min);
            x3JiePai.remove((Integer) min);
            min = MathUtil.findIncreasingSequence(x3JiePai, longestChuPai);
        }

        for (Iterator<Integer> iterator = mins.iterator(); iterator.hasNext(); ) {
            int minPokerId = iterator.next();
            if (minPokerId <= minX3PokerChuPai)
                iterator.remove();
        }

        if (mins.size() == 0)
            return biggerThreeLinks;

        Hand[] extensions = chuPai.getExtensions();
        Poker[] allPokers = Poker.values();
        if (extensions == null) {
            for (int minPokerId : mins) {
                ThreeLink threeLink = new ThreeLink(
                        allPokers[minPokerId],
                        longestChuPai,
                        null
                );
                biggerThreeLinks.add(threeLink);
            }
        }
        else {

            if (extensions[0] instanceof Single) {


                for (int minPokerId : mins) {

                    List<Poker> jiePaiCopy = new ArrayList<Poker>(playerPoker);

                    for (int i = 0; i < longestChuPai; i++) {
                        jiePaiCopy.remove(allPokers[minPokerId + i]);
                        jiePaiCopy.remove(allPokers[minPokerId + i]);
                        jiePaiCopy.remove(allPokers[minPokerId + i]);
                    }

                    Collections.sort(jiePaiCopy);

                    Hand[] singles = new Hand[longestChuPai];
                    for (int i = 0; i < longestChuPai; i++) {
                        singles[i] = new Single(jiePaiCopy.remove(0));
                    }

                    ThreeLink threeLink = new ThreeLink(allPokers[minPokerId], longestChuPai, singles);

                    biggerThreeLinks.add(threeLink);
                }
            }
            else {

                for (int minPokerId : mins) {

                    List<Poker> jiePaiCopy = new ArrayList<Poker>(playerPoker);

                    for (int i = 0; i < longestChuPai; i++) {
                        jiePaiCopy.remove(allPokers[minPokerId + i]);
                        jiePaiCopy.remove(allPokers[minPokerId + i]);
                        jiePaiCopy.remove(allPokers[minPokerId + i]);
                    }

                    List<Pair> pairExtensions = new ArrayList<Pair>();

                    Map<Poker, Integer> jiePaiCount = PokerUtils.counter(jiePaiCopy);
                    for (Poker poker : jiePaiCount.keySet()) {
                        if (counter.get(poker) == 2 || counter.get(poker) == 3) {
                            Pair pair = new Pair(poker);
                            pairExtensions.add(pair);
                        }
                        else if (counter.get(poker) == 4) {
                            Pair pair = new Pair(poker);
                            pairExtensions.add(pair);
                            pairExtensions.add(pair);
                        }
                    }

                    if (pairExtensions.size() < longestChuPai) {
                        return biggerThreeLinks;
                    }

                    Collections.sort(pairExtensions);

                    Hand[] pairs = new Hand[longestChuPai];
                    for (int i = 0; i < longestChuPai; i++)
                        pairs[i] = pairExtensions.get(i);

                    ThreeLink threeLink = new ThreeLink(allPokers[minPokerId], longestChuPai, pairs);
                    biggerThreeLinks.add(threeLink);
                }
            }

        }

        return biggerThreeLinks;
    }

    public static boolean existRocket(List<Poker> playerPoker) {

        return playerPoker.contains(Poker.DaWang) && playerPoker.contains(Poker.XiaoWang);
    }

    public static List<Bomb> findBiggerBomb(Poker bombPoker, List<Poker> pokers) {

        List<Bomb> bombs = findBombs(pokers);
        for (Iterator<Bomb> iterator = bombs.iterator(); iterator.hasNext(); ) {

            Bomb bomb = iterator.next();

            if (!bomb.getKey().isBiggerThan(bombPoker))
                iterator.remove();

        }

        return bombs;
    }

    public static List<Bomb> findBombs(List<Poker> pokers) {

        Map<Poker, Integer> pokerCounter = counter(pokers);

        List<Bomb> bombs = new ArrayList<Bomb>(5);

        for (Poker poker : pokerCounter.keySet()) {

            if (pokerCounter.get(poker) == 4)
                bombs.add(new Bomb(poker));

        }

        return bombs;
    }

    public static boolean isBomb(List<Poker> pokers) {

        return pokers.size() == 4 && new HashSet(pokers).size() == 1;
    }

    public static boolean isSingle(List<Poker> pokers) {
        return pokers.size() == 1;
    }

    public static boolean isPair(List<Poker> pokers) {
        return pokers.size() == 2 && counter(pokers).keySet().size() == 1;
    }

    public static boolean isSingleLink(List<Poker> pokers) {
        int size = pokers.size();
        if (size < 5) return false;

        Map<Poker, Integer> counter = counter(pokers);
        List<Integer> pokerIndexes = new ArrayList<Integer>(size);
        for (Poker poker : counter.keySet()) {
            if (poker.isBiggerThan(Poker.A))
                return false;

            if (counter.get(poker) == 1) {
                pokerIndexes.add(poker.ordinal());
            }
            else {
                return false;
            }
        }

        Collections.sort(pokerIndexes);
        return MathUtil.existIncreasingSequence(pokerIndexes, size);
    }

    public static boolean isPairLink(List<Poker> pokers) {

        int size = pokers.size();
        if (size % 2 != 0 || size < 6) return false;

        Map<Poker, Integer> counter = counter(pokers);
        List<Integer> pokerIndexes = new ArrayList<Integer>(size / 2);
        for (Poker poker : counter.keySet()) {
            if (poker.isBiggerThan(Poker.A))
                return false;

            if (counter.get(poker) == 2) {
                pokerIndexes.add(poker.ordinal());
            }
            else {
                return false;
            }
        }

        Collections.sort(pokerIndexes);
        return MathUtil.existIncreasingSequence(pokerIndexes, size / 2);
    }

    public static Hand isX3Series(List<Poker> pokers) {
        int size = pokers.size();
        if (size < 3 || size > 5) return null;

        Map<Poker, Integer> counter = counter(pokers);
        Poker x3 = null;
        Poker x2 = null;
        Poker x1 = null;
        for (Poker poker : counter.keySet()) {
            int count = counter.get(poker);
            switch (count) {
                case 3:
                    x3 = poker;
                    break;
                case 2:
                    x2 = poker;
                    break;
                case 1:
                    x1 = poker;
                    break;
            }
        }

        if (x3 == null)
            return null;

        if (size == 3) {
            return new X3(x3, null);
        }
        else {

            if (size == 4) {
                Hand[] extensions = new Hand[1];
                extensions[0] = new Single(x1);
                return new X3(x3, extensions);
            }
            else {

                if (counter.keySet().size() == 2) {
                    Hand[] extensions = new Hand[1];
                    extensions[0] = new Pair(x2);
                    return new X3(x3, extensions);
                }

            }


        }

        return null;
    }


    public static Poker findCount(Map<Poker, Integer> counter, int requestCount) {

        List<Poker> result = findCounts(counter, requestCount, false);

        if (result.size() > 0)
            return result.get(0);

        return null;
    }

    public static List<Poker> findCounts(Map<Poker, Integer> counter, int requestCount, boolean findAll) {

        List<Poker> result = new ArrayList<Poker>();

        for (Poker poker : counter.keySet()) {

            if (counter.get(poker) == requestCount) {
                result.add(poker);

                if (!findAll)
                    return result;

            }
        }

        return result;
    }

    public static boolean isRocker(List<Poker> pokers) {
        if (pokers.size() != 2) return false;

        return existPoker(pokers, Poker.XiaoWang) && existPoker(pokers, Poker.DaWang);
    }

    public static boolean existPoker(List<Poker> pokers, Poker poker) {
        return Collections.binarySearch(pokers, poker) >= 0;
    }

    public static List<Single> findBiggerSingles(Hand chuPai, List<Poker> playerPoker) {
        List<Single> singles = new ArrayList<Single>();

        Map<Poker, Integer> counter = PokerUtils.counter(playerPoker);

        Set<Poker> pokerSet = counter.keySet();
        List<Poker> pokerSetList = new ArrayList<Poker>(pokerSet);
        Collections.sort(pokerSetList);

        for (Poker poker : pokerSetList) {
            int num = counter.get(poker);

            if ((num == 1) && poker.isBiggerThan(chuPai.getKey())) {
                singles.add(new Single(poker));
            }

        }

        for (Poker poker : pokerSetList) {
            int num = counter.get(poker);

            if ((num == 2 || num == 3) && poker.isBiggerThan(chuPai.getKey())) {
                singles.add(new Single(poker));
            }

        }


        return singles;
    }

    public static List<Pair> findBiggerPairs(Hand chuPai, List<Poker> playerPoker) {
        List<Pair> pairs = new ArrayList<Pair>();

        Map<Poker, Integer> counter = PokerUtils.counter(playerPoker);
        Set<Poker> pokerSet = counter.keySet();

        List<Poker> pokerSetList = new ArrayList<Poker>(pokerSet);
        Collections.sort(pokerSetList);

        for (Poker poker : pokerSetList) {

            int num = counter.get(poker);

            if (num == 2 && poker.isBiggerThan(chuPai.getKey())) {
                pairs.add(new Pair(poker));
            }

        }

        for (Poker poker : pokerSetList) {

            int num = counter.get(poker);

            if (num == 3 && poker.isBiggerThan(chuPai.getKey())) {
                pairs.add(new Pair(poker));
            }

        }

        return pairs;
    }

    public static List<X3> findBiggerX3s(Hand chuPai, List<Poker> playerPoker) {
        Map<Poker, Integer> counter = PokerUtils.counter(playerPoker);
        Poker chuPaiKey = chuPai.getKey();

        List<Poker> x3s = new ArrayList<Poker>();

        List<X3> biggerX3 = new ArrayList<X3>();

        for (Poker poker : counter.keySet()) {
            if (counter.get(poker) >= 3 && poker.isBiggerThan(chuPaiKey)) {
                x3s.add(poker);
            }
        }

        Hand[] extensions = ((X3) chuPai).getExtensions();
        if (extensions == null) {
            for (Poker x3 : x3s) {
                biggerX3.add(new X3(x3, null));
            }
        }
        else {
            if (extensions[0] instanceof Single) {
                for (Poker x3 : x3s) {
                    List<Poker> copy = new ArrayList<Poker>(playerPoker);
                    copy.remove(x3);
                    copy.remove(x3);
                    copy.remove(x3);

                    Collections.sort(copy);

                    Hand[] singles = new Hand[]{
                            new Single(copy.get(0))
                    };

                    biggerX3.add(
                            new X3(x3, singles)
                    );
                }
            }
            else {
                for (Poker x3 : x3s) {
                    List<Poker> copy = new ArrayList<Poker>(playerPoker);
                    copy.remove(x3);
                    copy.remove(x3);
                    copy.remove(x3);

                    Collections.sort(copy);

                    Map<Poker, Integer> copyCounter = PokerUtils.counter(copy);
                    for (Poker poker : copyCounter.keySet()) {
                        if (copyCounter.get(poker) >= 2) {

                            Hand[] pairs = new Hand[]{
                                    new Pair(poker)
                            };

                            biggerX3.add(
                                    new X3(x3, pairs)
                            );

                            break;
                        }
                    }


                }


            }


        }
        return biggerX3;
    }


}
