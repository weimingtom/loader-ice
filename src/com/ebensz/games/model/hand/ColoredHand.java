package com.ebensz.games.model.hand;

import com.ebensz.games.model.poker.ColoredPoker;

import java.util.List;

/**
 * User: Mike.Hu
 * Date: 11-12-22
 * Time: 下午5:19
 */
public class ColoredHand {

    public static final ColoredHand BU_YAO = null;

    public ColoredHand(Hand hand, List<ColoredPoker> coloredPokers) {
        this.hand = hand;
        this.coloredPokers = coloredPokers;
    }

    public Hand getHand() {
        return hand;
    }

    public List<ColoredPoker> getColoredPokers() {
        return coloredPokers;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    public void setColoredPokers(List<ColoredPoker> coloredPokers) {
        this.coloredPokers = coloredPokers;
    }

    @Override
    public String toString() {
        return "ColoredHand{" +
                "hand=" + hand +
                '}';
    }

    private Hand hand;
    private List<ColoredPoker> coloredPokers;
}
