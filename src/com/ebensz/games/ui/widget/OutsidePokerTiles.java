package com.ebensz.games.ui.widget;

import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import com.ebensz.games.model.hand.ColoredHand;
import com.ebensz.games.model.poker.ColoredPoker;
import com.ebensz.games.model.poker.Poker;
import com.ebensz.games.utils.SlideLineTool;
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
    public static final int SHOU_PAI_Y = 80;
    private static final int CHU_PAI_Y = 400;
    private static final int CHU_PAI_MARGIN = 30;
    public static final int STAND_UP_Y = 30;

    public OutsidePokerTiles() {
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

    @Override
    public void faPai(ColoredPoker coloredPoker) {
        PokerOverlay poker = new PokerOverlay(coloredPoker);
        Point point = posProvider.getShouPaiPos(size(), 17);
        poker.setPos(point.x, point.y, size() * 0.2f);

        addChild(poker);
    }

    @Override
    public void faPaiRemainThree(List<ColoredPoker> remainThree) {
        for (ColoredPoker coloredPoker : remainThree) {
            PokerOverlay poker = new PokerOverlay(coloredPoker);
            Point point = posProvider.getShouPaiPos(size(), 20);
            poker.setPos(point.x, point.y, size() * 0.2f);
            addChild(poker);
        }

        tidyShouPai(300);
    }

    @Override
    public void showChuPai(ColoredHand chuPai) {
        sortChuPai(chuPai);

        List<ColoredPoker> coloredPokers = chuPai.getColoredPokers();

        for (int i = 0, size = size(); i < size; i++) {
            PokerOverlay pokerOverlay = (PokerOverlay) get(i);
            if (coloredPokers.contains(pokerOverlay.getColoredPoker())) {
                this.chuPai.add(pokerOverlay);
            }

        }

        for (int i = 0, size = this.chuPai.size(); i < size; i++) {
            PokerOverlay poker = this.chuPai.get(i);

            Point point = posProvider.getChuPaiPos(i, size);

            poker.startAnimation(
                    new TranslateAnimation(
                            500,
                            point.x - poker.getPosX(),
                            point.y - poker.getPosY()
                    )
            );
        }

        List<Overlay> children = getChildren();
        List<Overlay> copy = new ArrayList<Overlay>(children);
        copy.removeAll(this.chuPai);

        tidyShouPai(100, copy);
    }

    private void sortChuPai(ColoredHand chuPai) {
        Poker[] pokers = chuPai.getHand().getPokers();

        List<ColoredPoker> coloredPokers = chuPai.getColoredPokers();
        Collections.sort(coloredPokers);

        List<ColoredPoker> coloredPokersCopy = new ArrayList<ColoredPoker>(coloredPokers.size());

        for (Poker poker : pokers) {
            ColoredPoker coloredPoker = popColoredPoker(poker, coloredPokers);
            coloredPokersCopy.add(coloredPoker);
        }

        chuPai.setColoredPokers(coloredPokersCopy);
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
                updateSelect();
                break;
            case MotionEvent.ACTION_UP:
                doFilter();
                postSelect(selectedPokerOverlays);
                break;
        }

    }

    private void doFilter() {
        if (selectedPokerOverlays.size() <= 1) return;

        boolean containsSome = false;
        boolean doNotContainAll = false;

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

                tidyShouPai(50);
                break;
            }
        }
    }

    private void updateSelect() {
        if (!validY()) return;

        for (int i = size() - 1; i >= 0; i--) {

            PokerOverlay pokerTile = (PokerOverlay) get(i);

            boolean regionTest = selectRegionTest(i, pokerTile);

            if (regionTest) {
                selectedPokerOverlays.add(pokerTile);
            }
            else {
                selectedPokerOverlays.remove(pokerTile);
            }

            pokerTile.setSelected(regionTest);
        }
    }

    private boolean validY() {
        for (int i = size() - 1; i >= 0; i--) {

            PokerOverlay pokerTile = (PokerOverlay) get(i);

            float halfPokerHeight = pokerTile.getHeight() / 2;
            float posY = pokerTile.getPosY();

            if (selectRegion.top >= posY - halfPokerHeight && selectRegion.bottom <= posY + halfPokerHeight)
                return true;
        }

        return false;
    }

    private boolean selectRegionTest(int i, PokerOverlay pokerTile) {
        float pokerTileX = pokerTile.getPosX();
        float halfPokerWidth = pokerTile.getWidth() / 2;

        float pokerLeft = pokerTileX - halfPokerWidth;

        if (i == size() - 1) {
            float pokerRight = pokerTileX + halfPokerWidth;

            return (pokerLeft >= selectRegion.left && pokerLeft <= selectRegion.right)
                    || (pokerRight >= selectRegion.left && pokerRight <= selectRegion.right)
                    || (pokerLeft <= selectRegion.left && pokerRight >= selectRegion.right);
        }
        else {
            return pokerLeft + SHOU_PAI_MARGIN >= selectRegion.left
                    && pokerLeft <= selectRegion.right;
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

//        for (PokerOverlay pokerOverlay : shouPai) {
//            ColoredPoker tilePoker = pokerOverlay.getColoredPoker();
//
//            if (pokers.contains(tilePoker) && pokerOverlay.getPosY() == SHOU_PAI_Y) {
//                pokerOverlay.standUp();
//            }
//
//        }
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

    private void postSelect(Set<PokerOverlay> multiSelection) {

        for (int i = 0; i < size(); i++) {
            PokerOverlay overlay = (PokerOverlay) get(i);
            overlay.setSelected(false);
        }

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

    public void tidyShouPai(long time, List<Overlay> pokers) {
        for (int i = 0, size = pokers.size(); i < size; i++) {
            Point point = posProvider.getShouPaiPos(i, size);
            Overlay overlay = pokers.get(i);

            overlay.startAnimation(
                    new TranslateAnimation(
                            time,
                            point.x - overlay.getPosX(),
                            point.y - overlay.getPosY()
                    )
            );
        }
    }

    public void tidyShouPai(long time) {
        tidyShouPai(time, getChildren());
    }

    private PokerOverlay findFromRightToLeft(int x, int y) {
        int size = size();

//        for (int i = size - 1; i >= 0; i--) {
//
//            PokerOverlay pokerOverlay = shouPai.get(i);
//
//            if (pokerOverlay.hitTest(x, y))
//                return pokerOverlay;
//
//        }

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
