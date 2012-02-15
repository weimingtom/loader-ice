package com.ebensz.games.ai;

import com.ebensz.games.model.DaoGenFan;
import com.ebensz.games.model.Dir;
import com.ebensz.games.model.RobLoaderScore;
import com.ebensz.games.model.hand.Hand;
import com.ebensz.games.model.poker.PokersInfo;

import java.util.Map;

/**
 * User: Mike.Hu
 * Date: 11-11-28
 * Time: 下午3:57
 */
public interface Ai {

    RobLoaderScore robLoader(Dir self, RobLoaderScore lastHighestScore, PokersInfo pokersInfo);

    boolean dao(Dir loaderDir, Dir self, PokersInfo pokersInfo);

    boolean gen(Dir loaderDir, Dir self, PokersInfo pokersInfo);

    boolean fan(Map<Dir, DaoGenFan> daoGenFanMap, Dir self, PokersInfo pokersInfo);

    Hand chuPai(PokersInfo pokersInfo, Dir self, Dir loaderDir);

    Hand jiePai(Dir chuPaiDir, Hand chuPai, PokersInfo pokersInfo, Dir self, Dir loaderDir);

    public class Helper {

        public static boolean isLoaderShangJia(Dir self, Dir loaderDir) {
            return self.shangJia() == loaderDir;
        }

        public static boolean isLoaderXiaJia(Dir self, Dir loaderDir) {
            return loaderDir.xiaJia() == self;
        }

    }
}
