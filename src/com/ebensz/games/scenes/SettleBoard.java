package com.ebensz.games.scenes;

import android.graphics.*;
import com.ebensz.games.R;
import com.ebensz.games.logic.settle.SettleTool;
import com.ebensz.games.model.Dir;
import com.ebensz.games.model.Role.Role;
import ice.engine.EngineContext;
import ice.node.widget.BitmapOverlay;
import ice.res.Res;
import ice.util.TextDrawer;

/**
 * User: jason
 * Date: 12-2-13
 * Time: 下午4:37
 */
public class SettleBoard extends BitmapOverlay {

    private static final int NAME_WIDTH = 160;
    private static final int LINE_HEIGHT = 26;
    private static final int SCORE_WIDTH = 185;
    private static final int LINES_MARGIN = 5;

    private static final int NAME_START_X = 25;
    private static final int LINE_START_Y = 126;
    private static final int SCORE_START_X = 195;

    public SettleBoard(SettleTool.Result result) {
        super(fillTextInfo(result));

        setPos((EngineContext.getAppWidth() - getWidth()) / 2, (EngineContext.getAppHeight() + getHeight()) / 2);
    }

    private static Bitmap fillTextInfo(SettleTool.Result result) {
        Bitmap bg = Res.getBitmap(R.drawable.settle_board);

        Bitmap board = Bitmap.createBitmap(bg.getWidth(), bg.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(board);

        canvas.drawBitmap(bg, 0, 0, null);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.YELLOW);

        Rect region = new Rect(0, 0, NAME_WIDTH, LINE_HEIGHT);
        region.offset(NAME_START_X, LINE_START_Y);

        for (Dir dir : Dir.values()) {
            Role role = result.roleMap.get(dir);

            TextDrawer.drawTextInLine(canvas, paint, role.getName(), region, true);

            region.offset(0, LINES_MARGIN + LINE_HEIGHT);
        }

        region.set(0, 0, SCORE_WIDTH, LINE_HEIGHT);
        region.offset(SCORE_START_X, LINE_START_Y);

        for (Dir dir : Dir.values()) {
            int score = result.winScores.get(dir);
            TextDrawer.drawTextInLine(canvas, paint, "" + score, region, true);
            region.offset(0, LINES_MARGIN + LINE_HEIGHT);
        }

        return board;
    }
}
