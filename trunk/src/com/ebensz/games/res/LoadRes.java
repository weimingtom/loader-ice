package com.ebensz.games.res;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.ebensz.games.R;
import com.ebensz.games.model.poker.ColoredPoker;
import com.ebensz.games.model.poker.Poker;
import ice.res.Res;
import ice.util.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import static com.ebensz.games.model.poker.Poker.*;

/**
 * User: Mike.Hu
 * Date: 11-11-30
 * Time: 下午2:56
 */
public class LoadRes extends Res {
    private static final int HEAD_ICON_COUNT = 32;

    private static List<Bitmap> headIcons;

    public static final int[] POKER_RES_IDS = {
            R.drawable.pai_30, R.drawable.pai_31, R.drawable.pai_32, R.drawable.pai_33,
            R.drawable.pai_40, R.drawable.pai_41, R.drawable.pai_42, R.drawable.pai_43,
            R.drawable.pai_50, R.drawable.pai_51, R.drawable.pai_52, R.drawable.pai_53,
            R.drawable.pai_60, R.drawable.pai_61, R.drawable.pai_62, R.drawable.pai_63,
            R.drawable.pai_70, R.drawable.pai_71, R.drawable.pai_72, R.drawable.pai_73,
            R.drawable.pai_80, R.drawable.pai_81, R.drawable.pai_82, R.drawable.pai_83,
            R.drawable.pai_90, R.drawable.pai_91, R.drawable.pai_92, R.drawable.pai_93,
            R.drawable.pai_100, R.drawable.pai_101, R.drawable.pai_102, R.drawable.pai_103,

            R.drawable.pai_j0, R.drawable.pai_j1, R.drawable.pai_j2, R.drawable.pai_j3,
            R.drawable.pai_q0, R.drawable.pai_q1, R.drawable.pai_q2, R.drawable.pai_q3,
            R.drawable.pai_k0, R.drawable.pai_k1, R.drawable.pai_k2, R.drawable.pai_k3,
            R.drawable.pai_a0, R.drawable.pai_a1, R.drawable.pai_a2, R.drawable.pai_a3,
            R.drawable.pai_20, R.drawable.pai_21, R.drawable.pai_22, R.drawable.pai_23,
            R.drawable.pai_g0, R.drawable.pai_g1,
    };

    public static Bitmap getFrontPoker(ColoredPoker coloredPoker) {
        int pokerResId = -1;

        Poker poker = coloredPoker.getPoker();

        if (poker == XiaoWang) {
            pokerResId = POKER_RES_IDS[POKER_RES_IDS.length - 2];
        }
        else if (poker == DaWang) {
            pokerResId = POKER_RES_IDS[POKER_RES_IDS.length - 1];
        }
        else {
            ColoredPoker.Color color = coloredPoker.getColor();
            pokerResId = POKER_RES_IDS[poker.ordinal() * 4 + color.ordinal()];
        }

        return getBitmap(pokerResId);
    }

    public static Bitmap createDigitBitmap(int digit, boolean withPlus) {

        final boolean isNegative = digit < 0;

        String strDigit = String.valueOf(digit);

        if (withPlus && !isNegative)
            strDigit = "+" + strDigit;

        final Bitmap[] letters = Res.loadArray(isNegative ? R.drawable.digit_negative : R.drawable.digit_positive, 11);
        final Bitmap template = letters[0];

        int letterWidth = template.getWidth();
        int letterHeight = template.getHeight();

        final char[] chars = strDigit.toCharArray();
        int letterCount = chars.length;

        Bitmap bitmap = Bitmap.createBitmap(letterWidth * letterCount, letterHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        for (int i = 0; i < letterCount; i++) {

            int index = translateDigitIndex(chars[i]);
            Bitmap letter = letters[index];

            canvas.drawBitmap(letter, i * letterWidth, 0, null);
        }

        return bitmap;
    }

    public static Bitmap createDigitBitmap(int digit) {
        return createDigitBitmap(digit, true);
    }

    public static int getOutsidePokerWidth() {
        return getBitmap(R.drawable.pai_30).getWidth();
    }

    public static int getOutsidePokerHeight() {
        return getBitmap(R.drawable.pai_30).getHeight();
    }

    public static int getLeftShouPaiWidth() {
        return getBitmap(R.drawable.poker_back_small).getWidth();
    }

    public static int getLeftShouPaiHeight() {
        return getBitmap(R.drawable.poker_back_small).getHeight();
    }

    public static List<Bitmap> getHeadIcons() {

        if (headIcons == null) {
            loadHeadIconBitmaps();
        }

        return headIcons;
    }

    private static void loadHeadIconBitmaps() {

        Bitmap icons = Res.getBitmap(R.drawable.head_icons);

        float iconWidth = icons.getWidth() * 1f / HEAD_ICON_COUNT;
        int iconHeight = icons.getHeight();

        headIcons = new ArrayList<Bitmap>();
        for (int i = 0; i < HEAD_ICON_COUNT; i++) {

            Bitmap slices = BitmapUtils.slices(
                    icons,
                    (int) (i * iconWidth),
                    0,
                    (int) iconWidth,
                    iconHeight
            );

            headIcons.add(slices);
        }
    }

    private static int translateDigitIndex(char c) {

        switch (c) {

            case '+':
            case '-':
                return 0;

            default:
                return c - '0' + 1;
        }
    }

}
