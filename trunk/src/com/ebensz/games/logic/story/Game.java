package com.ebensz.games.logic.story;

import android.util.Log;
import com.ebensz.games.Rule.Rule;
import com.ebensz.games.logic.RuntimeData;
import com.ebensz.games.logic.SavedGame;
import com.ebensz.games.logic.interactive.Feedback;
import com.ebensz.games.logic.interactive.Message;
import com.ebensz.games.logic.player.Interactive;
import com.ebensz.games.logic.player.Player;
import com.ebensz.games.logic.round.Round;
import com.ebensz.games.logic.settle.SettleTool;
import com.ebensz.games.model.*;
import com.ebensz.games.model.Role.Role;
import com.ebensz.games.model.hand.ColoredHand;
import com.ebensz.games.model.hand.Hand;
import com.ebensz.games.model.poker.ColoredPoker;
import com.ebensz.games.model.poker.Poker;
import com.ebensz.games.model.poker.PokerUtils;
import com.ebensz.games.model.poker.PokersInfo;
import com.ebensz.games.scenes.GameScene;
import com.ebensz.games.ui.widget.OutsidePokerTiles;
import com.ebensz.games.utils.SleepUtils;
import ice.node.Overlay;
import ice.node.widget.ButtonOverlay;

import java.util.*;

import static com.ebensz.games.logic.interactive.Message.*;
import static com.ebensz.games.logic.story.Helper.*;
import static com.ebensz.games.model.Dir.Outside;

public abstract class Game implements Runnable, Feedback {

    private static final String TAG = Game.class.getSimpleName();

    public GameScene getScene() {
        return scene;
    }

    public void onCreate() {
        Log.i(TAG, "onCreate");
        handlers = new ArrayList<Feedback.Handler>(3);
        scene = createScene();

        suggestHandler = new SuggestHandler();
        autoCompleteHandler = new AutoCompleteHandler();
    }

    public void onResume(SavedGame savedGame) {
        Log.i(TAG, "onResume");
        this.savedGame = savedGame;

        handlers.clear();
        round = new Round(this);

        stopFlag = false;

        gameThread = new Thread(this);
        gameThread.start();
    }

    public void onPause() {
        Log.i(TAG, "onPause");

        stopFlag = true;

        round.stop();

        Player humanPlayer = runtimeData.getPlayerMap().get(Outside);
        Interactive interactive = (Interactive) humanPlayer.getDecisionMaker();

        interactive.release();

        gameThread.interrupt();
    }

    @Override
    public void run() {

        while (!stopFlag) {

            reset();

            boolean recoverMode = savedGame != null;

            runtimeData = recoverMode ? recoverRuntime(savedGame) : prepareRuntime(runtimeData, round.getHistory());

            Map<Dir, Player> playerMap = runtimeData.getPlayerMap();

            initScene(playerMap);

            PokersInfo pokersInfo = runtimeData.getPokersInfo();

            if (!recoverMode) {

                Dir faPaiStartDir = runtimeData.getFaPaiStartDir();

                faPai(pokersInfo, faPaiStartDir); //发牌


                Dir loaderDir = ensureLoaderDir();

                if (loaderDir == null) { //叫分失败
                    robLoaderFail();
                    continue;
                }

                runtimeData.setLoaderDir(loaderDir);

                faPaiLeftThree(loaderDir, pokersInfo);//剩下3张牌发给地主

                daoGenFan(loaderDir, playerMap, pokersInfo); //倒跟反
            }


            Dir loaderDir = runtimeData.getLoaderDir();

            // AppContext.get().setRuntime(runtimeData);

            round.bind(playerMap, pokersInfo, loaderDir);

            round.start(loaderDir); //一局开始

            settle();//结算

            waitForContinue();
        }

        Log.i(TAG, "exit");
    }

    protected abstract Dir ensureLoaderDir();

    protected void initScene(Map<Dir, Player> playerMap) {
        scene.reset();

        Map<Dir, Role> roleMap = new HashMap<Dir, Role>(3);
        for (Dir dir : playerMap.keySet()) {
            roleMap.put(dir, playerMap.get(dir).getRole());
        }
        scene.showPlayers(roleMap);
    }

    @Override
    public void registerHandler(Handler handler) {
        if (!handlers.contains(handler))
            handlers.add(handler);
    }

    @Override
    public final void onHandled(Message msg) {
        if (msg == null) return;

        switch (msg.what) {
            case SELECT_ROB_LOADER:
                scene.hideRobLoaderButtons();

                msg.what = NOTHING;
                scene.update(msg);
                break;

            case SELECT_DAO:
            case SELECT_GEN:
            case SELECT_FAN:
                scene.hideD_G_FBttons(); //注意！ 倒跟反的消息不要影响scene的状态
                break;

            case SELECT_JIE_PAI:
                onJiePaiHandled(msg);
            case SELECT_CHU_PAI:
                Overlay chuPaiBtn = scene.getChuPaiBtn();
                if (chuPaiBtn != null)
                    chuPaiBtn.setVisible(false);

                Overlay passBtn = scene.getChuPaiPassBtn();
                if (passBtn != null)
                    passBtn.setVisible(false);

                msg.what = NOTHING;
                scene.update(msg);
                autoCompleteHandler.reset();
                break;

            default:
                whenHandled(msg);

                msg.what = NOTHING;
                scene.update(msg);
                break;
        }

    }

    @Override
    public void onMessage(Message msg) {
        this.msg = msg;
        scene.update(msg);

        switch (msg.what) {
            case SELECT_ROB_LOADER:
                handleRobLoader(msg);
                break;

            case SELECT_DAO:
            case SELECT_GEN:
            case SELECT_FAN:
                handleD_G_F(msg);
                break;

            case SELECT_CHU_PAI:
            case SELECT_JIE_PAI:
                autoCompleteHandler.prepare();
                handleSelectPokers(msg.what == SELECT_JIE_PAI);
                break;

            case SELECT_CONTINUE_GAME:
                handleSelectContinueGame(msg);
                break;

        }
    }

    protected abstract void handleSelectContinueGame(Message msg);

    public void onPokersSelected() {
        Rule rule = runtimeData.getRule();

        OutsidePokerTiles outsidePokerTiles = scene.getPokersOverlay().getOutsidePokerTiles();

        List<ColoredPoker> selectPokers = outsidePokerTiles.getSelectedPokers();

        if (selectPokers.size() == 0) {
            outsidePokerTiles.setValidatedHand(null);
            return;
        }

        if (isSame(outsidePokerTiles.getValidatedHand(), selectPokers)) return;

        List<ColoredPoker> others = autoCompleteHandler.tryComplete(selectPokers);

        if (others != null && others.size() != 0) {
            outsidePokerTiles.showSelectedPokers(others);
            selectPokers.addAll(others);
        }


        List<Poker> selected = PokerUtils.parseToPokers(selectPokers);

        Hand selectedHand = null;

        if (msg.what == SELECT_CHU_PAI) {
            selectedHand = rule.indentify(selected);
            System.out.println("chu pai indentify selectedHand = " + selectedHand);
        }
        else if (msg.what == SELECT_JIE_PAI) {
            selectedHand = rule.indentify(selected);
            System.out.println("jie pai indentify selectedHand = " + selectedHand);

            JiePaiEvent jiePaiEvent = (JiePaiEvent) msg.obj;
            if (!rule.validateJiePai(jiePaiEvent.chuPai.getHand(), selectedHand))
                selectedHand = null;
        }


        boolean validated = selectedHand != null;

        if (validated) {
            outsidePokerTiles.setValidatedHand(
                    new ColoredHand(
                            selectedHand,
                            new ArrayList<ColoredPoker>(selectPokers)
                    )
            );
        }
        else {
            outsidePokerTiles.setValidatedHand(null);
        }

        ButtonOverlay chuPaiButton = scene.getChuPaiBtn();
        chuPaiButton.setDisabled(!validated);
    }

    private boolean isSame(ColoredHand lastValidated, List<ColoredPoker> selectPokers) {

        if (lastValidated == null) return false;

        List<ColoredPoker> lastSelected = lastValidated.getColoredPokers();

        if (selectPokers.size() != lastSelected.size()) return false;

        Collections.sort(selectPokers);
        Collections.sort(lastSelected);

        for (int i = 0; i < selectPokers.size(); i++) {
            if (!selectPokers.get(i).equals(lastSelected.get(i)))
                return false;
        }

        return true;
    }

    protected void noticeMsg(Message msg) {
        for (Handler handler : handlers) {
            handler.onFeedback(this, msg);
        }
    }

    protected abstract RuntimeData prepareRuntime(RuntimeData oldData, History history);

    protected abstract GameScene createScene();

    protected void whenHandled(Message msg) {

    }

    private void handleRobLoader(final Message msg) {
        scene.showRobLoaderButtons();

        final ButtonOverlay finalPassBtn = scene.getPassBtn();
        final ButtonOverlay finalOneBtn = scene.getOneBtn();
        final ButtonOverlay finalTwoBtn = scene.getTwoBtn();
        final ButtonOverlay finalThreeBtn = scene.getThreeBtn();

        ButtonOverlay.OnClickListener onClickListener = new ButtonOverlay.OnClickListener() {
            @Override
            public void onClick(ButtonOverlay btn) {
                if (btn == finalPassBtn) {
                    msg.obj = RobLoaderScore.Pass;
                }
                else if (btn == finalOneBtn) {
                    msg.obj = RobLoaderScore.One;
                }
                else if (btn == finalTwoBtn) {
                    msg.obj = RobLoaderScore.Two;
                }
                else if (btn == finalThreeBtn) {
                    msg.obj = RobLoaderScore.Three;
                }

                noticeMsg(msg);
            }
        };

        finalPassBtn.setOnClickListener(onClickListener);
        finalOneBtn.setOnClickListener(onClickListener);
        finalTwoBtn.setOnClickListener(onClickListener);
        finalThreeBtn.setOnClickListener(onClickListener);
    }

    private void handleSelectPokers(boolean jiePai) {

        //如果只剩一手牌自动打出去
        if (handleOnlyOneHand(jiePai)) return;

        scene.showChuPaiBtns(jiePai);

        ButtonOverlay chuPaiButton = scene.getChuPaiBtn();
        ButtonOverlay passButton = scene.getChuPaiPassBtn();

        if (chuPaiButton.getOnClickListener() == null)
            bindChuPaiBtnAction(chuPaiButton, passButton);

        if (jiePai) {
            ButtonOverlay suggestButton = scene.getSuggestBtn();

            if (suggestButton.getOnClickListener() == null)
                suggestButton.setOnClickListener(suggestHandler);
        }


    }

    private boolean handleOnlyOneHand(boolean jiePai) {

        Rule rule = runtimeData.getRule();
        final PokersInfo pokersInfo = runtimeData.getPokersInfo();
        List<Poker> humanShouPai = pokersInfo.getShouPai(Dir.Outside);

        final Hand hand = rule.indentify(humanShouPai);

        if (hand == null) return false;

        if (jiePai) {
            Hand chuPai = ((JiePaiEvent) msg.obj).chuPai.getHand();
            if (!rule.validateJiePai(chuPai, hand)) return false;

            new Thread() {
                @Override
                public void run() {

                    SleepUtils.sleep(600);//不要马上启动，否则还没开始要出牌

                    List<ColoredPoker> coloredPokers = pokersInfo.getShouPaiMap().get(Dir.Outside);

                    JiePaiEvent jiePaiEvent = (JiePaiEvent) msg.obj;
                    jiePaiEvent.jiePai = new ColoredHand(
                            hand,
                            new ArrayList<ColoredPoker>(coloredPokers)
                    );

                    noticeMsg(msg);
                }
            }.start();

            return true;
        }


        new Thread() {
            @Override
            public void run() {

                SleepUtils.sleep(600);

                List<ColoredPoker> coloredPokers = pokersInfo.getShouPaiMap().get(Dir.Outside);

                ChuPaiEvent chuPaiEvent = (ChuPaiEvent) msg.obj;
                chuPaiEvent.chuPai = new ColoredHand(
                        hand,
                        new ArrayList<ColoredPoker>(coloredPokers)
                );

                noticeMsg(msg);
            }
        }.start();

        return true;
    }

    private void bindChuPaiBtnAction(final ButtonOverlay chuPaiButton, ButtonOverlay passButton) {
        ButtonOverlay.OnClickListener onClickListener = new ButtonOverlay.OnClickListener() {
            @Override
            public void onClick(ButtonOverlay btn) {
                if (msg == null) return;

                if (msg.what != SELECT_CHU_PAI && msg.what != SELECT_JIE_PAI) return;

                OutsidePokerTiles outsidePokerTiles = scene.getPokersOverlay().getOutsidePokerTiles();
                List<ColoredPoker> selectedPokers = outsidePokerTiles.getSelectedPokers();

                if (btn == chuPaiButton) {

                    tryPostChuOrJie();
                }
                else { //pass button

                    if (msg.what == SELECT_JIE_PAI) {

                        if (selectedPokers != null && selectedPokers.size() > 0)
                            outsidePokerTiles.tidyShouPai(50);

                        JiePaiEvent jiePaiEvent = (JiePaiEvent) msg.obj;
                        jiePaiEvent.jiePai = ColoredHand.BU_YAO;
                        noticeMsg(msg);
                    }

                }


                if (selectedPokers != null && selectedPokers.size() > 0) {
                    selectedPokers.clear();
                    outsidePokerTiles.setValidatedHand(null);
                }

            }
        };

        chuPaiButton.setOnClickListener(onClickListener);
        passButton.setOnClickListener(onClickListener);
    }

    public void tryPostChuOrJie() {

        OutsidePokerTiles outsidePokerTiles = scene.getPokersOverlay().getOutsidePokerTiles();

        ColoredHand validatedHand = outsidePokerTiles.getValidatedHand();

        if (validatedHand == null) return;

        if (msg.what == SELECT_CHU_PAI) {
            ChuPaiEvent chuPaiEvent = (ChuPaiEvent) msg.obj;
            chuPaiEvent.chuPai = validatedHand;
        }
        else if (msg.what == SELECT_JIE_PAI) {
            JiePaiEvent jiePaiEvent = (JiePaiEvent) msg.obj;
            jiePaiEvent.jiePai = validatedHand;
        }

        noticeMsg(msg);
    }

    private void handleD_G_F(final Message msg) {
        scene.showD_G_F(msg.what);

        final ButtonOverlay d_g_fBtn = scene.getD_g_fBtn();
        final ButtonOverlay passBtn = scene.getD_g_fPassBtn();
        ButtonOverlay.OnClickListener onClickListener = new ButtonOverlay.OnClickListener() {
            @Override
            public void onClick(ButtonOverlay btn) {
                msg.obj = (btn == d_g_fBtn);
                noticeMsg(msg);
            }
        };

        d_g_fBtn.setOnClickListener(onClickListener);
        passBtn.setOnClickListener(onClickListener);
    }

    private void faPaiLeftThree(Dir loaderDir, PokersInfo pokersInfo) {
        List<ColoredPoker> leftThree = pokersInfo.getLeftThree();
        List<ColoredPoker> loaderShouPai = pokersInfo.getShouPaiMap().get(loaderDir);
        loaderShouPai.addAll(leftThree);
        scene.showFaPaiLeftThree(loaderDir, leftThree);
    }

    private void onJiePaiHandled(Message msg) {
        JiePaiEvent jiePaiEvent = (JiePaiEvent) msg.obj;

        if (jiePaiEvent.jiePaiDir == runtimeData.getLoaderDir().shangJia()) { //已出完一轮牌，如果还没有倒跟反就视为不倒跟反

            if (d_g_f_handler != null && d_g_f_handler.isWaitingReply()) {
                d_g_f_handler.stopHandle();
            }
        }

        if (jiePaiEvent.jiePaiDir == jiePaiEvent.chuPaiDir.shangJia() && jiePaiEvent.jiePai == ColoredHand.BU_YAO) {
            for (Dir dir : Dir.values()) {
                scene.getPokersOverlay().getDirPoker(dir).hideLastChuPai();
            }

            SleepUtils.sleep(500);
        }

        if (jiePaiEvent.jiePaiDir == Dir.Outside) {
            scene.getSuggestBtn().setVisible(false);
            suggestHandler.clearSuggestion();
        }
    }

    private void reset() {

        if (runtimeData != null)
            runtimeData.reset();

    }

    private void daoGenFan(Dir loaderDir, Map<Dir, Player> playerMap, PokersInfo pokersInfo) {
        if (stopFlag) return;

        d_g_f_handler = new D_G_F_Handler(loaderDir, playerMap, pokersInfo);
        d_g_f_handler.start();
    }

    private void robLoaderFail() {
        scene.showRobLoaderFail();
    }


    private void faPai(PokersInfo pokersInfo, Dir startDir) {
        List<ColoredPoker> allPokers = pokersInfo.getAllPokers();

        Map<Dir, List<ColoredPoker>> shouPaiMap = pokersInfo.getShouPaiMap();
        List<Dir> order = makeDirOrder(startDir);

        for (int i = 0; i < 17; i++) {
            for (Dir dir : order) {
                List<ColoredPoker> shouPai = shouPaiMap.get(dir);
                shouPai.add(allPokers.remove(0));
            }
        }
        pokersInfo.setLeftThree(allPokers);

        System.out.println("shouPaiMap = " + shouPaiMap);

        scene.showFaPai(order, shouPaiMap, allPokers);
    }

    private RuntimeData recoverRuntime(SavedGame savedGame) {
        return null;
    }

    private void settle() {
        if (!round.isOver())
            return;

        SettleTool settleTool = runtimeData.getSettleTool();

        SettleTool.Input input = new SettleTool.Input();

        fillInput(input, runtimeData, round);

        SettleTool.Result result = settleTool.settle(input);

        settleTool.applyResult(result);

        scene.showSettle(result);
    }

    private void waitForContinue() {
        if (!round.isOver())
            return;

        Player human = runtimeData.getPlayerMap().get(Dir.Outside);


        Interactive interactive = (Interactive) human.getDecisionMaker();

        interactive.askForContinueGame();
    }

    protected GameScene scene;

    private Message msg;
    private Round round;
    private Thread gameThread;
    protected boolean stopFlag;
    private SavedGame savedGame;
    private List<Handler> handlers;
    protected RuntimeData runtimeData;
    private D_G_F_Handler d_g_f_handler;
    private SuggestHandler suggestHandler;
    private AutoCompleteHandler autoCompleteHandler;

    private class D_G_F_Handler extends Thread {
        private final String TAG = D_G_F_Handler.class.getSimpleName();

        public D_G_F_Handler(Dir loaderDir, Map<Dir, Player> playerMap, PokersInfo pokersInfo) {
            this.loaderDir = loaderDir;
            this.playerMap = playerMap;
            this.pokersInfo = pokersInfo;
            waitingReply = true;
        }

        public void stopHandle() {
            Log.i(TAG, "stopHandle");
            scene.hideD_G_FBttons();
            interrupt();
        }

        @Override
        public void run() {

            Map<Dir, DaoGenFan> daoGenFanMap = runtimeData.getDaoGenFanMap();
            Dir loaderXiaJia = loaderDir.xiaJia();
            Dir loaderShangJia = loaderDir.shangJia();

            boolean dao = playerMap.get(loaderXiaJia).dao(loaderDir, loaderXiaJia, pokersInfo);

            if (dao) {
                daoGenFanMap.put(loaderXiaJia, DaoGenFan.Dao);
                scene.showDao(loaderXiaJia);

                boolean gen = playerMap.get(loaderShangJia).gen(loaderDir, loaderShangJia, pokersInfo);
                if (gen) {
                    daoGenFanMap.put(loaderShangJia, DaoGenFan.Gen);
                    scene.showGen(loaderShangJia);
                }
            }
            else {
                dao = playerMap.get(loaderShangJia).dao(loaderDir, loaderShangJia, pokersInfo);
                if (dao) {
                    daoGenFanMap.put(loaderShangJia, DaoGenFan.Dao);
                    scene.showDao(loaderShangJia);
                }
            }

            if (dao) {
                boolean fan = playerMap.get(loaderDir).fan(daoGenFanMap, loaderDir, pokersInfo);
                if (fan) {
                    daoGenFanMap.put(loaderDir, DaoGenFan.Fan);
                    scene.showFan(loaderDir);
                }
            }

            waitingReply = false;
            Log.i(TAG, "exit");
        }


        public boolean isWaitingReply() {
            return waitingReply;
        }

        private Dir loaderDir;
        private Map<Dir, Player> playerMap;
        private PokersInfo pokersInfo;
        private boolean waitingReply;
    }

    private class AutoCompleteHandler {

        private AutoCompleteHandler() {
            chuPaiSuggestions = new ArrayList<Hand>(5);
        }

        public void prepare() {

            if (msg.what == SELECT_JIE_PAI) {

                Rule rule = runtimeData.getRule();
                PokersInfo pokersInfo = runtimeData.getPokersInfo();

                JiePaiEvent jiePaiEvent = (JiePaiEvent) Game.this.msg.obj;
                List<Poker> shouPai = pokersInfo.getShouPai(Dir.Outside);

                jiePaiSuggestions = rule.requestSuggestion(jiePaiEvent.chuPai.getHand(), shouPai);
            }
            else if (msg.what == SELECT_CHU_PAI) {
                chuPaiSuggestions.clear();
            }
        }

        public List<ColoredPoker> tryComplete(List<ColoredPoker> selectPokers) {

            PokersInfo pokersInfo = runtimeData.getPokersInfo();

            if (msg.what == SELECT_CHU_PAI) {

                return chuPaiAutoComplete(
                        chuPaiSuggestions,
                        selectPokers,
                        pokersInfo
                );
            }
            else if (msg.what == SELECT_JIE_PAI) {

                return jiePaiAutoComplete(
                        jiePaiSuggestions,
                        selectPokers,
                        pokersInfo
                );
            }

            return null;
        }

        public void reset() {
            jiePaiSuggestions = null;
        }

        private List<Hand> chuPaiSuggestions;
        private List<Hand> jiePaiSuggestions;
    }

    private class SuggestHandler implements ButtonOverlay.OnClickListener {

        @Override
        public void onClick(ButtonOverlay btn) {

            if (msg == null || msg.what != SELECT_JIE_PAI) return;

            JiePaiEvent jiePaiEvent = (JiePaiEvent) msg.obj;
            PokersInfo pokersInfo = runtimeData.getPokersInfo();
            List<Poker> shouPai = pokersInfo.getShouPai(Dir.Outside);
            Hand chuPai = jiePaiEvent.chuPai.getHand();

            if (suggestHands == null) {

//                if (!rule.checkExistBiggerHand(showChuPai, shouPai)) {  // 要不起
//                    jiePaiEvent.jiePai = ColoredHand.BU_YAO;
//                    noticeMsg(msg);
//                    return;
//                }

                Rule rule = runtimeData.getRule();

                suggestHands = prepareSuggestions(
                        rule,
                        chuPai,
                        shouPai,
                        pokersInfo.getShouPaiMap().get(Dir.Outside)
                );
                System.out.println("suggestHands = " + suggestHands);

            }

            int suggestionSize = suggestHands.size();

            if (suggestionSize > 0) {

                List<ColoredPoker> suggestion = suggestHands.get(suggestIndex);

                suggestIndex = (suggestIndex + 1) % suggestionSize;

                scene.showSuggestion(suggestion);
            }
            else {//没有好的建议
                jiePaiEvent.jiePai = ColoredHand.BU_YAO;
                noticeMsg(msg);
            }

        }

        public void clearSuggestion() {
            suggestHands = null;
            suggestIndex = 0;
        }

        private List<List<ColoredPoker>> suggestHands;
        private int suggestIndex;
    }

}
