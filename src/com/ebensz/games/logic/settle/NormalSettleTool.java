package com.ebensz.games.logic.settle;

import com.ebensz.games.logic.player.Player;
import com.ebensz.games.model.DaoGenFan;
import com.ebensz.games.model.Dir;
import com.ebensz.games.model.Role.Role;
import com.ebensz.games.model.Role.RoleCenter;
import ice.res.Res;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Mike.Hu
 * Date: 11-12-15
 * Time: 上午10:15
 */
public class NormalSettleTool implements SettleTool {
    public static final int BASE_SCORE = 10;

    public NormalSettleTool(Map<Dir, Player> playerMap) {
        this.playerMap = playerMap;
    }


    @Override
    public Result settle(Input input) {

        Result result = new Result();

        int springCount = input.isSpring ? 1 : 0;
        int baseWinScore = input.baseScore * input.multiple * (int) Math.pow(2, (input.bombNum + springCount));


        int shangJiaWinScore = baseWinScore;
        Dir shangJiaDir = input.loaderDir.shangJia();
        if (checkDaoGen(shangJiaDir, input.daoGenFanMap))
            shangJiaWinScore *= 2;
        if (input.daoGenFanMap.get(input.loaderDir) == DaoGenFan.Fan)
            shangJiaWinScore *= 2;
        if (input.loaderWin)
            shangJiaWinScore *= -1;

        int xiaJiaWinScore = baseWinScore;
        Dir xiaJiaDir = input.loaderDir.xiaJia();
        if (checkDaoGen(xiaJiaDir, input.daoGenFanMap))
            xiaJiaWinScore *= 2;
        if (input.daoGenFanMap.get(input.loaderDir) == DaoGenFan.Fan)
            xiaJiaWinScore *= 2;
        if (input.loaderWin)
            xiaJiaWinScore *= -1;

        int loaderWinScore = -(shangJiaWinScore + xiaJiaWinScore);

        result.winScores = new HashMap<Dir, Integer>(3);
        result.winScores.put(shangJiaDir, shangJiaWinScore);
        result.winScores.put(xiaJiaDir, xiaJiaWinScore);
        result.winScores.put(input.loaderDir, loaderWinScore);

        result.roleMap = new HashMap<Dir, Role>(3);
        for (Dir dir : Dir.values()) {
            result.roleMap.put(dir, playerMap.get(dir).getRole());
        }

        return result;
    }

    @Override
    public void applyResult(Result result) {
        RoleCenter roleCenter = RoleCenter.getInstance(Res.getContext());

        for (Dir dir : playerMap.keySet()) {
            Role role = playerMap.get(dir).getRole();
            int currentWealth = role.getWealth();

            role.setWealth(currentWealth + result.winScores.get(dir));

            roleCenter.update(role);
        }

    }

    private boolean checkDaoGen(Dir dir, Map<Dir, DaoGenFan> daoGenFanMap) {

        DaoGenFan daoGen = daoGenFanMap.get(dir);
        if (daoGen == DaoGenFan.Dao || daoGen == DaoGenFan.Gen)
            return true;

        return false;
    }

    private Map<Dir, Player> playerMap;
}
