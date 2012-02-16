package com.ebensz.games.ui.widget;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import com.ebensz.games.R;
import com.ebensz.games.model.hand.ColoredHand;
import com.ebensz.games.model.poker.ColoredPoker;
import com.ebensz.games.model.poker.Poker;
import com.ebensz.games.res.LoadRes;
import com.ebensz.games.scenes.GameSceneBase;
import com.ebensz.games.utils.SleepUtils;
import com.ebensz.games.utils.SlideLineTool;
import ice.animation.Animation;
import ice.animation.AnimationGroup;
import ice.animation.Interpolator.LinearInterpolator;
import ice.animation.ScaleAnimation;
import ice.animation.TranslateAnimation;
import ice.engine.EngineContext;
import ice.node.Overlay;

import java.util.*;

/**
 * User: Mike.Hu
 * Date: 11-11-29
 * Time: 下午12:08
 */
public class OutsidePokerTiles extends DirPokerTiles {
    private static final int SHOU_PAI_MARGIN = 35;
    public static final int SHOU_PAI_Y = 50;
    private static final int CHU_PAI_Y = 400;
    private static final int CHU_PAI_MARGIN = 30;
    public static final int STAND_UP_Y = 30;

    public OutsidePokerTiles(GameSceneBase gameScene) {
        super(gameScene);
        selectedPokers = new ArrayList<ColoredPoker>();
        selectStartPoint = new Point();
        selectEndPoint = new Point();
        selectRegion = new Rect();
        selectedPokerTiles = new HashSet<PokerTile>();
        slideLineTool = new SlideLineTool();
    }

    @Override
    protected DirPositionProvider onCreatePosProvider() {
        return new DirPositionProvider() {
            @Override
            public Point getShouPaiPos(int index, int size) {
                return calShouPaiPos(index, size);
            }

            @Override
            public Point getChuPaiPos(int index, int size) {
                return calChuPaiPos(index, size);
            }
        };
    }

    public void sortAndMakeFront() {
        SleepUtils.sleep(500);

        List<ColoredPoker> pokerList = new ArrayList<ColoredPoker>(shouPai.size());
        for (PokerTile pokerTile : shouPai) {
            pokerList.add(pokerTile.getColoredPoker());
        }

        Collections.sort(pokerList);

        int index = 0;
        for (PokerTile pokerTile : shouPai) {
            pokerTile.setColoredPoker(pokerList.get(index));
            index++;
        }

        final LinearInterpolator linearInterpolator = new LinearInterpolator();

        for (PokerTile pokerTile : shouPai) {

            ScaleAnimation scale = new ScaleAnimation(150, 1, 0.55f, 1, 1);
            scale.setInterpolator(linearInterpolator);

            final ScaleAnimation enlarge = new ScaleAnimation(150, 0.55f, 1, 1, 1);
            enlarge.setInterpolator(linearInterpolator);
            //enlarge.setOffsetTime(10);
//            enlarge.setListener(new Animation.Listener() {
//                @Override
//                public void onAnimationEnd(Overlay drawable) {
//                    drawable.enableHover(); //明牌后才允许悬浮效果
//                    drawable.setOnHoverListener(pokerHoverHandler);
//                }
//            });

            scale.setListener(new Animation.Listener() {
                @Override
                public void onAnimationEnd(Overlay drawable) {
                    drawable.setVisible(false);
                    PokerTile pokerTile = (PokerTile) drawable;
                    Bitmap bitmap = LoadRes.getFrontPoker(pokerTile.getColoredPoker());
                    pokerTile.setBitmap(bitmap);
                    drawable.startAnimation(enlarge);
                }
            });

            pokerTile.startAnimation(scale);
            SleepUtils.sleep(30);
        }
    }

    @Override
    public void faPai(PokerTile pokerTile, int maxSize) {
        PokerTile copy = pokerTile.copy();
        Animation animation = prepareFaPaiAnimation(copy);
        copy.setRemovable(false);
        shouPai.add(copy);
        gameScene.addChild(copy);

        copy.startAnimation(animation);
    }

    @Override
    public void faPaiRemainThree(PokerTile[] remainThree) {
        List<PokerTile> remainThreeCopy = new ArrayList<PokerTile>(3);

        for (int i = 0; i < remainThree.length; i++) {
            PokerTile pokerTile = remainThree[i].copy();
            pokerTile.setRemovable(false);
            //pokerTile.enableHover();
            remainThreeCopy.add(pokerTile);
        }

        shouPai.addAll(remainThreeCopy);


        Collections.sort(remainThreeCopy);
        Collections.sort(shouPai);

        for (int i = 2; i >= 0; i--) {
            PokerTile newPoker = remainThreeCopy.get(i);
            int sortIndex = Collections.binarySearch(shouPai, newPoker);
            if (sortIndex == shouPai.size() - 1) {
                gameScene.addChild(newPoker);
            }
            else {
                PokerTile prePoker = shouPai.get(sortIndex + 1);
                int insertIndex = gameScene.indexOf(prePoker) - 1;
                //gameScene.addChild(insertIndex, newPoker);
                gameScene.addChild(newPoker);
            }

            Point point = posProvider.getShouPaiPos(sortIndex, 20);


            newPoker.startAnimation(TranslateAnimation.createMoveBy(1000, point.x - newPoker.getPosX(), point.y - newPoker.getPosY()));

            // newPoker.setOnHoverListener(pokerHoverHandler);
        }

        tidyShouPai(100);
    }

    @Override
    public void chuPai(ColoredHand chuPai) {
        Poker[] pokers = chuPai.getHand().getPokers();

        List<ColoredPoker> coloredPokers = chuPai.getColoredPokers();
        Collections.sort(coloredPokers);

        List<ColoredPoker> coloredPokersCopy = new ArrayList<ColoredPoker>(coloredPokers.size());

        for (Poker poker : pokers) {
            ColoredPoker coloredPoker = popColoredPoker(poker, coloredPokers);
            coloredPokersCopy.add(coloredPoker);
        }

        chuPai.setColoredPokers(coloredPokersCopy);

        super.chuPai(chuPai);
    }

    private Animation prepareFaPaiAnimation(PokerTile poker) {
        int size = shouPai.size();
        Point point = posProvider.getShouPaiPos(size - 1, 17);

        TranslateAnimation moveBy = TranslateAnimation.createMoveBy(
                1000,
                point.x - poker.getPosX(),
                point.y - poker.getPosY()
        );

        ScaleAnimation scale = new ScaleAnimation(700, 1, 1.468f, 1, 1.31f);

        scale.setListener(new Animation.Listener() {
            @Override
            public void onAnimationEnd(Overlay drawable) {
                PokerTile poker = (PokerTile) drawable;
                poker.setBitmap(LoadRes.getBitmap(R.drawable.poker_back_large));
            }
        });

        AnimationGroup group = new AnimationGroup();
        group.add(moveBy);
        group.add(scale);
        return group;
    }

    public void handlerSelectPokers(MotionEvent event) {

        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        selectEndPoint.set(x, y);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                selectStartPoint.set(x, y);
                selectedPokerTiles.clear();
                break;

            case MotionEvent.ACTION_MOVE:
                selectedPokerTiles.clear();

                selectRegion.set(
                        Math.min(selectStartPoint.x, selectEndPoint.x),
                        Math.min(selectStartPoint.y, selectEndPoint.y),
                        Math.max(selectStartPoint.x, selectEndPoint.x),
                        Math.max(selectStartPoint.y, selectEndPoint.y)
                );

                if (!validY()) return;

                for (int i = shouPai.size() - 1; i >= 0; i--) {

                    PokerTile pokerTile = shouPai.get(i);

                    boolean regionTest = selectRegionTest(i, pokerTile);

                    if (regionTest)
                        selectedPokerTiles.add(pokerTile);

                    pokerTile.setSelected(regionTest);
                }

                break;
            case MotionEvent.ACTION_UP:

                PokerTile pokerTile = findFromRightToLeft(x, y);
                if (pokerTile != null && !selectedPokerTiles.contains(pokerTile)) {
                    selectedPokerTiles.add(pokerTile);
                }

                boolean containsSome = false;
                boolean doNotContainAll = false;
                if (selectedPokerTiles.size() > 1) {
                    for (PokerTile tile : selectedPokerTiles) {
                        if (selectedPokers.contains(tile.getColoredPoker())) {
                            containsSome = true;
                        }
                        else {
                            doNotContainAll = true;
                        }

                        if (containsSome && doNotContainAll) {
                            selectedPokerTiles.clear();
                            selectedPokers.clear();
                            break;
                        }
                    }
                }
                else {

                }

                postSelect(selectedPokerTiles, containsSome && doNotContainAll);
                break;
        }

    }

    private boolean selectRegionTest(int i, PokerTile pokerTile) {
        float pokerTileX = pokerTile.getPosX();
        return (pokerTileX + SHOU_PAI_MARGIN >= selectRegion.left && pokerTileX <= selectRegion.right)
                || (i == shouPai.size() - 1 && pokerTileX < selectRegion.left && pokerTileX + pokerTile.getWidth() > selectRegion.right);
    }

    public boolean handlerSlidePokers(MotionEvent event) {

        int action = event.getAction();

        int x = (int) event.getX();
        int y = (int) event.getY();
        long time = System.currentTimeMillis();

        if (action == MotionEvent.ACTION_MOVE) {
            slideLineTool.addData(x, y, time);
        }

        if (action == MotionEvent.ACTION_UP) {
            slideLineTool.addData(x, y, time);

            int validCount = slideLineTool.getValidCount();
            int startX = slideLineTool.getValidStartX(validCount);
            int startY = slideLineTool.getValidStartY(validCount);

            if (!checkValidStart(startX, startY))
                return false;

            return slideLineTool.isSlideLine(validCount);
        }

        return false;
    }

    private boolean checkValidStart(int startX, int startY) {

        PokerTile pokerTile = findFromRightToLeft(startX, startY);
        if (pokerTile != null) {
            for (ColoredPoker selectedPokerTile : selectedPokers) {
                if (pokerTile.getColoredPoker().getColor() == null)
                    return pokerTile.getColoredPoker().getPoker() == selectedPokerTile.getPoker();
                if (pokerTile.getColoredPoker().getColor() == selectedPokerTile.getColor()
                        && pokerTile.getColoredPoker().getPoker() == selectedPokerTile.getPoker())
                    return true;
            }
        }

        return false;
    }

    public ColoredHand getValidatedHand() {
        return validatedHand;
    }

    public void setValidatedHand(ColoredHand validatedHand) {
        if (validatedHand != null) {
            selectedPokers.clear();
            selectedPokers.addAll(validatedHand.getColoredPokers());
        }

        this.validatedHand = validatedHand;
    }

    public List<ColoredPoker> getSelectedPokers() {
        return selectedPokers;
    }

    public void setSelectedPokers(List<ColoredPoker> selectedPokers) {
        this.selectedPokers = selectedPokers;
    }

    public void showSelectedPokers(List<ColoredPoker> pokers) {

        for (PokerTile pokerTile : shouPai) {
            ColoredPoker tilePoker = pokerTile.getColoredPoker();

            if (pokers.contains(tilePoker) && pokerTile.getPosY() == SHOU_PAI_Y) {
                pokerTile.standUp();
            }

        }
    }

    public Set<PokerTile> getSelectedPokerTiles() {
        return selectedPokerTiles;
    }

    private ColoredPoker popColoredPoker(Poker poker, List<ColoredPoker> coloredPokers) {

        for (Iterator<ColoredPoker> iterator = coloredPokers.iterator(); iterator.hasNext(); ) {
            ColoredPoker coloredPoker = iterator.next();
            if (coloredPoker.getPoker() == poker) {
                iterator.remove();
                return coloredPoker;
            }
        }

        return null;
    }

    private void postSelect(Set<PokerTile> multiSelection, boolean tidyShouPai) {

        if (multiSelection.size() == 0) {
            if (tidyShouPai) //multiSelection size =0 点击非牌区域时也为0，此时不应该清掉选择的牌
                tidyShouPai(100);
        }
        else {
            for (PokerTile pokerTile : multiSelection) {
                ColoredPoker coloredPoker = pokerTile.getColoredPoker();

                if (selectedPokers.contains(coloredPoker)) {
                    selectedPokers.remove(coloredPoker);
                    pokerTile.sitDown();
                }
                else {
                    selectedPokers.add(coloredPoker);
                    pokerTile.standUp();
                }
            }
        }

        for (PokerTile pokerTile : shouPai) {
            pokerTile.setSelected(false);
        }
    }

    private boolean validY() {
        for (int i = shouPai.size() - 1; i >= 0; i--) {

            PokerTile pokerTile = shouPai.get(i);
            if (selectRegion.top <= pokerTile.getPosY() + pokerTile.getHeight()
                    && selectRegion.bottom >= pokerTile.getPosY())
                return true;
        }

        return false;
    }

    private PokerTile findFromRightToLeft(int x, int y) {
        int size = shouPai.size();

        for (int i = size - 1; i >= 0; i--) {

            PokerTile pokerTile = shouPai.get(i);

            if (pokerTile.hitTest(x, y))
                return pokerTile;

        }

        return null;
    }

    private Point calShouPaiPos(int index, int size) {
        int eachWidth = LoadRes.getOutsidePokerWidth();

        int totalWidth = (size - 1) * SHOU_PAI_MARGIN + eachWidth;
        int startX = (EngineContext.getAppWidth() - totalWidth) / 2;
        return new Point(startX + index * SHOU_PAI_MARGIN, SHOU_PAI_Y);
    }

    private Point calChuPaiPos(int index, int size) {
        int eachWidth = LoadRes.getOutsidePokerWidth();

        int totalWidth = (size - 1) * CHU_PAI_MARGIN + eachWidth;
        int startX = (EngineContext.getAppWidth() - totalWidth) / 2;
        return new Point(startX + index * CHU_PAI_MARGIN, CHU_PAI_Y);
    }

    public void clearSelectedPokers() {
        for (PokerTile pokerTile : selectedPokerTiles) {
            pokerTile.setSelected(false);
        }

        selectedPokerTiles.clear();
        selectedPokers.clear();
    }

    private Rect selectRegion;
    private Point selectStartPoint, selectEndPoint;
    private Set<PokerTile> selectedPokerTiles;
    private SlideLineTool slideLineTool;

    private ColoredHand validatedHand;
    private List<ColoredPoker> selectedPokers;


}
