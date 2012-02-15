package com.ebensz.games.logic.settle;

import com.ebensz.games.logic.player.Player;
import com.ebensz.games.model.Dir;

import java.util.Map;

/**
 * User: jason
 * Date: 12-1-13
 * Time: 上午11:48
 */
public class SuperSettleTool extends NormalSettleTool {


    public SuperSettleTool(Map<Dir, Player> playerMap) {
        super(playerMap);
        multiplier = 1;
    }

    @Override
    public Result settle(Input input) {
        Result result = super.settle(input);

        for (Dir dir : result.winScores.keySet()) {

            int score = result.winScores.get(dir);

            result.winScores.put(dir, score * multiplier);

        }

        multiplier *= 2;

        return result;
    }

    private int multiplier;
}
