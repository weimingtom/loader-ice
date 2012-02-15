package com.ebensz.games.logic.player;

import android.util.Log;
import com.ebensz.games.Rule.Rule;
import com.ebensz.games.ai.Ai;
import com.ebensz.games.logic.interactive.Feedback;
import com.ebensz.games.logic.interactive.Message;
import com.ebensz.games.model.*;
import com.ebensz.games.model.hand.ColoredHand;
import com.ebensz.games.model.hand.Hand;
import com.ebensz.games.model.poker.ColoredPoker;
import com.ebensz.games.model.poker.PokerUtils;
import com.ebensz.games.model.poker.PokersInfo;

import java.util.List;
import java.util.Map;

import static com.ebensz.games.logic.interactive.Message.SELECT_CHU_PAI;
import static com.ebensz.games.logic.interactive.Message.SELECT_JIE_PAI;

public class AIDecisionMaker extends DecisionMaker {

    private static final String TAG = AIDecisionMaker.class.getSimpleName();

    public AIDecisionMaker(Ai ai, Feedback feedback, Rule rule) {
        super(rule, feedback);
        this.ai = ai;
    }

    @Override
    protected RobLoaderScore requestRobLoader(Dir self, RobLoaderScore lastHighestScore, PokersInfo pokersInfo) {

        RobLoaderScore score = ai.robLoader(self, lastHighestScore, pokersInfo);

        if (!score.isHigherThan(lastHighestScore))
            return RobLoaderScore.Pass;

        return score;
    }

    @Override
    public boolean dao(Dir loaderDir, Dir self, PokersInfo pokersInfo) {

        return ai.dao(loaderDir, self, pokersInfo);
    }

    @Override
    public boolean gen(Dir loaderDir, Dir self, PokersInfo pokersInfo) {

        return ai.gen(loaderDir, self, pokersInfo);
    }

    @Override
    public boolean fan(Map<Dir, DaoGenFan> daoGenFanMap, Dir self, PokersInfo pokersInfo) {

        return ai.fan(daoGenFanMap, self, pokersInfo);
    }

    @Override
    protected ColoredHand requestChuPai(PokersInfo pokersInfo, Dir self, Dir loaderDir) {

        Hand chuPai = ai.chuPai(pokersInfo, self, loaderDir);

        List<ColoredPoker> coloredPokers = pokersInfo.getShouPaiMap().get(self);

        Log.i(TAG, self + "  ChuPai" + chuPai + " " + coloredPokers);

        List<ColoredPoker> mapedPokers = PokerUtils.mapColoredPokers(
                chuPai.getPokers(),
                coloredPokers
        );

        ChuPaiEvent chuPaiEvent = new ChuPaiEvent(self);
        chuPaiEvent.chuPai = new ColoredHand(chuPai, mapedPokers);

        feedback.onHandled(new Message(SELECT_CHU_PAI, chuPaiEvent));

        return chuPaiEvent.chuPai;
    }


    @Override
    protected ColoredHand requestJiePai(Dir chuPaiDir, ColoredHand chuPai, PokersInfo pokersInfo, Dir self, Dir loaderDir) {

        Hand jiePai = ai.jiePai(chuPaiDir, chuPai.getHand(), pokersInfo, self, loaderDir);

        List<ColoredPoker> coloredPokers = pokersInfo.getShouPaiMap().get(self);

        Log.i(TAG, self + "JiePai" + (jiePai == null ? "bu yao " : jiePai) + "  " + coloredPokers);

        JiePaiEvent jiePaiEvent = new JiePaiEvent(chuPaiDir, chuPai, self);

        if (jiePai == Hand.BU_YAO) {
            jiePaiEvent.jiePai = ColoredHand.BU_YAO;
        }
        else {
            List<ColoredPoker> mapedPokers = PokerUtils.mapColoredPokers(
                    jiePai.getPokers(),
                    coloredPokers
            );

            ColoredHand coloredHand = new ColoredHand(jiePai, mapedPokers);

            jiePaiEvent.jiePai = coloredHand;

        }

        feedback.onHandled(new Message(SELECT_JIE_PAI, jiePaiEvent));

        return jiePaiEvent.jiePai;
    }


    @Override
    public void onFeedback(Feedback feedback, Message msg) {

    }

    private Ai ai;
}
