package com.ebensz.games.model.values;

/**
 * User: tosmart
 * Date: 11-6-16
 * Time: 上午11:19
 */
public class LevelDefine {

    public static class Range {

        public Range(int from, int to, String name) {
            this.from = from;
            this.to = to;
            this.name = name;
        }

        public boolean inRange(int exp) {
            return exp >= from && exp <= to;
        }

        public int getDistance() {
            return to - from + 1;
        }

        public int getFrom() {
            return from;
        }

        public int getTo() {
            return to;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private int from, to;
        private String name;
    }

    public static Range[] ranges = {
    	new Range(0, 999, "一品"),
    	new Range(1000, 2999, "二品"),
    	new Range(3000, 4999, "三品"),
    	new Range(5000, 9999, "四品"),
    	new Range(10000, 29999, "五品"),
    	new Range(30000, 79999, "六品"),
    	new Range(80000, 149999, "七品"),
    	new Range(150000, 299999, "八品"),
    	new Range(300000, 599999, "九品"),
    	new Range(600000, 0x7FFFFFFF, "极品"),
    };

    public static int getLevel(int exp) {

        int levelCount = ranges.length;

        for (int i = 0; i < levelCount; i++) {
            Range range = ranges[i];
            if (range.inRange(exp)) return i;
        }

        return 0;
    }

    public static String getLevelName(int level) {
        if (level < 0 || level >= ranges.length) return "";
        return ranges[level].getName();
    }

    public static int getUpgradePercent(int exp) {

        final int level = getLevel(exp);

        final Range range = ranges[level];
        final int moreExp = exp - range.getFrom();
        final int distance = range.getDistance();

        return moreExp * 100 / distance;
    }
}
