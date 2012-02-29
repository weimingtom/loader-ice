package com.ebensz.games.Rule;

import com.ebensz.games.model.RobLoaderScore;
import com.ebensz.games.model.hand.*;
import com.ebensz.games.model.poker.Poker;
import com.ebensz.games.model.poker.PokerUtils;

import java.util.*;

import static com.ebensz.games.Rule.RuleHelper.*;

/**
 * User: Mike.Hu
 * Date: 11-12-20
 * Time: 上午10:46
 */
public class DefaultRule implements Rule {

    @Override
    public Hand indentify(List<Poker> pokers) {

        Collections.sort(pokers);

        if (isRocker(pokers)) //    Rocket
            return new Rocket();

        if (isBomb(pokers))   // Bomb
            return new Bomb(pokers.get(0));

        Hand x4_x2 = validateX4_X2Series(pokers);
        if (x4_x2 != null)  // x4_x2
            return x4_x2;

        Hand threeLinkSeries = isThreeLinkSeries(pokers);
        if (threeLinkSeries != null)//    ThreeLink
            return threeLinkSeries;

        Hand x3Series = isX3Series(pokers);
        if (x3Series != null) //   x3
            return x3Series;

        if (isPairLink(pokers)) //    PairLink
            return new PairLink(pokers.get(0), pokers.size() / 2);

        if (isSingleLink(pokers)) //    SingleLink
            return new SingleLink(pokers.get(0), pokers.size());

        if (isPair(pokers))  //    Pair
            return new Pair(pokers.get(0));

        if (isSingle(pokers)) //    Single
            return new Single(pokers.get(0));

        return null;
    }

    @Override
    public boolean validateRobLoader(RobLoaderScore score, RobLoaderScore lastHighestScore) {

        return score == RobLoaderScore.Pass || score.isHigherThan(lastHighestScore);
    }

    @Override
    public boolean validateChuPai(Hand chuPai) {

        return true;
    }

    @Override
    public boolean validateJiePai(Hand chuPai, Hand jiePai) throws IllegalArgumentException {

        if (jiePai == Hand.BU_YAO)
            return true;

        if (!validateChuPai(jiePai))
            return false;

        if (chuPai instanceof Rocket) {
            return false;
        }
        else if (chuPai instanceof Bomb) {

            if (jiePai instanceof Rocket)
                return true;

            return (jiePai instanceof Bomb) && (jiePai.getKey().isBiggerThan(chuPai.getKey()));
        }
        else {

            if (jiePai instanceof Rocket || jiePai instanceof Bomb)
                return true;

            if (jiePai.getPokers().length != chuPai.getPokers().length)
                return false;

            Class<? extends Hand> chuPaiType = chuPai.getClass();
            Class<? extends Hand> jiePaiType = jiePai.getClass();

            if (chuPaiType != jiePaiType) return false;

            if (!jiePai.getKey().isBiggerThan(chuPai.getKey()))
                return false;

            return true;

        }

    }

    @Override
    public List<Hand> requestSuggestion(Hand chuPai, final List<Poker> playerPoker) {

        List<Hand> biggerHands = new ArrayList<Hand>();

        if (chuPai instanceof Rocket)
            return biggerHands;


        if (existRocket(playerPoker)) {
            biggerHands.add(new Rocket());
        }

        if (chuPai instanceof Bomb) {

            List<Bomb> biggerBombs = findBiggerBomb(chuPai.getKey(), playerPoker);

            biggerHands.addAll(biggerBombs);

            Collections.sort(biggerHands);

            return biggerHands;
        }

        List<Bomb> bombs = findBombs(playerPoker);
        for (Bomb bomb : bombs) {
            if (!biggerHands.contains(bomb))
                biggerHands.add(bomb);
        }


        if (playerPoker.size() < chuPai.size())
            return biggerHands;

        //X4-X2
//        Poker bombPoker = validateBombSeries(showChuPai);
//        if (bombPoker != null) {
//
//        }

        //    ThreeLink
        if (chuPai instanceof ThreeLink) {

            List<ThreeLink> biggerThreeLink = findBiggerThreeLinks((ThreeLink) chuPai, playerPoker);

            biggerHands.addAll(biggerThreeLink);

            Collections.sort(biggerHands);

            return biggerHands;
        }

        //   x3
        if (chuPai instanceof X3) {

            List<X3> biggerX3 = findBiggerX3s(chuPai, playerPoker);

            biggerHands.addAll(biggerX3);

            Collections.sort(biggerHands);

            return biggerHands;
        }

        //    PairLink
        if (chuPai instanceof PairLink) {

            List<PairLink> biggerPairLink = findBiggerPairLink((PairLink) chuPai, playerPoker);

            biggerHands.addAll(biggerPairLink);

            Collections.sort(biggerHands);

            return biggerHands;
        }


        //    SingleLink
        if (chuPai instanceof SingleLink) {

            List<SingleLink> biggerSingleLink = findBiggerSingleLink((SingleLink) chuPai, playerPoker);

            biggerHands.addAll(biggerSingleLink);

            Collections.sort(biggerHands);

            return biggerHands;
        }


        //Pair
        if (chuPai instanceof Pair) {

            List<Pair> pairs = findBiggerPairs(chuPai, playerPoker);

            biggerHands.addAll(pairs);

            Collections.sort(biggerHands, new Comparator<Hand>() {
                @Override
                public int compare(Hand handA, Hand handB) {
                    if (handA instanceof Rocket)
                        return -1;

                    if (handB instanceof Rocket)
                        return 1;

                    Map<Poker, Integer> counter = PokerUtils.counter(playerPoker);

                    return counter.get(handA.getKey()) - counter.get(handB.getKey());
                }
            });


            return biggerHands;
        }


        if (chuPai instanceof Single) {

            List<Single> singles = findBiggerSingles(chuPai, playerPoker);

            biggerHands.addAll(singles);

            Collections.sort(biggerHands, new Comparator<Hand>() {
                @Override
                public int compare(Hand handA, Hand handB) {

                    Map<Poker, Integer> counter = PokerUtils.counter(playerPoker);

                    Poker keyA = handA.getKey();
                    Poker keyB = handB.getKey();

                    int sub = counter.get(keyA) - counter.get(keyB);
                    if (sub != 0) return sub;

                    return keyA.ordinal() - keyB.ordinal();
                }
            });

            return biggerHands;
        }

        return biggerHands;
    }

    @Override
    public boolean checkExistBiggerHand(Hand chuPai, List<Poker> shouPai) {
        return true;
    }

}
