package com.ebensz.games.ai;

import com.ebensz.games.ai.bean.AiHand;
import com.ebensz.games.ai.bean.HandType;
import com.ebensz.games.model.hand.*;
import com.ebensz.games.model.poker.Poker;

import java.util.*;

/**
 * User: Mike.Hu
 * Date: 11-12-22
 * Time: 下午5:10
 */
public class HandAdapter {


    public static Hand parse(AiHand aiHand) {

        if (aiHand == null) return null;

        HandType type = aiHand.getType();

        Hand hand = null;

        switch (type) {
            case Rocket:
                hand = new Rocket();
                break;
            case Bomb:
                hand = buildBomb(aiHand);
                break;
            case Type_x4_2x2:
                hand = buildX4_2X2(aiHand);
                break;
            case Type_x4_2:
                hand = buildX4_X2(aiHand);
                break;
            case ThreeLink:
                hand = buildThreeLink(aiHand);
                break;
            case ThreeLink_2xn:
                hand = buildThreeLink_2xn(aiHand);
                break;
            case ThreeLink_1xn:
                hand = buildThreeLink_1xn(aiHand);
                break;
            case Type_x3:
            case Type_x3_2:
            case Type_x3_1:
                hand = buildX3(aiHand);
                break;
            case PairLink:
                hand = buildPairLink(aiHand);
                break;
            case SingleLink:
                hand = buildSingleLink(aiHand);
                break;
            case Pair:
                hand = buildPair(aiHand);
                break;
            case Single:
                hand = new Single(translateToPoker(aiHand.getSortChar()));
                break;
        }

        return hand;
    }

    private static Hand buildSingleLink(AiHand aiHand) {
        char maxPokerChar = aiHand.getSortChar();
        Poker maxPoker = translateToPoker(maxPokerChar);
        int length = aiHand.size();

        return new SingleLink(Poker.values()[maxPoker.ordinal() - length + 1], length);
    }

    private static Hand buildPair(AiHand aiHand) {
        return new Pair(translateToPoker(aiHand.getSortChar()));
    }

    private static Hand buildPairLink(AiHand aiHand) {
        return new PairLink(translateToPoker(aiHand.getMinChar()), aiHand.size() / 2);
    }

    private static Hand buildX3(AiHand aiHand) {

        Map<Character, Integer> counter = aiHand.getCounter();
        Character extend = null;
        Character x3 = null;

        Hand[] extensions = null;

        for (Character character : counter.keySet()) {

            if (counter.get(character) == 3) {
                x3 = character;
            }
            else if (counter.get(character) != 0) {
                extend = character;
            }
        }

        HandType type = aiHand.getType();
        if (type != HandType.Type_x3) {

            extensions = new Hand[1];

            Poker extensionPoker = translateToPoker(extend);

            extensions[0] = (type == HandType.Type_x3_1)
                    ? new Single(extensionPoker)
                    : new Pair(extensionPoker);

        }

        return new X3(translateToPoker(x3), extensions);
    }

    private static Hand buildThreeLink_1xn(AiHand aiHand) {
        Map<Character, Integer> counter = aiHand.getCounter();

        List<Character> x3List = new ArrayList<Character>();

        Set<Character> x1Set = new HashSet<Character>();

        for (Character character : counter.keySet()) {
            int count = counter.get(character);

            if (count == 3) {
                x3List.add(character);
            }
            else if (count == 1) {
                x1Set.add(character);
            }
        }

        if (x3List.size() != x1Set.size())
            throw new IllegalStateException("x3List= " + x3List + " ,x1Set " + x1Set);

        Collections.sort(x3List);

        Poker key = translateToPoker(x3List.get(0));
        int length = x3List.size();

        Hand[] extensions = new Hand[length];
        int index = 0;
        for (Character character : x1Set) {
            extensions[index++] = new Single(translateToPoker(character));
        }

        return new ThreeLink(key, length, extensions);
    }

    private static Hand buildThreeLink_2xn(AiHand aiHand) {
        Map<Character, Integer> counter = aiHand.getCounter();
        List<Character> x3List = new ArrayList<Character>();
        Set<Character> x2Set = new HashSet<Character>();

        for (Character character : counter.keySet()) {
            int count = counter.get(character);
            if (count == 3) {
                x3List.add(character);
            }
            else {
                x2Set.add(character);
            }
        }

        Collections.sort(x3List);


        Poker key = translateToPoker(x3List.get(0));
        int length = x3List.size();

        Hand[] extensions = new Hand[length];
        int index = 0;
        for (Character character : x2Set) {
            extensions[index++] = new Pair(translateToPoker(character));
        }


        return new ThreeLink(key, length, extensions);
    }

    private static Hand buildThreeLink(AiHand aiHand) {
        Poker key = translateToPoker(aiHand.getMinChar());
        int length = aiHand.size() / 3;

        return new ThreeLink(key, length, null);
    }

    private static Hand buildX4_X2(AiHand aiHand) {
        Character x4 = aiHand.getSortChar();
        Poker poker = translateToPoker(x4);

        Hand[] _1x1 = new Hand[2];

        Set<Character> set = aiHand.charSet();
        int index = 0;
        for (Character c : set) {
            if (c == x4) continue;

            Poker x1Poker = translateToPoker(c);
            _1x1[index++] = new Single(x1Poker);
        }

        return new X4_X2(poker, _1x1);
    }

    private static Hand buildX4_2X2(AiHand aiHand) {

        Character x4 = aiHand.getSortChar();
        Poker poker = translateToPoker(x4);

        Hand[] _2x2 = new Hand[2];

        Set<Character> set = aiHand.charSet();
        int index = 0;
        for (Character c : set) {
            if (c == x4) continue;

            Poker x2Poker = translateToPoker(c);
            _2x2[index++] = new Pair(x2Poker);
        }

        return new X4_X2(poker, _2x2);
    }

    private static Hand buildBomb(AiHand aiHand) {
        Character sortChar = aiHand.getSortChar();
        return new Bomb(translateToPoker(sortChar));
    }

    private static Poker translateToPoker(char c) {

        switch (c) {

            case '3':
                return Poker._3;
            case '4':
                return Poker._4;
            case '5':
                return Poker._5;
            case '6':
                return Poker._6;
            case '7':
                return Poker._7;
            case '8':
                return Poker._8;
            case '9':
                return Poker._9;
            case 'a':
                return Poker._10;
            case 'b':
                return Poker.J;
            case 'c':
                return Poker.Q;
            case 'd':
                return Poker.K;
            case 'e':
                return Poker.A;
            case 'x':
                return Poker._2;
            case 'y':
                return Poker.XiaoWang;
            case 'z':
                return Poker.DaWang;
        }

        throw new IllegalArgumentException(" " + c);
    }

}
