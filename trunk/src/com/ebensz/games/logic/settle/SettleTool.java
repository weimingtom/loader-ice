package com.ebensz.games.logic.settle;

import com.ebensz.games.model.DaoGenFan;
import com.ebensz.games.model.Dir;
import com.ebensz.games.model.Role.Role;

import java.util.Map;

/**
 * User: Mike.Hu
 * Date: 11-12-15
 * Time: 上午10:02
 */
public interface SettleTool {

    public class Input {

        public boolean loaderWin;
        public Dir loaderDir;
        public int baseScore;
        public int multiple;
        public int bombNum; //包括火箭和四个的炸弹
        public Map<Dir, DaoGenFan> daoGenFanMap;
        public boolean isSpring;
    }

    public class Result {

        public Map<Dir, Integer> winScores;
        public Map<Dir, Boolean> upgradeFlag;

        public Map<Dir, Role> roleMap;

    }

    Result settle(Input input);

    void applyResult(Result result);
}
