package com.ebensz.games.scenes;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import com.ebensz.games.R;
import com.ebensz.games.logic.interactive.Message;
import com.ebensz.games.logic.settle.SettleTool;
import com.ebensz.games.logic.story.Game;
import com.ebensz.games.model.Dir;
import com.ebensz.games.model.RobLoaderScore;
import com.ebensz.games.model.Role.Role;
import com.ebensz.games.model.hand.ColoredHand;
import com.ebensz.games.model.poker.ColoredPoker;
import com.ebensz.games.res.LoadRes;
import com.ebensz.games.ui.widget.*;
import com.ebensz.games.utils.SleepUtils;
import ice.animation.AlphaAnimation;
import ice.animation.Animation;
import ice.animation.TranslateAnimation;
import ice.engine.EngineContext;
import ice.graphic.texture.Texture;
import ice.node.Overlay;
import ice.node.widget.AtlasOverlay;
import ice.node.widget.BitmapOverlay;
import ice.node.widget.ButtonOverlay;

import java.util.*;

import static com.ebensz.games.logic.interactive.Message.*;

/**
 * User: Mike.Hu
 * Date: 11-11-14
 * Time: 下午3:14
 */
public abstract class GameScene extends GameSceneBase {
    public static final int ROB_SCORE_Y = 250;
    private static final String TAG = "GameScene";

    protected GameScene(Game game) {
        this.game = game;
        msg = new Message();

        background = new BitmapOverlay(R.drawable.bg_game_1);
        background.setPos(background.getWidth() / 2, background.getHeight() / 2);
        addChild(background);
        addChild(gameControllerBar);

        addChild(sliceTile = new SliceTile());

        addChild(pokersOverlay = new PokersOverlay());
    }

    public void reset() {
        pokersOverlay.clean();

        for (int i = 0; i < size(); i++) {
            Overlay overlay = get(i);

            if (overlay instanceof ButtonOverlay || overlay instanceof SettleBoard)
                remove(overlay);
        }

    }

    public void update(Message msg) {
        this.msg = msg;
    }

    @Override
    protected boolean onDispatchTouch(Overlay child, MotionEvent event) {
        if (child instanceof PokerOverlay || child == sliceTile) { //避免一次touch 被分发两次
            return false;
        }

        return super.onDispatchTouch(child, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        sliceTile.onTouchEvent(event);

        boolean chu_or_jie = msg.what == SELECT_CHU_PAI || msg.what == SELECT_JIE_PAI;

//        if (chu_or_jie) {
//
//            if (onSlideTouch(event)) {
//                outsidePokers.clearSelectedPokers();
//                return true;
//            }
//
//        }

        OutsidePokerTiles outsidePokers = pokersOverlay.getOutsidePokerTiles();
        outsidePokers.handlerSelectPokers(event);

        if (chu_or_jie)
            game.onPokersSelected();

        return super.onTouchEvent(event);
    }

    public void showPlayers(Map<Dir, Role> roleMap) {

        if (roleInfoLeft == null) {
            Role role = roleMap.get(Dir.Left);
            roleInfoLeft = new RoleTile(Dir.Left, role);

            role = roleMap.get(Dir.Right);
            roleInfoRight = new RoleTile(Dir.Right, role);

            role = roleMap.get(Dir.Outside);
            roleInfoOutside = new RoleTile(Dir.Outside, role);

            addChildren(roleInfoLeft, roleInfoRight, roleInfoOutside);
        }
    }

    public void showRobLoader(Dir dir, RobLoaderScore score) {

        BitmapOverlay scoreTile;

        if (score == RobLoaderScore.Pass) {
            scoreTile = new BitmapOverlay(R.drawable.point0);
        }
        else {
            AtlasOverlay atlasOverlay = new AtlasOverlay(30, 40, 11);
            atlasOverlay.setAtlasIndex(
                    score.ordinal() + 1,
                    new Texture(R.drawable.digit_positive)
            );

            scoreTile = atlasOverlay;
        }

        switch (dir) {
            case Left:
                scoreTile.setPos(100, 600);
                break;
            case Outside:
                scoreTile.setPos(getWidth() / 2, 200);
                break;
            case Right:
                scoreTile.setPos(900, 600);
                break;
        }

        scoreTile.startAnimation(AlphaAnimation.createFadeOut(1500));

        addChild(scoreTile);

        SleepUtils.sleep(1500);
    }

    public void showDao(Dir loaderXiaJia) {
        BitmapOverlay daoTile = new BitmapOverlay(R.drawable.dao_zi);

        switch (loaderXiaJia) {
            case Left:
                daoTile.setPos(100, 600);
                break;
            case Outside:
                daoTile.setPos(getWidth() / 2, 200);
                break;
            case Right:
                daoTile.setPos(900, 600);
                break;
        }

        daoTile.startAnimation(AlphaAnimation.createFadeOut(1500));

        addChild(daoTile);
        SleepUtils.sleep(1500);
    }

    public void showGen(Dir loaderShangJia) {
        BitmapOverlay genTile = new BitmapOverlay(R.drawable.gen_zi);

        switch (loaderShangJia) {
            case Left:
                genTile.setPos(100, 600);
                break;
            case Outside:
                genTile.setPos(getWidth() / 2, 200);
                break;
            case Right:
                genTile.setPos(900, 600);
                break;
        }

        genTile.startAnimation(AlphaAnimation.createFadeOut(1500));

        addChild(genTile);
        SleepUtils.sleep(1500);
    }

    public void showFan(Dir loaderDir) {
        BitmapOverlay fanTile = new BitmapOverlay(R.drawable.fan_zi);

        switch (loaderDir) {
            case Left:
                fanTile.setPos(100, 600);
                break;
            case Outside:
                fanTile.setPos(getWidth() / 2, 200);
                break;
            case Right:
                fanTile.setPos(900, 600);
                break;
        }
        fanTile.startAnimation(AlphaAnimation.createFadeOut(1500));

        addChild(fanTile);
        SleepUtils.sleep(1500);
    }

    public void showRobLoaderFail() {
        BitmapOverlay bitmapTile = new BitmapOverlay(R.drawable.nobody_callscore);

        bitmapTile.setPos(getWidth() / 2, getHeight() / 2);

        bitmapTile.startAnimation(AlphaAnimation.createFadeOut(1500));

        addChild(bitmapTile);

        SleepUtils.sleep(1500);
    }

    public void showFaPai(List<Dir> order, Map<Dir, List<ColoredPoker>> shouPaiMap, List<ColoredPoker> leftThree) {
        pokersOverlay.showFaPai(order, shouPaiMap, leftThree);
    }

    public void showFaPaiLeftThree(Dir loaderDir, List<ColoredPoker> leftThree) {

        pokersOverlay.showFaPaiLeftThree(loaderDir, leftThree);

        SleepUtils.sleep(500);
    }

    public HashMap<RobLoaderScore, ButtonOverlay> showRobLoaderButtons(RobLoaderScore lastHightestScore) {
        robScoreBtnMap = new HashMap<RobLoaderScore, ButtonOverlay>(4);

        List<RobLoaderScore> allScores = Arrays.asList(RobLoaderScore.values());
        List<RobLoaderScore> validScores = new ArrayList<RobLoaderScore>(allScores);

        switch (lastHightestScore) {
            case Pass:
                break;

            case Three:
                throw new IllegalStateException("lastHightestScore" + lastHightestScore);
            case Two:
                validScores.remove(RobLoaderScore.Two);
            case One:
                validScores.remove(RobLoaderScore.One);
                break;
        }


        for (RobLoaderScore validScore : validScores) {
            int normalId = 0;
            int pressedId = 0;

            switch (validScore) {
                case Pass:
                    normalId = R.drawable.by_button_1;
                    pressedId = R.drawable.by_button_2;
                    break;
                case One:
                    normalId = R.drawable.point1_button_1;
                    pressedId = R.drawable.point1_button_2;
                    break;
                case Two:
                    normalId = R.drawable.point2_button_1;
                    pressedId = R.drawable.point2_button_2;
                    break;
                case Three:
                    normalId = R.drawable.point3_button_1;
                    pressedId = R.drawable.point3_button_2;
                    break;
            }

            robScoreBtnMap.put(validScore, new ButtonOverlay(normalId, pressedId));
        }

        int eachWidth = 99;
        int margin = 20;
        float totalWidth = validScores.size() * eachWidth + (validScores.size() - 1) * margin;
        float mostLeft = EngineContext.getAppWidth() / 2 - totalWidth / 2 + eachWidth / 2;

        Collections.sort(validScores);

        for (int i = 0; i < validScores.size(); i++) {
            ButtonOverlay button = robScoreBtnMap.get(validScores.get(i));
            button.setPos(mostLeft + i * (eachWidth + margin), ROB_SCORE_Y);
        }

        addChildren(robScoreBtnMap.values());

        return robScoreBtnMap;
    }

    public void hideRobLoaderButtons() {
        remove(robScoreBtnMap.values());
        robScoreBtnMap.clear();
    }

    public void showD_G_F(int what) {
        int normal = 0, pressed = 0;

        switch (what) {
            case SELECT_DAO:
                normal = R.drawable.dao_button_1;
                pressed = R.drawable.dao_button_2;
                break;
            case SELECT_GEN:
                normal = R.drawable.gen_button_1;
                pressed = R.drawable.gen_button_2;
                break;
            case SELECT_FAN:
                normal = R.drawable.fan_button_1;
                pressed = R.drawable.fan_button_2;
                break;
        }

        d_g_fBtn = new ButtonOverlay(normal, pressed);
        d_g_fBtn.setPos(500, 400);

        d_g_fPassBtn = new ButtonOverlay(
                R.drawable.by_button_1,
                R.drawable.by_button_2
        );
        d_g_fBtn.setPos(600, 400);

        addChildren(d_g_fBtn, d_g_fPassBtn);
    }

    public void hideD_G_FBttons() {
        remove(d_g_fBtn, d_g_fPassBtn);
    }

    public void showChuPaiBtns(boolean jiePai) {

        if (jiePai) {
            if (suggestBtn == null) {
                suggestBtn = new ButtonOverlay(R.drawable.tshi_button_1, R.drawable.tshi_button_2);
                suggestBtn.setPos(getWidth() / 2, 250);

                addChildren(suggestBtn);
            }
            else {
                suggestBtn.setVisible(true);
            }
        }

        if (chuPaiBtn == null) {
            chuPaiBtn = new ButtonOverlay(R.drawable.chupai_button_1, R.drawable.chupai_button_2);
            chuPaiBtn.setPos(getWidth() / 2 + 200, 250);

            chuPaiPassBtn = new ButtonOverlay(R.drawable.by_button_1, R.drawable.by_button_2);
            chuPaiPassBtn.setPos(getWidth() / 2 - 200, 250);

            addChildren(chuPaiBtn, chuPaiPassBtn);
        }
        else {
            chuPaiBtn.setVisible(true);
            chuPaiPassBtn.setVisible(true);
        }

    }

    public void showChuPai(Dir dir, ColoredHand chuPai, boolean noShouPaiLeft) {
        pokersOverlay.showChuPai(dir, chuPai);
        SleepUtils.sleep(noShouPaiLeft ? 2000 : 400);
        Log.i(TAG, "showChuPai player: " + dir + "--" + chuPai);
    }

    public void showJiePai(Dir jiePaiDir, ColoredHand jiePai, Dir chuPaiDir, boolean noShouPaiLeft) {
        pokersOverlay.hideLastChuPai(jiePaiDir);
        SleepUtils.sleep(300);

        showChuPai(jiePaiDir, jiePai, noShouPaiLeft);
        SleepUtils.sleep(noShouPaiLeft ? 2000 : 400);

        Log.i(TAG, "jiePai player: " + jiePaiDir + "--" + jiePai);
    }

    public void showBuYao(Dir jiePaiDir, Dir chuPaiDir) {
        BitmapOverlay buYaoTile = new BitmapOverlay(R.drawable.by_button_1);

        PointF pos = new PointF();
        switch (jiePaiDir) {
            case Left:
                pos.set(100, 400);
                break;
            case Outside:
                pos.set(getWidth() / 2, 300);
                break;
            case Right:
                pos.set(800, 400);
                break;
        }
        buYaoTile.setPos(pos.x, pos.y);
        addChild(buYaoTile);
        buYaoTile.startAnimation(AlphaAnimation.createFadeOut(1500));

        SleepUtils.sleep(1000);
    }

    public void showSuggestion(List<ColoredPoker> suggestion) {
        pokersOverlay.getOutsidePokerTiles().showSuggestion(suggestion);
    }

    public void showSettle(SettleTool.Result result) {

        showDetailBoard(result);

        showScores(result);

        showUpgrade(result);
    }

    private void showDetailBoard(SettleTool.Result result) {
        addChild(settleBoard = new SettleBoard(result));
    }

    private void showScores(SettleTool.Result result) {
        List<Overlay> tiles = new ArrayList<Overlay>(result.winScores.size());

        for (final Dir dir : result.winScores.keySet()) {
            final int winScore = result.winScores.get(dir);

            final Point pos = new Point();
            RoleTile roleTile = null;

            switch (dir) {
                case Left:
                    pos.set(100, 100);
                    roleTile = roleInfoLeft;
                    break;
                case Right:
                    pos.set(800, 100);
                    roleTile = roleInfoRight;
                    break;
                case Outside:
                    pos.set(500, 600);
                    roleTile = roleInfoOutside;
                    break;
            }

            final RoleTile theRoleTile = roleTile;

            BitmapOverlay scoreTile = new BitmapOverlay(LoadRes.createDigitBitmap(winScore));
            scoreTile.setPos(pos.x, pos.y);

            scoreTile.setVisible(false);

            AlphaAnimation fadeOut = AlphaAnimation.createFadeOut(4000);

            fadeOut.setListener(new Animation.Listener() {
                @Override
                public void onAnimationEnd(Overlay drawable) {
                    theRoleTile.update(winScore);
                }
            });

            tiles.add(scoreTile);

            scoreTile.startAnimation(fadeOut);
        }

        addChildren(tiles);
    }

    private void showUpgrade(SettleTool.Result result) {

        Boolean upgradeFlag = result.upgradeFlag.get(Dir.Outside);

        if (upgradeFlag == true) {

            final BitmapOverlay upgradeOverlay = new BitmapOverlay(R.drawable.update_grade);
            upgradeOverlay.setPos(this.getWidth() / 2, this.getHeight() / 3);

            TranslateAnimation translateAnimation = new TranslateAnimation(1000, 0, 0, 200);
            translateAnimation.setListener(new Animation.Listener() {
                @Override
                public void onAnimationEnd(Overlay overlay) {
                    upgradeOverlay.setVisible(false);
                }
            });
            upgradeOverlay.addGlStatusController(translateAnimation);

            addChild(upgradeOverlay);
        }
    }

    public ButtonOverlay getD_g_fBtn() {
        return d_g_fBtn;
    }

    public ButtonOverlay getD_g_fPassBtn() {
        return d_g_fPassBtn;
    }

    public ButtonOverlay getChuPaiBtn() {
        return chuPaiBtn;
    }

    public ButtonOverlay getChuPaiPassBtn() {
        return chuPaiPassBtn;
    }

    public ButtonOverlay getSuggestBtn() {
        return suggestBtn;
    }

    public PokersOverlay getPokersOverlay() {
        return pokersOverlay;
    }

    private HashMap<RobLoaderScore, ButtonOverlay> robScoreBtnMap;

    private SettleBoard settleBoard;

    private SliceTile sliceTile;

    private ButtonOverlay d_g_fBtn;

    private ButtonOverlay d_g_fPassBtn;
    private ButtonOverlay chuPaiBtn;
    private ButtonOverlay chuPaiPassBtn;
    private ButtonOverlay suggestBtn;


    private RoleTile roleInfoLeft;
    private RoleTile roleInfoRight;
    private RoleTile roleInfoOutside;
    private Message msg;
    private Game game;

    protected PokersOverlay pokersOverlay;

    private BitmapOverlay background;
}
