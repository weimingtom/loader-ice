package com.ebensz.games.scenes.dialogs;

import android.graphics.Bitmap;
import android.graphics.Color;
import com.ebensz.games.R;
import com.ebensz.games.scenes.AnimationTextBox;
import ice.animation.Animation;
import ice.animation.RotateAnimation;
import ice.animation.TranslateAnimation;
import ice.engine.EngineContext;
import ice.node.Drawable;
import ice.node.widget.Button;
import ice.node.widget.ColorBlock;
import ice.node.widget.ConfirmDialog;
import ice.node.widget.TextureGrid;

import java.util.List;

/**
 * User: jason
 * Date: 12-1-12
 * Time: 下午5:28
 */
public class ServiceDialog extends ConfirmDialog {

    public ServiceDialog() {
        setPos(0, EngineContext.getAppHeight() * 0.2f);
    }

    public void showMsg(String oneLine) {
        animationTextBox.setText(oneLine);
    }

    public void showMsg(List<String> lines) {
        animationTextBox.setTexts(lines, 20);
    }

    @Override
    protected void onSetupComponent() {
        float width = getWidth();
        float height = getHeight();
        colorBg = new ColorBlock(Color.RED, width, height);

        animationTextBox = new AnimationTextBox(800, 30, 1000);

        animationTextBox.setPos(
                200,
                50
        );

        girl = new TextureGrid(R.drawable.service_girl);

        confirmButton = new Button(R.drawable.start_game, R.drawable.start_game_press);

        confirmButton.setPos(
                (width - confirmButton.getWidth()) / 2,
                130
        );

        confirmButton.setVisible(false);

        addChildren(colorBg, animationTextBox, girl, confirmButton);
    }

    public void startEntryAnimation(final String welcomeText) {

        TranslateAnimation translate = new TranslateAnimation(200, -getWidth(), 0, 0, 0);

        translate.setListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Drawable tile) {
                animationTextBox.setText(welcomeText);
                //animationTextBox.startAnimation(new FadeInAnimation(500));
            }
        });

        colorBg.startAnimation(translate);

        RotateAnimation rotate = new RotateAnimation(500, 90, 0);
        rotate.setCenterOffset(girl.getWidth() / 2, girl.getHeight(), 0);
        // rotate.setOffsetTime(200);
        girl.setVisible(false);
        girl.startAnimation(rotate);
    }

    private TextureGrid girl;
    private ColorBlock colorBg;
    private AnimationTextBox animationTextBox;
}
