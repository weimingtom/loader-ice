package com.ebensz.games.ui.widget;

import android.graphics.Point;
import com.ebensz.games.R;
import com.ebensz.games.model.hand.ColoredHand;
import com.ebensz.games.model.poker.ColoredPoker;
import ice.animation.AlphaAnimation;
import ice.graphic.texture.Texture;
import ice.node.widget.AtlasSequence;

import java.util.List;

/**
 * User: jason
 * Date: 12-3-1
 * Time: 上午11:11
 */
public abstract class LeftRightBase extends DirPokerTiles {

    @Override
    protected DirPositionProvider onCreatePosProvider() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void faPai(ColoredPoker coloredPoker) {
        if (remainingPokerNum == null) {
            remainingPokerNum = new AtlasSequence(50, 40, 20);
            remainingPokerNum.setAtlas(new Texture(R.drawable.digit_positive), 11, 1);
            Point shouPaiPos = posProvider.getShouPaiPos(pokerNum, 17);
            remainingPokerNum.setPos(shouPaiPos.x, shouPaiPos.y);
            addChild(remainingPokerNum);
        }
        else {
            remainingPokerNum.resetSequence();
        }

        pokerNum++;
        updatePokerNum(pokerNum);
    }

    @Override
    public void faPaiRemainThree(List<PokerOverlay> remainThree) {
        pokerNum += 3;

        updatePokerNum(pokerNum);
    }

    private void updatePokerNum(int pokerNum) {

        char[] digits = String.format("%02d", pokerNum).toCharArray();

        int[] sequence = new int[digits.length];

        for (int i = 0; i < sequence.length; i++) {
            sequence[i] = digits[sequence.length - i - 1] - '0' + 1;
        }

        remainingPokerNum.setSequence(sequence);
    }

    @Override
    public void reset() {
        if (remainingPokerNum != null)
            remainingPokerNum.resetSequence();
    }

    @Override
    public void showChuPai(ColoredHand chuPai) {
        List<ColoredPoker> coloredPokers = chuPai.getColoredPokers();

        for (int i = 0, size = coloredPokers.size(); i < size; i++) {

            ColoredPoker coloredPoker = coloredPokers.get(i);

            PokerOverlay poker = new PokerOverlay(coloredPoker);

            Point point = posProvider.getChuPaiPos(i, coloredPokers.size());
            poker.setPos(point.x, point.y, size() * 0.2f);

            addChild(poker);
            this.chuPai.add(poker);

            poker.startAnimation(AlphaAnimation.createFadeIn(100));
        }

        pokerNum -= coloredPokers.size();
        updatePokerNum(pokerNum);
    }

    private int pokerNum;
    private AtlasSequence remainingPokerNum;
}
