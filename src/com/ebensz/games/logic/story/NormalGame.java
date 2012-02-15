package com.ebensz.games.logic.story;

import com.ebensz.games.Rule.DefaultRule;
import com.ebensz.games.Rule.Rule;
import com.ebensz.games.ai.SimpleAi;
import com.ebensz.games.logic.FaPaiUnit;
import com.ebensz.games.logic.RuntimeData;
import com.ebensz.games.logic.interactive.Message;
import com.ebensz.games.logic.player.AIDecisionMaker;
import com.ebensz.games.logic.player.DecisionMaker;
import com.ebensz.games.logic.player.Interactive;
import com.ebensz.games.logic.player.Player;
import com.ebensz.games.logic.settle.NormalSettleTool;
import com.ebensz.games.model.Dir;
import com.ebensz.games.model.History;
import com.ebensz.games.model.RobLoaderScore;
import com.ebensz.games.model.Role.NpcChooser;
import com.ebensz.games.model.Role.Role;
import com.ebensz.games.model.Role.RoleCenter;
import com.ebensz.games.model.poker.PokersInfo;
import com.ebensz.games.scenes.GameScene;
import com.ebensz.games.scenes.SimpleGameScene;
import ice.node.widget.Button;
import ice.res.Res;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.ebensz.games.logic.interactive.Message.SELECT_CONTINUE_GAME;
import static com.ebensz.games.model.Dir.*;
import static com.ebensz.games.logic.story.Helper.*;

public class NormalGame extends Game {

    @Override
    protected void whenHandled(Message msg) {
        if (msg != null && msg.what == SELECT_CONTINUE_GAME) {
            ((SimpleGameScene) scene).getSelectContinueGameBtn().setRemovable(true);
        }
    }

    @Override
    protected GameScene createScene() {
        return new SimpleGameScene(this);
    }

    @Override
    protected Dir ensureLoaderDir() {

        Dir startDir = ensureRobLoaderStartDir(); //确定叫分的开始位置

        Map<Dir, Player> playerMap = runtimeData.getPlayerMap();
        PokersInfo pokersInfo = runtimeData.getPokersInfo();

        return robLoader(startDir, playerMap, pokersInfo);// 叫分
    }

    @Override
    protected void handleSelectContinueGame(final Message msg) {
        ((SimpleGameScene) scene).showSelectContinueGame();

        Button continueBtn = ((SimpleGameScene) scene).getSelectContinueGameBtn();

        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(Button btn) {

                msg.obj = Boolean.valueOf(true);

                Player human = runtimeData.getPlayerMap().get(Dir.Outside);
                Interactive interactive = (Interactive) human.getDecisionMaker();

                interactive.onFeedback(NormalGame.this, msg);
            }
        };

        continueBtn.setOnClickListener(onClickListener);
    }

    @Override
    protected RuntimeData prepareRuntime(RuntimeData oldData, History history) {
        RuntimeData newData = oldData;
        if (newData == null) {
            newData = new RuntimeData();
        }

        if (newData.getRule() == null) {
            newData.setRule(new DefaultRule());
        }

        Map<Dir, Player> playerMap = newData.getPlayerMap();

        if (playerMap != null) {
            validatePlays(playerMap);
        }
        else {
            playerMap = new HashMap<Dir, Player>(3);

            fillPlays(playerMap);

            newData.setPlayerMap(playerMap);
        }

        bindInteractives(newData);

        Dir[] allDir = Dir.values();
        int index = new Random().nextInt(1000) % allDir.length;
        Dir faPaiStartDir = allDir[index];
        newData.setFaPaiStartDir(faPaiStartDir);

        PokersInfo pokersInfo = new PokersInfo(FaPaiUnit.getDebugPoker(faPaiStartDir), history);
        newData.setPokersInfo(pokersInfo);

        if (newData.getSettleTool() == null) {
            newData.setSettleTool(new NormalSettleTool(playerMap));
        }

        return newData;
    }

    /**
     * 叫分.
     *
     * @param startDir   开始叫分一方
     * @param playerMap  所有玩家
     * @param pokersInfo 牌面信息
     * @return 地主（如果都不要则返回null）
     */
    private Dir robLoader(Dir startDir, Map<Dir, Player> playerMap, PokersInfo pokersInfo) {
        Dir highestScoreDir = null;
        List<Dir> order = makeDirOrder(startDir);
        RobLoaderScore highestScore = RobLoaderScore.Pass;

        for (Dir dir : order) {
            RobLoaderScore score = playerMap.get(dir).robLoader(highestScore, dir, pokersInfo);

            if (stopFlag) {
                return null;
            }

            if (score.isHigherThan(highestScore)) {
                highestScore = score;
                highestScoreDir = dir;
            }

            scene.showRobLoader(dir, score);

            if (score == RobLoaderScore.Three) break;
        }

        runtimeData.setLoaderDir(highestScoreDir);
        runtimeData.setLoaderJiaoFen(highestScore);

        return highestScoreDir;
    }

    private void bindInteractives(RuntimeData newData) {
        Rule rule = newData.getRule();

        DecisionMaker interactive = new Interactive(this, rule);
        DecisionMaker leftDecisionMaker = new AIDecisionMaker(new SimpleAi(), this, rule);
        DecisionMaker rightDecisionMaker = new AIDecisionMaker(new SimpleAi(), this, rule);

        registerHandler(interactive);
        registerHandler(leftDecisionMaker);
        registerHandler(rightDecisionMaker);

        Map<Dir, Player> players = newData.getPlayerMap();
        players.get(Dir.Outside).setDecisionMaker(interactive);
        players.get(Dir.Left).setDecisionMaker(leftDecisionMaker);
        players.get(Dir.Right).setDecisionMaker(rightDecisionMaker);
    }

    private void validatePlays(Map<Dir, Player> playerMap) {
        Map<Dir, Role> loseNpcs = new HashMap<Dir, Role>(2);

        for (Dir dir : playerMap.keySet()) {
            if (dir != Dir.Outside) {
                Role npc = playerMap.get(dir).getRole();
                if (npc.getWealth() <= 0) {
                    loseNpcs.put(dir, npc);
                }
            }
        }

        if (loseNpcs.size() > 0) {
            RoleCenter roleCenter = RoleCenter.getInstance(Res.getContext());

            Map<Dir, Role> newNpcs = NpcChooser.replaceNpcs(loseNpcs, roleCenter);

            for (Dir dir : newNpcs.keySet()) {
                playerMap.get(dir).setRole(newNpcs.get(dir));
            }
        }


    }

    private void fillPlays(Map<Dir, Player> playerMap) {
        RoleCenter roleCenter = RoleCenter.getInstance(Res.getContext());

        Role human = roleCenter.getHuman();
        List<Role> allNpc = roleCenter.getAllNpc();

        Map<Dir, Role> otherTwo = NpcChooser.choose(human, allNpc);

        Player humanPlayer = new Player(human);
        Player leftPlayer = new Player(otherTwo.get(Left));
        Player rightPlayer = new Player(otherTwo.get(Right));

        playerMap.put(Outside, humanPlayer);
        playerMap.put(Left, leftPlayer);
        playerMap.put(Right, rightPlayer);
    }
}
