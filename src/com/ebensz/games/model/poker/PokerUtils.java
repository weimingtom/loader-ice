package com.ebensz.games.model.poker;

import java.util.*;

/**
 * User: Mike.Hu
 * Date: 11-12-6
 * Time: 下午4:25
 */
public class PokerUtils {

    public static List<Poker> parseToPokers(List<ColoredPoker> pokers) {
        if (pokers == null || pokers.size() == 0)
            return null;

        List<Poker> pokerList = new ArrayList<Poker>(pokers.size());

        for (ColoredPoker coloredPoker : pokers) {
            pokerList.add(coloredPoker.getPoker());
        }

        return pokerList;
    }

    public static List<ColoredPoker> mapColoredPokers(List<Poker> pokers, List<ColoredPoker> coloredPokers) {

        List<ColoredPoker> copy = new ArrayList<ColoredPoker>(coloredPokers);

        List<ColoredPoker> colored = new ArrayList<ColoredPoker>(pokers.size());

        for (Poker poker : pokers) {
            colored.add(findAndPop(poker, copy));
        }

        return colored;
    }

    public static List<ColoredPoker> mapColoredPokers(Poker[] pokers, List<ColoredPoker> coloredPokers) {
        return mapColoredPokers(Arrays.asList(pokers), coloredPokers);
    }

    public static char[] toCharArray(List<Poker> pokers) {

        StringBuilder stringBuilder = new StringBuilder();

        for (Poker poker : pokers) {
            stringBuilder.append(PokerUtils.toChar(poker));
        }

        return stringBuilder.toString().toCharArray();
    }


    public static char[] toArray(List<ColoredPoker> pokers) {

        StringBuilder stringBuilder = new StringBuilder();

        for (ColoredPoker poker : pokers) {
            stringBuilder.append(PokerUtils.toChar(poker.getPoker()));
        }

        return stringBuilder.toString().toCharArray();
    }

    public static Map<Poker, Integer> counter(Collection<Poker> pokers) {

        Map<Poker, Integer> counter = new HashMap<Poker, Integer>(pokers.size());

        for (Poker poker : pokers) {

            if (counter.containsKey(poker)) {
                counter.put(poker, counter.get(poker) + 1);
            }
            else {
                counter.put(poker, 1);
            }

        }
        return counter;
    }

    public static char toChar(Poker poker) {

        switch (poker) {
            case _3:
                return '3';
            case _4:
                return '4';
            case _5:
                return '5';
            case _6:
                return '6';
            case _7:
                return '7';
            case _8:
                return '8';
            case _9:
                return '9';
            case _10:
                return 'a';
            case J:
                return 'b';
            case Q:
                return 'c';
            case K:
                return 'd';
            case A:
                return 'e';
            case _2:
                return 'x';
            case XiaoWang:
                return 'y';
            case DaWang:
                return 'z';
        }

        return '-';
    }

    private static ColoredPoker findAndPop(Poker poker, List<ColoredPoker> copy) {

        for (Iterator<ColoredPoker> iterator = copy.iterator(); iterator.hasNext(); ) {
            ColoredPoker next = iterator.next();

            if (next.getPoker() == poker) {
                iterator.remove();
                return next;
            }
        }

        throw new IllegalArgumentException("poker = " + poker + " copy = " + copy);
    }

    public static Map<Poker, Integer> counter(Poker[] pokers) {
        return counter(Arrays.asList(pokers));
    }
}
