package com.ebensz.games.ui.widget;

import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import com.ebensz.games.model.hand.ColoredHand;
import com.ebensz.games.model.poker.ColoredPoker;
import com.ebensz.games.model.poker.Poker;
import com.ebensz.games.res.LoadRes;
import com.ebensz.games.scenes.GameSceneBase;
import com.ebensz.games.utils.SleepUtils;
import com.ebensz.games.utils.SlideLineTool;
import ice.animation.TranslateAnimation;
import ice.engine.EngineContext;

import java.util.*;

/**
 * User: Mike.Hu
 * Date: 11-11-29
 * Time: 下午12:08
 */
public class OutsidePokerTiles extends DirPokerTiles {
    private static final int SHOU_PAI_MARGIN = 35;
    public static final int SHOU_PAI_Y = 80;
    private static final int CHU_PAI_Y = 400;
    private static final int CHU_PAI_MARGIN = 30;
    public static final int STAND_UP_Y = 30;

    public OutsidePokerTiles(GameSceneBase gameScene) {
        super(gameScene);
        selectedPokers = new ArrayList<ColoredPoker>();
        selectStartPoint = new Point();
        selectEndPoint = new Point();
        selectRegion = new Rect();
        selectedPokerOverlays = new HashSet<PokerOverlay>();
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
        for (PokerOverlay pokerOverlay : shouPai) {
            pokerList.add(pokerOverlay.getColoredPoker());
        }

        Collections.sort(pokerList);

        int index = 0;
        for (PokerOverlay pokerOverlay : shouPai) {
            pokerOverlay.setColoredPoker(pokerList.get(index));
            index++;
        }

        for (PokerOverlay pokerOverlay : shouPai) {
            pokerOverlay.rotateToFront();
            SleepUtils.sleep(30);
        }
    }

    @Override
    public void faPai(int index, PokerOverlay pokerOverlay, int maxSize) {
        shouPai.add(pokerOverlay);

        Point point = posProvider.getShouPaiPos(index, maxSize);

        TranslateAnimation animation = new TranslateAnimation(1000, point.x - pokerOverlay.getPosX(), point.y - pokerOverlay.getPosY());

        pokerOverlay.startAnimation(animation);
    }

    @Override
    public void faPaiRemainThree(PokerOverlay[] remainThree) {
        List<PokerOverlay> remainThreeCopy = new ArrayList<PokerOverlay>(3);

        for (int i = 0; i < remainThree.length; i++) {
            //pokerOverlay.enableHover();
            remainThreeCopy.add(remainThree[i]);
        }

        shouPai.addAll(remainThreeCopy);


        Collections.sort(remainThreeCopy);
        Collections.sort(shouPai);

        for (int i = 2; i >= 0; i--) {
            PokerOverlay newPoker = remainThreeCopy.get(i);
            int sortIndex = Collections.binarySearch(shouPai, newPoker);
            if (sortIndex == shouPai.size() - 1) {
                gameScene.addChild(newPoker);
            }
            else {
                PokerOverlay prePoker = shouPai.get(sortIndex + 1);
                int insertIndex = gameScene.indexOf(prePoker) - 1;
                //gameScene.addChild(insertIndex, newPoker);
                gameScene.addChild(newPoker);
            }

            Point point = posProvider.getShouPaiPos(sortIndex, 20);


            newPoker.startAnimation(new TranslateAnimation(1000, point.x - newPoker.getPosX(), point.y - newPoker.getPosY()));

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

    public void handlerSelectPokers(MotionEvent event) {

        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        selectEndPoint.set(x, y);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                selectStartPoint.set(x, y);
                selectedPokerOverlays.clear();
                break;

            case MotionEvent.ACTION_MOVE:
                selectedPokerOverlays.clear();

                selectRegion.set(
                        Math.min(selectStartPoint.x, selectEndPoint.x),
                        Math.min(selectStartPoint.y, selectEndPoint.y),
                        Math.max(selectStartPoint.x, selectEndPoint.x),
                        Math.max(selectStartPoint.y, selectEndPoint.y)
                );

                int pokerWidth = LoadRes.getPokerWidth();
                int pokerHeight = LoadRes.getPokerHeight();

                Rect pokerBounds = new Rect(-pokerWidth / 2, pokerHeight / 2, pokerWidth / 2, -pokerHeight / 2);

                for (int i = shouPai.size() - 1; i >= 0; i--) {

                    PokerOverlay pokerOverlay = shouPai.get(i);

                    pokerBounds.offsetTo(
                            (int) pokerOverlay.getPosX(),
                            (int) pokerOverlay.getPosY()
                    );

                    boolean regionTest = selectRegion.intersect(pokerBounds);

                    if (regionTest)
                        selectedPokerOverlays.add(pokerOverlay);

                    pokerOverlay.setSelected(regionTest);
                }

                break;
            case MotionEvent.ACTION_UP:

                PokerOverlay pokerOverlay = findFromRightToLeft(x, y);
                if (pokerOverlay != null && !selectedPokerOverlays.contains(pokerOverlay)) {
                    selectedPokerOverlays.add(pokerOverlay);
                }

                boolean containsSome = false;
                boolean doNotContainAll = false;
                if (selectedPokerOverlays.size() > 1) {
                    for (PokerOverlay overlay : selectedPokerOverlays) {
                        if (selectedPokers.contains(overlay.getColoredPoker())) {
                            containsSome = true;
                        }
                        else {
                            doNotContainAll = true;
                        }

                        if (containsSome && doNotContainAll) {
                            selectedPokerOverlays.clear();
                            selectedPokers.clear();
                            break;
                        }
                    }
                }
                else {

                }

                postSelect(selectedPokerOverlays, containsSome && doNotContainAll);
                break;
        }

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

        PokerOverlay pokerOverlay = findFromRightToLeft(startX, startY);
        if (pokerOverlay != null) {
            for (ColoredPoker selectedPokerTile : selectedPokers) {
                if (pokerOverlay.getColoredPoker().getColor() == null)
                    return pokerOverlay.getColoredPoker().getPoker() == selectedPokerTile.getPoker();
                if (pokerOverlay.getColoredPoker().getColor() == selectedPokerTile.getColor()
                        && pokerOverlay.getColoredPoker().getPoker() == selectedPokerTile.getPoker())
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

        for (PokerOverlay pokerOverlay : shouPai) {
            ColoredPoker tilePoker = pokerOverlay.getColoredPoker();

            if (pokers.contains(tilePoker) && pokerOverlay.getPosY() == SHOU_PAI_Y) {
                pokerOverlay.standUp();
            }

        }
    }

    public Set<PokerOverlay> getSelectedPokerOverlays() {
        return selectedPokerOverlays;
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

    private void postSelect(Set<PokerOverlay> multiSelection, boolean tidyShouPai) {

        if (multiSelection.size() == 0) {
            if (tidyShouPai) //multiSelection size =0 点击非牌区域时也为0，此时不应该清掉选择的牌
                tidyShouPai(100);
        }
        else {
            for (PokerOverlay pokerOverlay : multiSelection) {
                ColoredPoker coloredPoker = pokerOverlay.getColoredPoker();

                if (selectedPokers.contains(coloredPoker)) {
                    selectedPokers.remove(coloredPoker);
                    pokerOverlay.sitDown();
                }
                else {
                    selectedPokers.add(coloredPoker);
                    pokerOverlay.standUp();
                }
            }
        }

        for (PokerOverlay pokerOverlay : shouPai) {
            pokerOverlay.setSelected(false);
        }
    }

    private PokerOverlay findFromRightToLeft(int x, int y) {
        int size = shouPai.size();

        for (int i = size - 1; i >= 0; i--) {

            PokerOverlay pokerOverlay = shouPai.get(i);

            if (pokerOverlay.hitTest(x, y))
                return pokerOverlay;

        }

        return null;
    }

    private Point calShouPaiPos(int index, int size) {
        int totalWidth = (size - 1) * SHOU_PAI_MARGIN;
        int startX = (EngineContext.getAppWidth() - totalWidth) / 2;
        return new Point(startX + index * SHOU_PAI_MARGIN, SHOU_PAI_Y);
    }

    private Point calChuPaiPos(int index, int size) {
        int totalWidth = (size - 1) * CHU_PAI_MARGIN;
        int startX = (EngineContext.getAppWidth() - totalWidth) / 2;
        return new Point(startX + index * CHU_PAI_MARGIN, CHU_PAI_Y);
    }

    public void clearSelectedPokers() {
        for (PokerOverlay pokerOverlay : selectedPokerOverlays) {
            pokerOverlay.setSelected(false);
        }

        selectedPokerOverlays.clear();
        selectedPokers.clear();
    }

    private Rect selectRegion;
    private Point selectStartPoint, selectEndPoint;
    private Set<PokerOverlay> selectedPokerOverlays;
    private SlideLineTool slideLineTool;

    private ColoredHand validatedHand;
    private List<ColoredPoker> selectedPokers;


}
