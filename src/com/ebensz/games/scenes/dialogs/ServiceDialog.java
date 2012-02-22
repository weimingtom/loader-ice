package com.ebensz.games.scenes.dialogs;

import com.ebensz.games.R;
import ice.animation.AlphaAnimation;
import ice.animation.Animation;
import ice.animation.RotateAnimation;
import ice.animation.TranslateAnimation;
import ice.engine.EngineContext;
import ice.graphic.texture.Texture;
import ice.node.Overlay;
import ice.node.mesh.Grid;
import ice.node.widget.BitmapOverlay;
import ice.node.widget.ButtonOverlay;
import ice.node.widget.ConfirmDialog;
import ice.practical.ComesMoreTextBox;

/**
 * User: jason
 * Date: 12-1-12
 * Time: 下午5:28
 */
public class ServiceDialog extends ConfirmDialog {

    public ServiceDialog() {
        super(EngineContext.getAppWidth(), 150);
    }


    public void showMsg(String oneLine) {
        comesMoreTextBox.setTexts(new String[]{oneLine});
    }

    public void showMsg(String[] lines) {
        comesMoreTextBox.setTexts(lines, 20);
    }

    @Override
    protected void onSetupComponent() {
        float width = getWidth();
        float height = getHeight();
        bg = new BitmapOverlay(width, height, R.drawable.server_bg);

        bg.getTexture().setParams(Texture.Params.LINEAR_REPEAT);

        bg.setPos(width / 2, height / 2);

        comesMoreTextBox = new ComesMoreTextBox(800, 30, 1000);

        comesMoreTextBox.setPos(
                width / 2,
                getHeight() - 30
        );

        girl = new BitmapOverlay(R.drawable.service_girl);
        girl.setPos(girl.getWidth() / 2, girl.getHeight() / 2);

        confirmButton = new ButtonOverlay(R.drawable.start_game, R.drawable.start_game_press);

        confirmButton.setPos(width / 2, 50);

        confirmButton.setVisible(false);

        addChildren(bg, comesMoreTextBox, girl, confirmButton);
    }

    public void startEntryAnimation(final String welcomeText) {

        TranslateAnimation translate = new TranslateAnimation(1000, -getWidth(), 0, 0, 0);

        translate.setListener(new Animation.Listener() {
            @Override
            public void onAnimationEnd(Overlay tile) {
                comesMoreTextBox.setTexts(new String[]{welcomeText});
                comesMoreTextBox.startAnimation(AlphaAnimation.createFadeIn(1000));
            }
        });

        bg.startAnimation(translate);

        RotateAnimation rotate = new RotateAnimation(1000, -90, 0);
        rotate.setCenterOffset(0, -girl.getHeight(), 0);
        girl.setVisible(false);
        girl.startAnimation(rotate);
    }

    private BitmapOverlay girl;
    private Grid bg;
    private ComesMoreTextBox comesMoreTextBox;
}
