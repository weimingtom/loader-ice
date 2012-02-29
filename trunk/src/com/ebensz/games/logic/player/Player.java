package com.ebensz.games.logic.player;

import com.ebensz.games.model.DaoGenFan;
import com.ebensz.games.model.Dir;
import com.ebensz.games.model.RobLoaderScore;
import com.ebensz.games.model.Role.Role;
import com.ebensz.games.model.hand.ColoredHand;
import com.ebensz.games.model.poker.PokersInfo;

import java.util.Map;

/**
 * User: Mike.Hu
 * Date: 11-11-28
 * Time: 下午2:59
 */
public class Player {

    public Player(Role role) {
        this.role = role;
    }

    /**
     * 叫分.
     *
     * @param lastHighestScore
     * @param self
     * @param pokersInfo       牌面信息  @return 叫的分
     */
    public RobLoaderScore robLoader(RobLoaderScore lastHighestScore, Dir self, PokersInfo pokersInfo) {
        System.out.println("robLoader  " + self);
        return decisionMaker.robLoader(lastHighestScore, self, pokersInfo);
    }

    /**
     * 倒
     *
     * @param loaderDir
     * @param self
     * @param pokersInfo
     * @return
     */
    public boolean dao(Dir loaderDir, Dir self, PokersInfo pokersInfo) {
        System.out.println("dao  " + self);
        return decisionMaker.dao(loaderDir, self, pokersInfo);
    }

    /**
     * 跟
     *
     * @param loaderDir
     * @param self
     * @param pokersInfo
     * @return
     */
    public boolean gen(Dir loaderDir, Dir self, PokersInfo pokersInfo) {
        System.out.println("gen  " + self);
        return decisionMaker.gen(loaderDir, self, pokersInfo);
    }

    /**
     * 反
     *
     * @param daoGenFanMap
     * @param self
     * @param pokersInfo
     * @return
     */
    public boolean fan(Map<Dir, DaoGenFan> daoGenFanMap, Dir self, PokersInfo pokersInfo) {
        System.out.println("fan  " + self);
        return decisionMaker.fan(daoGenFanMap, self, pokersInfo);
    }

    /**
     * 主动出牌.
     *
     * @param pokersInfo 牌面信息
     * @param self       自己所在的位置
     * @param loaderDir  地主所在的位置
     * @return 要出的牌
     */
    public ColoredHand chuPai(PokersInfo pokersInfo, Dir self, Dir loaderDir) {
        System.out.println("showChuPai  " + self);
        return decisionMaker.chuPai(pokersInfo, self, loaderDir);
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
    public ColoredHand jiePai(Dir chuPaiDir, ColoredHand chuPai, PokersInfo pokersInfo, Dir self, Dir loaderDir) {

        ColoredHand coloredPokers = decisionMaker.jiePai(chuPaiDir, chuPai, pokersInfo, self, loaderDir);

        return coloredPokers;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (role != null ? !role.equals(player.role) : player.role != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return role != null ? role.hashCode() : 0;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public DecisionMaker getDecisionMaker() {
        return decisionMaker;
    }

    public void setDecisionMaker(DecisionMaker decisionMaker) {
        this.decisionMaker = decisionMaker;
    }

    private Role role;
    private transient DecisionMaker decisionMaker;
}
