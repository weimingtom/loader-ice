package com.ebensz.games.scenes.dialogs;

import android.graphics.Bitmap;
import android.graphics.Color;
import com.ebensz.games.R;
import ice.animation.AlphaAnimation;
import ice.animation.Animation;
import ice.animation.RotateAnimation;
import ice.animation.TranslateAnimation;
import ice.engine.EngineContext;
import ice.node.Overlay;
import ice.node.widget.BitmapOverlay;
import ice.node.widget.ButtonOverlay;
import ice.node.widget.ColorOverlay;
import ice.node.widget.ConfirmDialog;
import ice.practical.ComesMoreTextBox;
import ice.res.Res;

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
        colorBg = new ColorOverlay(Color.RED, width, height);

        comesMoreTextBox = new ComesMoreTextBox(800, 20, 1000);

        comesMoreTextBox.setPos(
                100,
                0
        );

        Bitmap girlBitmap = Res.getBitmap(R.drawable.service_girl);
        girl = new BitmapOverlay(girlBitmap.getWidth() / 2, girlBitmap.getHeight() / 2, girlBitmap);

        confirmButton = new ButtonOverlay(R.drawable.start_game, R.drawable.start_game_press);

        confirmButton.setPos(
                (width - confirmButton.getWidth()) / 2,
                130
        );

        confirmButton.setVisible(false);

        addChildren(colorBg, comesMoreTextBox, girl, confirmButton);
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

        colorBg.startAnimation(translate);

        RotateAnimation rotate = new RotateAnimation(1000, -90, 0);
        rotate.setCenterOffset(girl.getWidth() / 2, -girl.getHeight(), 0);
        // rotate.setOffsetTime(200);
        girl.setVisible(false);
        girl.startAnimation(rotate);
    }

    private BitmapOverlay girl;
    private ColorOverlay colorBg;
    private ComesMoreTextBox comesMoreTextBox;
}
