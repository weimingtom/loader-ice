package com.ebensz.games.model.poker;

import static com.ebensz.games.model.poker.Poker.*;

/**
 * User: Mike.Hu
 * Date: 11-12-6
 * Time: 下午3:13
 */
public class ColoredPoker implements Comparable<ColoredPoker> {

    public enum Color {
        /**
         * 方块
         */
        Diamond,
        /**
         * 梅花
         */
        Club,
        /**
         * 黑桃
         */
        Spade,
        /**
         * 红心
         */
        Heart;
    }

    public ColoredPoker(Poker poker, Color color) {
        if (poker == null || color == null)
            throw new IllegalArgumentException("null error ");

        if (poker.isBiggerThan(_2) && color != null)
            throw new IllegalArgumentException("Poker color error " + color);

        this.poker = poker;
        this.color = color;
    }

    public ColoredPoker(boolean xiaoWang) {
        poker = xiaoWang ? XiaoWang : DaWang;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColoredPoker that = (ColoredPoker) o;

        if (color != that.color) return false;
        if (poker != that.poker) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = color != null ? color.hashCode() : 0;
        result = 31 * result + (poker != null ? poker.hashCode() : 0);
        return result;
    }

    public Poker getPoker() {
        return poker;
    }

    public void setPoker(Poker poker) {
        this.poker = poker;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public int compareTo(ColoredPoker another) {

        int pokerSub = another.poker.ordinal() - poker.ordinal();
        if (pokerSub != 0)
            return pokerSub;

        if (color == null) return 0;

        return another.color.ordinal() - color.ordinal();
    }

    @Override
    public String toString() {
        return color == null
                ? poker.toString()
                : color.toString() + poker;
    }

    private Color color;
    private Poker poker;
}
