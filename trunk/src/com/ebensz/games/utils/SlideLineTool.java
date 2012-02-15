package com.ebensz.games.utils;

/**
 * User: Mike.Hu
 * Date: 12-2-7
 * Time: 下午4:55
 */
public class SlideLineTool {

    public static final int MIN_SLIDE_Y_LENGTH = 100;
    public static final int MOVE_ERROR = 100;

    public SlideLineTool() {

        for (int i = 0; i < 15; i++) {

            moveX[i] = 0;
            moveY[i] = 0;
            moveTime[i] = 0;
        }
    }

    public boolean isSlideLine(int validCount) {

        if (validCount <= 3)
            return false;

        if (!checkValidSpace(validCount))
            return false;

        return isOneLine(validCount);
    }

    private boolean isOneLine(int validCount) {

        int validIndex = validCount - 1;
        int lastMoveX = moveX[0];
        int lastMoveY = moveY[0];

        int intervalY = lastMoveY - moveY[validIndex];
        int intervalX = lastMoveX - moveX[validIndex];

        if (intervalX == 0) {
            for (int i = 0; i < validCount; i++) {
                if (Math.abs(moveX[i] - lastMoveX) > MOVE_ERROR)
                    return false;
            }
        }
        else {
            int distanceY = (int) (MOVE_ERROR * Math.abs(intervalY) / (Math.sqrt((double) (intervalY * intervalY + intervalX * intervalX))));
            float ascent = intervalY / intervalX;
            int intercept = lastMoveY - (int) (lastMoveX * ascent);

            for (int i = 0; i < validCount; i++) {
                int standardY = (int) (moveX[i] * ascent) + intercept;
                if (moveY[i] > (standardY + distanceY) || moveY[i] < (standardY - distanceY))
                    return false;
            }
        }
        return true;
    }

    private boolean checkValidSpace(int validCount) {

        int validIndex = validCount - 1;

        if (Math.abs(moveY[validIndex] - moveY[0]) < MIN_SLIDE_Y_LENGTH)
            return false;
        else {
            if (Math.abs(moveY[validIndex] - moveY[0]) < Math.abs(moveX[validIndex] - moveX[0]))
                return false;
        }

        return true;
    }


    public int getValidCount() {

        long lastTime = moveTime[0];
        for (int i = 1; i < 15; i++) {
            if (lastTime - moveTime[i] > 400)
                return i + 1;
        }
        return 15;
    }

    public void addData(int x, int y, long time) {

        for (int i = 14; i > 0; i--) {
            moveX[i] = moveX[i - 1];
            moveY[i] = moveY[i - 1];
            moveTime[i] = moveTime[i - 1];
        }
        moveX[0] = x;
        moveY[0] = y;
        moveTime[0] = time;
    }

    public int getValidStartX(int validCount) {

        return moveX[validCount - 1];
    }

    public int getValidStartY(int validCount) {

        return moveY[validCount - 1];
    }

    int moveX[] = new int[15];
    int moveY[] = new int[15];
    long moveTime[] = new long[15];
}
