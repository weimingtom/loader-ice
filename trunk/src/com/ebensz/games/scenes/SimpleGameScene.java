package com.ebensz.games.scenes;

import android.graphics.Bitmap;
import android.graphics.Point;
import com.ebensz.games.R;
import com.ebensz.games.logic.story.Game;
import ice.engine.EngineContext;
import ice.node.widget.ButtonOverlay;
import ice.res.Res;

/**
 * User: Mike.Hu
 * Date: 11-11-28
 * Time: 下午4:54
 */
public class SimpleGameScene extends GameScene {
    private static final String TAG = SimpleGameScene.class.getSimpleName();

    public SimpleGameScene(Game game) {
        super(game);
    }

    public void showSelectContinueGame() {
        Bitmap normal = Res.getBitmap(R.drawable.start_game);
        Bitmap pressed = Res.getBitmap(R.drawable.start_game_press);

        Point pos = new Point(
                EngineContext.getAppWidth() / 2,
                230
        );

        selectContinueGameBtn = new ButtonOverlay(normal, pressed);
        selectContinueGameBtn.setPos(pos.x, pos.y);
        addChild(selectContinueGameBtn);
    }

    public ButtonOverlay getSelectContinueGameBtn() {
        return selectContinueGameBtn;
    }

    private ButtonOverlay selectContinueGameBtn;
}
