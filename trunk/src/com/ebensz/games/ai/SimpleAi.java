package com.ebensz.games.ai;

import com.ebensz.games.ai.bean.AiHand;
import com.ebensz.games.ai.bean.AiHistory;
import com.ebensz.games.ai.bean.AiPokerList;
import com.ebensz.games.ai.bean.Phase;
import com.ebensz.games.ai.engine.EngineImpl;
import com.ebensz.games.model.DaoGenFan;
import com.ebensz.games.model.Dir;
import com.ebensz.games.model.RobLoaderScore;
import com.ebensz.games.model.hand.Hand;
import com.ebensz.games.model.poker.ColoredPoker;
import com.ebensz.games.model.poker.Poker;
import com.ebensz.games.model.poker.PokersInfo;

import java.util.List;
import java.util.Map;

import static com.ebensz.games.model.poker.PokerUtils.toArray;
import static com.ebensz.games.model.poker.PokerUtils.toCharArray;

/**
 * User: Mike.Hu
 * Date: 11-11-28
 * Time: 下午6:10
 */
public class SimpleAi implements Ai {

    public SimpleAi() {
        engine = new EngineImpl();
    }

    @Override
    public RobLoaderScore robLoader(Dir self, RobLoaderScore lastHighestScore, PokersInfo pokersInfo) {

        List<Poker> pokerList = pokersInfo.getShouPai(self);
        AiPokerList aiPokerList = new AiPokerList(toCharArray(pokerList));
        int points = engine.robLoader(aiPokerList);

        return RobLoaderScore.values()[points];
    }

    @Override
    public boolean dao(Dir loaderDir, Dir self, PokersInfo pokersInfo) {

        List<Poker> pokerList = pokersInfo.getShouPai(self);
        AiPokerList aiPokerList = new AiPokerList(toCharArray(pokerList));
        int points = engine.robLoader(aiPokerList);

        return points > EngineImpl.FAN_POINT_VALUE;
    }

    @Override
    public boolean gen(Dir loaderDir, Dir self, PokersInfo pokersInfo) {
        return dao(null, self, pokersInfo);
    }

    @Override
    public boolean fan(Map<Dir, DaoGenFan> daoGenFanMap, Dir self, PokersInfo pokersInfo) {
        return dao(null, self, pokersInfo);
    }

    @Override
    public Hand chuPai(PokersInfo pokersInfo, Dir self, Dir loaderDir) {

        List<ColoredPoker> leftThree = pokersInfo.getLeftThree();
        AiPokerList aceOfBase = new AiPokerList(toArray(leftThree));

        List<Poker> shouPai = pokersInfo.getShouPai(self);
        AiPokerList aiPokerList = new AiPokerList(toCharArray(shouPai));

        Phase phase = new Phase(loaderDir, aceOfBase, new AiHistory(pokersInfo.getHistory()));

        AiHand deliver = engine.deliver(aiPokerList, phase, self);
        return HandAdapter.parse(deliver);
    }

    @Override
    public Hand jiePai(Dir chuPaiDir, Hand chuPai, PokersInfo pokersInfo, Dir self, Dir loaderDir) {

        List<ColoredPoker> leftThree = pokersInfo.getLeftThree();
        AiPokerList aceOfBase = new AiPokerList(toArray(leftThree));

        List<Poker> shouPai = pokersInfo.getShouPai(self);
        AiPokerList aiPokerList = new AiPokerList(toCharArray(shouPai));

        Phase phase = new Phase(loaderDir, aceOfBase, new AiHistory(pokersInfo.getHistory()));

        AiHand deliver = engine.contact(aiPokerList, phase, self);

        if (deliver == null) return null;

        return HandAdapter.parse(deliver);
    }

    private EngineImpl engine;
}
