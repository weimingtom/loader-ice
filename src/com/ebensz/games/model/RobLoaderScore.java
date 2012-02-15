package com.ebensz.games.model;

/**
 * User: Mike.Hu
 * Date: 11-12-1
 * Time: 下午2:53
 * <p/>
 * 叫分分数.
 */
public enum RobLoaderScore {
    Pass, One, Two, Three;
    private int multiple;

    public boolean isHigherThan(RobLoaderScore otherScore) {

        return this.ordinal() - otherScore.ordinal() > 0;
    }

    public int getMultiple() {

        switch (this) {
            case One:
                return 1;
            case Two:
                return 2;
            case Three:
                return 3;
            default:
                return -1;
        }
    }
}
