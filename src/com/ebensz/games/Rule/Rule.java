package com.ebensz.games.Rule;

import com.ebensz.games.model.RobLoaderScore;
import com.ebensz.games.model.hand.Hand;
import com.ebensz.games.model.poker.Poker;

import java.util.List;

/**
 * User: Mike.Hu
 * Date: 11-12-20
 * Time: 上午10:45
 */
public interface Rule {

    public Hand indentify(List<Poker> pokers);

    boolean validateRobLoader(RobLoaderScore score, RobLoaderScore lastHighestScore);

    boolean validateChuPai(Hand chuPai);

    boolean validateJiePai(Hand chuPai, Hand jiePai) throws RuntimeException;

    List<Hand> requestSuggestion(Hand chuPai, List<Poker> playerPoker);

    boolean checkExistBiggerHand(Hand chuPai, List<Poker> shouPai);
}
