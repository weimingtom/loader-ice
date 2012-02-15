package com.ebensz.games.utils;

/**
 * User: ebensz
 * Date: 11-5-26
 */
public abstract class SleepUtils {

    public static final float FASTER = 0.4f;
    public static final float DEFAULT_SPEED = 1f;
    public static final float SLOWER = 1.5f;

    private SleepUtils() {
    }

    public static boolean sleep(long millsSeconds) {

        try {
            Thread.sleep((long) (millsSeconds * speed));
        }
        catch (InterruptedException e) {
            return false;
        }

        return true;
    }

    public static void setSpeed(float speed) {
        SleepUtils.speed = speed;
    }

    public static float getSpeed() {
        return speed;
    }

    private static float speed = DEFAULT_SPEED;
}
