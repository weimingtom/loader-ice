package com.ebensz.games.logic.player;

import android.util.Log;
import com.ebensz.games.Rule.Rule;
import com.ebensz.games.exception.ChuPaiErrorException;
import com.ebensz.games.exception.JiePaiErrorException;
import com.ebensz.games.exception.RobLoaderException;
import com.ebensz.games.logic.interactive.Feedback;
import com.ebensz.games.model.DaoGenFan;
import com.ebensz.games.model.Dir;
import com.ebensz.games.model.RobLoaderScore;
import com.ebensz.games.model.hand.ColoredHand;
import com.ebensz.games.model.poker.PokersInfo;

import java.util.Map;

/**
 * User: Mike.Hu
 * Date: 11-11-28
 * Time: 下午3:53
 */
public abstract class DecisionMaker implements Feedback.Handler {
    private static final String TAG = DecisionMaker.class.getSimpleName();

    protected DecisionMaker(Rule rule, Feedback feedback) {
        this.feedback = feedback;
        this.rule = rule;
    }

    /**
     * 叫分.
     *
     * @param lastHighestScore
     * @param self
     * @param pokersInfo       牌面信息  @return 叫的分
     */
    public RobLoaderScore robLoader(RobLoaderScore lastHighestScore, Dir self, PokersInfo pokersInfo) {

        RobLoaderScore score = requestRobLoader(self, lastHighestScore, pokersInfo);

        if (score == null) {
            Log.i(TAG, "rob loader interrupted !");
            return null;
        }

        if (!rule.validateRobLoader(score, lastHighestScore))
            throw new RobLoaderException("last highest score:" + lastHighestScore + "score:" + score);

        return score;
    }

    public abstract boolean dao(Dir loaderDir, Dir self, PokersInfo pokersInfo);

    public abstract boolean gen(Dir loaderDir, Dir self, PokersInfo pokersInfo);

    public abstract boolean fan(Map<Dir, DaoGenFan> daoGenFanMap, Dir self, PokersInfo pokersInfo);

    /**
     * 主动出牌.
     *
     * @param pokersInfo 牌面信息
     * @param self       自己所在的位置
     * @param loaderDir  地主所在的位置
     * @return 要出的牌
     */
    ColoredHand chuPai(PokersInfo pokersInfo, Dir self, Dir loaderDir) {

        ColoredHand chuPai = requestChuPai(pokersInfo, self, loaderDir);

        System.out.println("chu pai" + self + ", " + chuPai);

        if (!rule.validateChuPai(chuPai.getHand()))
            throw new ChuPaiErrorException(chuPai.toString());

        return chuPai;
    }

    /**
     * 接别人的牌
     *
     * @param chuPaiDir  出牌方
     * @param chuPai     出的牌
     * @param pokersInfo 牌面信息
     * @param self       自己所在的位置
     * @param loaderDir  地主所在的位置
     * @return 用来接的牌
     */
    ColoredHand jiePai(Dir chuPaiDir, ColoredHand chuPai, PokersInfo pokersInfo, Dir self, Dir loaderDir) {

        ColoredHand jiePai = requestJiePai(chuPaiDir, chuPai, pokersInfo, self, loaderDir);

        System.out.println("jie pai  self=" + self + ", " + jiePai + ",chu dir = " + chuPaiDir);

        if (jiePai == ColoredHand.BU_YAO)
            return jiePai;

        if (!rule.validateJiePai(chuPai.getHand(), jiePai.getHand()))
            throw new JiePaiErrorException(chuPai.toString());

        return jiePai;
    }

    protected abstract RobLoaderScore requestRobLoader(Dir self, RobLoaderScore lastHighestScore, PokersInfo pokersInfo);

    protected abstract ColoredHand requestChuPai(PokersInfo pokersInfo, Dir self, Dir loaderDir);


    protected abstract ColoredHand requestJiePai(Dir chuPaiDir, ColoredHand chuPai, PokersInfo pokersInfo, Dir self, Dir loaderDir);

    protected Feedback feedback;
    protected Rule rule;
}
