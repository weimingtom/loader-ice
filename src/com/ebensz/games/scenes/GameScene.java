package com.ebensz.games.scenes;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
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
import ice.node.Overlay;
import ice.node.widget.BitmapOverlay;
import ice.node.widget.ButtonOverlay;
import ice.res.Res;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.ebensz.games.logic.interactive.Message.*;

/**
 * User: Mike.Hu
 * Date: 11-11-14
 * Time: 下午3:14
 */
public abstract class GameScene extends GameSceneBase {

    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;

    public static final int ROB_SCORE_Y = 200;

    protected GameScene(Game game) {
        this.game = game;
        msg = new Message();

        addChild(sliceTile = new SliceTile());
    }

    @Override
    public void reset() {
        super.reset();

        if (settleBoard != null) {
            settleBoard.setRemovable(true);
            settleBoard = null;
        }

        passBtn = null;
        oneBtn = null;
        twoBtn = null;
        threeBtn = null;

        d_g_fBtn = null;

        d_g_fPassBtn = null;
        chuPaiBtn = null;
        chuPaiPassBtn = null;
        suggestBtn = null;
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

        if (chu_or_jie) {

            if (onSlideTouch(event)) {
                outsidePokers.clearSelectedPokers();
                return true;
            }

        }

        outsidePokers.handlerSelectPokers(event);

        if (chu_or_jie)
            game.onPokersSelected();

        return super.onTouchEvent(event);
    }

    private boolean onSlideTouch(MotionEvent event) {
        boolean flag = outsidePokers.handlerSlidePokers(event);
        if (flag == true)
            game.tryPostChuOrJie();
        return flag;
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
        Bitmap scoreBitmap;
        if (score == RobLoaderScore.Pass) {
            scoreBitmap = LoadRes.getBitmap(R.drawable.point0);
        }
        else {
            scoreBitmap = LoadRes.createDigitBitmap(score.getMultiple(), false);
        }

        PointF pos = new PointF();
        switch (dir) {
            case Left:
                pos.set(100, 100);
                break;
            case Outside:
                pos.set(500, 500);
                break;
            case Right:
                pos.set(900, 100);
                break;
        }

        BitmapOverlay scoreTile = new BitmapOverlay(scoreBitmap, pos);
        addChild(scoreTile);
        scoreTile.startAnimation(AlphaAnimation.createFadeOut(1500));
        SleepUtils.sleep(1500);
    }

    public void showDao(Dir loaderXiaJia) {

        Bitmap daoBitmap = LoadRes.getBitmap(R.drawable.dao_zi);
        PointF pos = new PointF();
        switch (loaderXiaJia) {
            case Left:
                pos.set(100, 100);
                break;
            case Outside:
                pos.set(500, 500);
                break;
            case Right:
                pos.set(900, 100);
                break;
        }
        BitmapOverlay daoTile = new BitmapOverlay(daoBitmap, pos);
        addChild(daoTile);
        daoTile.startAnimation(AlphaAnimation.createFadeOut(1500));
        SleepUtils.sleep(1500);
    }

    public void showGen(Dir loaderShangJia) {

        Bitmap genBitmap = LoadRes.getBitmap(R.drawable.gen_zi);
        PointF pos = new PointF();
        switch (loaderShangJia) {
            case Left:
                pos.set(100, 100);
                break;
            case Outside:
                pos.set(500, 500);
                break;
            case Right:
                pos.set(900, 100);
                break;
        }
        BitmapOverlay genTile = new BitmapOverlay(genBitmap, pos);
        addChild(genTile);
        genTile.startAnimation(AlphaAnimation.createFadeOut(1500));
        SleepUtils.sleep(1500);
    }

    public void showFan(Dir loaderDir) {

        Bitmap fanBitmap = LoadRes.getBitmap(R.drawable.fan_zi);
        PointF pos = new PointF();
        switch (loaderDir) {
            case Left:
                pos.set(100, 100);
                break;
            case Outside:
                pos.set(500, 500);
                break;
            case Right:
                pos.set(900, 100);
                break;
        }
        BitmapOverlay fanTile = new BitmapOverlay(fanBitmap, pos);
        addChild(fanTile);
        fanTile.startAnimation(AlphaAnimation.createFadeOut(1500));
        SleepUtils.sleep(1500);
    }

    public void showRobLoaderFail() {
        Bitmap bitmap = LoadRes.getBitmap(R.drawable.nobody_callscore);
        PointF pos = new PointF(
                (WIDTH - bitmap.getWidth()) / 2,
                (HEIGHT - bitmap.getHeight()) / 2
        );
        BitmapOverlay bitmapTile = new BitmapOverlay(bitmap, pos);
        addChild(bitmapTile);
        bitmapTile.startAnimation(AlphaAnimation.createFadeOut(1500));
        SleepUtils.sleep(1500);
    }

    public void showFaPai(List<Dir> order, Map<Dir, List<ColoredPoker>> shouPaiMap, List<ColoredPoker> leftThree) {
        packOfCardTiles = new PackOfCardTiles(order, shouPaiMap, leftThree);
        packOfCardTiles.show(this);

        for (int i = 0; i < 17; i++) {
            for (Dir dir : order) {
                PokerOverlay pokerOverlay = packOfCardTiles.pop();

                getPokerTiles(dir).faPai(i, pokerOverlay, 17);
            }
        }

        SleepUtils.sleep(700);

        packOfCardTiles.tidy();//展开剩余的三张牌

        SleepUtils.sleep(700);
    }

    public void sortAndShowHumanPokersFront() {
        outsidePokers.sortAndMakeFront();
        leftPokers.sortAndMakeFront();
        rightPokers.sortAndMakeFront();
    }

    public void showFaPaiLeftThree(Dir loaderDir, List<ColoredPoker> leftThree) {
        packOfCardTiles.showLeftThree();

        DirPokerTiles loaderShouPai = getPokerTiles(loaderDir);

        PokerOverlay[] remainThree = new PokerOverlay[3];
        for (int i = 0; i < 3; i++) {
            remainThree[i] = packOfCardTiles.pop();
        }

        loaderShouPai.faPaiRemainThree(remainThree);

        SleepUtils.sleep(500);
    }

    public void showRobLoaderButtons() {

        Bitmap normal = Res.getBitmap(R.drawable.by_button_1);
        Bitmap pressed = Res.getBitmap(R.drawable.by_button_2);
        passBtn = new ButtonOverlay(normal, pressed);
        passBtn.setPos(500, ROB_SCORE_Y);

        normal = Res.getBitmap(R.drawable.point1_button_1);
        pressed = Res.getBitmap(R.drawable.point1_button_2);
        oneBtn = new ButtonOverlay(normal, pressed);
        oneBtn.setPos(600, ROB_SCORE_Y);

        normal = Res.getBitmap(R.drawable.point2_button_1);
        pressed = Res.getBitmap(R.drawable.point2_button_2);
        twoBtn = new ButtonOverlay(normal, pressed);
        twoBtn.setPos(700, ROB_SCORE_Y);

        normal = Res.getBitmap(R.drawable.point3_button_1);
        pressed = Res.getBitmap(R.drawable.point3_button_2);
        threeBtn = new ButtonOverlay(normal, pressed);
        threeBtn.setPos(800, ROB_SCORE_Y);

        addChildren(passBtn, oneBtn, twoBtn, threeBtn);
    }

    public void hideRobLoaderButtons() {
        passBtn.setRemovable(true);
        oneBtn.setRemovable(true);
        twoBtn.setRemovable(true);
        threeBtn.setRemovable(true);
    }

    public void showD_G_F(int what) {
        Bitmap normal = null, pressed = null;

        switch (what) {
            case SELECT_DAO:
                normal = Res.getBitmap(R.drawable.dao_button_1);
                pressed = Res.getBitmap(R.drawable.dao_button_2);
                break;
            case SELECT_GEN:
                normal = Res.getBitmap(R.drawable.gen_button_1);
                pressed = Res.getBitmap(R.drawable.gen_button_2);
                break;
            case SELECT_FAN:
                normal = Res.getBitmap(R.drawable.fan_button_1);
                pressed = Res.getBitmap(R.drawable.fan_button_2);
                break;
        }

        d_g_fBtn = new ButtonOverlay(normal, pressed);
        d_g_fBtn.setPos(500, 500);

        normal = Res.getBitmap(R.drawable.by_button_1);
        pressed = Res.getBitmap(R.drawable.by_button_2);
        d_g_fPassBtn = new ButtonOverlay(normal, pressed);
        d_g_fBtn.setPos(600, 500);

        addChildren(d_g_fBtn, d_g_fPassBtn);
    }

    public void hideD_G_FBttons() {
        remove(d_g_fBtn);
        remove(d_g_fPassBtn);
    }

    public void showChuPaiBtns(boolean jiePai) {

        if (jiePai) {
            if (suggestBtn == null) {
                Bitmap normal = Res.getBitmap(R.drawable.tshi_button_1);
                Bitmap pressed = Res.getBitmap(R.drawable.tshi_button_2);
                suggestBtn = new ButtonOverlay(normal, pressed);
                suggestBtn.setPos((WIDTH - normal.getWidth()) / 2, 200);

                addChildren(suggestBtn);
            }
            else {
                suggestBtn.setVisible(true);
            }

        }

        if (chuPaiBtn == null) {
            chuPaiBtn = new ButtonOverlay(R.drawable.chupai_button_1, R.drawable.chupai_button_2);
            chuPaiBtn.setPos(600, 250);

            chuPaiPassBtn = new ButtonOverlay(R.drawable.by_button_1, R.drawable.by_button_2);
            chuPaiPassBtn.setPos(300, 250);

            addChildren(chuPaiBtn, chuPaiPassBtn);
        }
        else {
            chuPaiBtn.setVisible(true);
            chuPaiPassBtn.setVisible(true);
        }

    }

    public DirPokerTiles getPokerTiles(Dir dir) {
        switch (dir) {
            case Left:
                return leftPokers;
            case Right:
                return rightPokers;
            case Outside:
                return outsidePokers;
        }
        return null;
    }

    public abstract void showChuPai(Dir chuPaiPlayer, ColoredHand chuPai, boolean noShouPaiLeft);

    public abstract void showJiePai(Dir jiePaiPlayer, ColoredHand jiePai, Dir chuPaiPlayer, boolean noShouPaiLeft);

    public void showBuYao(Dir jiePaiDir, Dir chuPaiDir) {
        BitmapOverlay buYaoTile = new BitmapOverlay(R.drawable.by_button_1);

        Point pos = new Point();
        switch (jiePaiDir) {
            case Left:
                pos.set(100, 100);
                break;
            case Outside:
                pos.set((int) (WIDTH - buYaoTile.getWidth()) / 2, 600);
                break;
            case Right:
                pos.set(800, 100);
                break;
        }
        buYaoTile.setPos(pos.x, pos.y);
        addChild(buYaoTile);
        buYaoTile.startAnimation(AlphaAnimation.createFadeOut(1500));

        SleepUtils.sleep(1000);
    }

    public void showSuggestion(List<ColoredPoker> suggestion) {

        List<ColoredPoker> currentSelect = outsidePokers.getSelectedPokers();

        if (currentSelect.size() > 0) {
            outsidePokers.tidyShouPai(70);
            currentSelect.clear();
            SleepUtils.sleep(100);
        }

        outsidePokers.setSelectedPokers(new ArrayList<ColoredPoker>(suggestion));
        outsidePokers.showSelectedPokers(suggestion);
    }

    public void showSettle(SettleTool.Result result) {

        showDetailBoard(result);

        showScores(result);

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

            BitmapOverlay scoreTile = new BitmapOverlay(LoadRes.createDigitBitmap(winScore), pos);
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

    public ButtonOverlay getPassBtn() {
        return passBtn;
    }

    public ButtonOverlay getOneBtn() {
        return oneBtn;
    }

    public ButtonOverlay getTwoBtn() {
        return twoBtn;
    }

    public ButtonOverlay getThreeBtn() {
        return threeBtn;
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

    private SettleBoard settleBoard;

    private SliceTile sliceTile;

    private ButtonOverlay passBtn;
    private ButtonOverlay oneBtn;
    private ButtonOverlay twoBtn;
    private ButtonOverlay threeBtn;

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
}
