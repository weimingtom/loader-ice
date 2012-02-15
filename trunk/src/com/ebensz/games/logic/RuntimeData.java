package com.ebensz.games.logic;

import com.ebensz.games.Rule.Rule;
import com.ebensz.games.logic.player.Player;
import com.ebensz.games.logic.settle.SettleTool;
import com.ebensz.games.model.DaoGenFan;
import com.ebensz.games.model.Dir;
import com.ebensz.games.model.RobLoaderScore;
import com.ebensz.games.model.poker.PokersInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Mike.Hu
 * Date: 11-11-1
 * Time: 下午5:03
 */
public class RuntimeData {


    public RuntimeData() {
        daoGenFanMap = new HashMap<Dir, DaoGenFan>(3);
    }

    public void reset() {
        daoGenFanMap.put(Dir.Left, DaoGenFan.Pass);
        daoGenFanMap.put(Dir.Right, DaoGenFan.Pass);
        daoGenFanMap.put(Dir.Outside, DaoGenFan.Pass);
    }

    public Map<Dir, Player> getPlayerMap() {
        return playerMap;
    }

    public void setPlayerMap(Map<Dir, Player> playerMap) {
        this.playerMap = playerMap;
    }

    public void setPokersInfo(PokersInfo pokersInfo) {
        this.pokersInfo = pokersInfo;
    }

    public PokersInfo getPokersInfo() {
        return pokersInfo;
    }

    public void setLoaderDir(Dir loaderDir) {
        this.loaderDir = loaderDir;
    }

    public Dir getLoaderDir() {

        return loaderDir;
    }

    public void setSettleTool(SettleTool settleTool) {
        this.settleTool = settleTool;
    }

    public SettleTool getSettleTool() {
        return settleTool;
    }

    public void setLoaderJiaoFen(RobLoaderScore highestScore) {
        this.highestScore = highestScore;
    }

    public RobLoaderScore getHighestScore() {
        return highestScore;
    }

    public Map<Dir, DaoGenFan> getDaoGenFanMap() {
        return daoGenFanMap;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public Dir getFaPaiStartDir() {
        return faPaiStartDir;
    }

    public void setFaPaiStartDir(Dir faPaiStartDir) {
        this.faPaiStartDir = faPaiStartDir;
    }

    private Dir faPaiStartDir;
    private Rule rule;
    private Map<Dir, Player> playerMap;
    private PokersInfo pokersInfo;
    private SettleTool settleTool;
    private Dir loaderDir;
    private RobLoaderScore highestScore;
    private Map<Dir, DaoGenFan> daoGenFanMap;
}
