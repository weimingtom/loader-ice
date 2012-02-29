package com.ebensz.games.logic.round;

import android.util.Log;
import com.ebensz.games.logic.SavedGame;
import com.ebensz.games.logic.player.Player;
import com.ebensz.games.logic.story.Game;
import com.ebensz.games.model.Dir;
import com.ebensz.games.model.History;
import com.ebensz.games.model.hand.ColoredHand;
import com.ebensz.games.model.poker.ColoredPoker;
import com.ebensz.games.model.poker.PokersInfo;
import com.ebensz.games.scenes.GameScene;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: Mike.Hu
 * Date: 11-10-27
 * Time: 下午3:56
 */
public class Round {
    private static final Player NO_BODY = null;
    private static final String TAG = Round.class.getSimpleName();

    public Round(Game game) {
        this.game = game;
        history = new History();
    }

    public void bind(Map<Dir, Player> playerMap, PokersInfo pokersInfo, Dir loaderDir) {
        this.playerMap = playerMap;
        this.pokersInfo = pokersInfo;
        this.loaderDir = loaderDir;
    }

    public void start(Dir firstChuPaiDir) {

        reset(firstChuPaiDir);

        while (!stopFlag) {

            saveSnapshot();

            ColoredHand chuPai = chuPai(focusDir);

            if (stopFlag) break;

            if (noShouPaiLeft) {
                winDir = focusDir;
                break;
            }

            Dir nextChuPaiDir = jiePai(focusDir, chuPai);

            if (stopFlag) break;

            if (noShouPaiLeft) {
                winDir = nextChuPaiDir;
                break;
            }

            focusDir = nextChuPaiDir;
        }
    }

    private void reset(Dir firstChuPaiDir) {
        noShouPaiLeft = false;
        focusDir = firstChuPaiDir;
        history.reset();
    }

    public void stop() {
        stopFlag = true;
    }

    /**
     * 是否正常结束
     *
     * @return true   一局未正常结束  false 正常结束
     */
    public boolean isOver() {
        return !stopFlag && noShouPaiLeft;
    }

    private ColoredHand chuPai(Dir dir) {
        Player player = playerMap.get(dir);
        ColoredHand chuPai = player.chuPai(pokersInfo, dir, loaderDir);

        if (stopFlag) {
            Log.w(TAG, "chu pai stoped !" + chuPai);
            return null;
        }

        List<ColoredPoker> coloredPokers = chuPai.getColoredPokers();

        List<ColoredPoker> shouPai = pokersInfo.getShouPaiMap().get(dir);
        shouPai.removeAll(coloredPokers);

        history.addEvent(
                new History.Event(
                        dir,
                        chuPai
                )
        );

        if (!remainShouPai(dir)) {
            noShouPaiLeft = true;
        }

        showChuPai(dir, chuPai, noShouPaiLeft);

        return chuPai;
    }

    private Dir jiePai(Dir chuPaiDir, ColoredHand chuPai) {
        List<Dir> others = otherDir(chuPaiDir);

        for (Dir dir : others) {

            Player player = playerMap.get(dir);
            ColoredHand jiePai = player.jiePai(chuPaiDir, chuPai, pokersInfo, dir, loaderDir);

            if (stopFlag) {
                Log.w(TAG, "jie pai stoped !" + jiePai);
                return null;
            }


            history.addEvent(
                    new History.Event(
                            dir,
                            jiePai,
                            chuPaiDir
                    )
            );

            if (jiePai != ColoredHand.BU_YAO) {

                List<ColoredPoker> shouPai = pokersInfo.getShouPaiMap().get(dir);
                shouPai.removeAll(jiePai.getColoredPokers());

                boolean noRemain = !remainShouPai(dir);

                showJiePai(dir, jiePai, chuPaiDir, noRemain);

                if (noRemain) {
                    noShouPaiLeft = true;
                    return dir;
                }


                return jiePai(dir, jiePai);
            }
            else {
                showBuYao(dir, chuPaiDir); //不要
            }

        }

        return chuPaiDir;
    }


    private void showBuYao(Dir jiePaiDir, Dir chuPaiDir) {
        GameScene scene = game.getScene();
        scene.showBuYao(jiePaiDir, chuPaiDir);
        //scene.getPokerTiles(jiePaiDir).hideLastChuPai();
    }

    private void showChuPai(Dir chuPaiDir, ColoredHand chuPai, boolean noShouPaiLeft) {
        game.getScene().showChuPai(chuPaiDir, chuPai, noShouPaiLeft);
    }

    private void showJiePai(Dir jiePaiPlayer, ColoredHand jiePai, Dir chuPaiPlayer, boolean noShouPaiLeft) {
        game.getScene().showJiePai(jiePaiPlayer, jiePai, chuPaiPlayer, noShouPaiLeft);
    }

    private List<Dir> otherDir(Dir dir) {
        List<Dir> others = new ArrayList<Dir>(2);

        switch (dir) {
            case Left:
                others.add(Dir.Outside);
                others.add(Dir.Right);
                break;
            case Right:
                others.add(Dir.Left);
                others.add(Dir.Outside);
                break;
            case Outside:
                others.add(Dir.Right);
                others.add(Dir.Left);
                break;
        }

        return others;
    }

    private boolean remainShouPai(Dir dir) {
        List<ColoredPoker> shouPai = pokersInfo.getShouPaiMap().get(dir);
        return shouPai.size() > 0;
    }

    private void saveSnapshot() {

        SavedGame stateInfo = SavedGame.generate();

        if (snapshot == null) {
            snapshot = new ByteArrayOutputStream();
        }
        else {
            snapshot.reset();
        }

        // StorageUtil.writeObject(stateInfo, snapshot);
    }

    public Dir getWinDir() {
        return winDir;
    }


    public History getHistory() {
        return history;
    }

    /**
     * 某个玩家的牌出完了
     */
    private boolean noShouPaiLeft;

    private Game game;
    private Dir winDir;
    private Dir focusDir;
    private Dir loaderDir;
    private History history;
    private boolean stopFlag;
    private PokersInfo pokersInfo;
    private Map<Dir, Player> playerMap;
    private ByteArrayOutputStream snapshot;
}
