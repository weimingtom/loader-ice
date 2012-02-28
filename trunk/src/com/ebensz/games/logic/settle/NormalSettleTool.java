package com.ebensz.games.logic.settle;

import com.ebensz.games.logic.player.Player;
import com.ebensz.games.model.DaoGenFan;
import com.ebensz.games.model.Dir;
import com.ebensz.games.model.Role.Role;
import com.ebensz.games.model.Role.RoleCenter;
import com.ebensz.games.model.values.LevelDefine;
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
    public static final int WIN_EXP = 300;
    public static final int FAIL_EXP = 100;

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

        Role loaderRole = playerMap.get(input.loaderDir).getRole();
        Role shangJiaRole = playerMap.get(shangJiaDir).getRole();
        Role xiaJiaRole = playerMap.get(xiaJiaDir).getRole();

        int loaderExp = loaderRole.getExp();
        int shangJiaExp = shangJiaRole.getExp();
        int xiaJiaExp = xiaJiaRole.getExp();
        int loaderGrade = LevelDefine.getLevel(loaderExp);
        int shangJiaGrade = LevelDefine.getLevel(shangJiaExp);
        int xiaJiaGrade = LevelDefine.getLevel(xiaJiaExp);

        boolean loaderWinFlag = loaderWinScore > 0;

        loaderRole.setExp(loaderExp + (loaderWinFlag ? WIN_EXP : FAIL_EXP));
        shangJiaRole.setExp(shangJiaExp + (loaderWinFlag ? FAIL_EXP : WIN_EXP));
        xiaJiaRole.setExp(xiaJiaExp + (loaderWinFlag ? FAIL_EXP : WIN_EXP));
        int settleLoaderGrade = LevelDefine.getLevel(loaderExp);
        int settleShangJiaGrade = LevelDefine.getLevel(shangJiaExp);
        int settleXiaJiaGrade = LevelDefine.getLevel(xiaJiaExp);

        result.upgradeFlag = new HashMap<Dir, Boolean>(3);
        result.upgradeFlag.put(input.loaderDir, loaderGrade == settleLoaderGrade);
        result.upgradeFlag.put(shangJiaDir, shangJiaGrade == settleShangJiaGrade);
        result.upgradeFlag.put(xiaJiaDir, xiaJiaGrade == settleXiaJiaGrade);

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
