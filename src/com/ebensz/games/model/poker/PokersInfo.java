package com.ebensz.games.model.poker;

import com.ebensz.games.model.Dir;
import com.ebensz.games.model.History;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PokersInfo {

    public PokersInfo(List<ColoredPoker> allPokers, History history) {

        this.history = history;

        this.allPokers = allPokers;
        shouPaiMap = new HashMap<Dir, List<ColoredPoker>>(3);

        for (Dir dir : Dir.values()) {
            shouPaiMap.put(dir, new ArrayList<ColoredPoker>());
        }
    }


    public List<ColoredPoker> getLeftThree() {
        return leftThree;
    }

    public Map<Dir, List<ColoredPoker>> getShouPaiMap() {
        return shouPaiMap;
    }

    public List<Poker> getShouPai(Dir dir) {
        return PokerUtils.parseToPokers(shouPaiMap.get(dir));
    }

    public List<Poker> getLeftThreePokers() {
        return PokerUtils.parseToPokers(leftThree);
    }

    public List<ColoredPoker> getAllPokers() {
        return allPokers;
    }

    public void setLeftThree(List<ColoredPoker> leftThree) {
        this.leftThree = leftThree;
    }

    public History getHistory() {
        return history;
    }

    private List<ColoredPoker> allPokers;

    /**
     * 地主的三张牌
     */
    private List<ColoredPoker> leftThree;
    private Map<Dir, List<ColoredPoker>> shouPaiMap;
    private History history;
}
