package com.ebensz.games.scenes;

import com.ebensz.games.R;
import com.ebensz.games.model.poker.ColoredPoker;
import com.ebensz.games.model.poker.Poker;
import com.ebensz.games.scenes.dialogs.ServiceDialog;
import com.ebensz.games.ui.widget.PokerOverlay;
import ice.animation.Interpolator.LinearInterpolator;
import ice.animation.RotateAnimation;
import ice.engine.Scene;
import ice.node.widget.BitmapOverlay;
import ice.node.widget.RadioButtonOverlay;
import ice.node.widget.RadioGroup;

public class MainSceneBase extends Scene {


    public MainSceneBase() {
        setupComponents();
    }

    private void setupComponents() {

        BitmapOverlay background = new BitmapOverlay(R.drawable.bg);
        background.setPos(getWidth() / 2, getHeight() / 2);

        ControllerBar controllerBar = new ControllerBar();

        addChildren(background, controllerBar);

        addEntries();

        addChild(serviceDialog = new ServiceDialog());

        PokerOverlay pokerOverlay = new PokerOverlay(new ColoredPoker(Poker._10, ColoredPoker.Color.Diamond));
        pokerOverlay.setBack(true);
        pokerOverlay.setPos(getWidth() / 2, getHeight() / 2, 20);


        RotateAnimation rotateAnimation = new RotateAnimation(10000, 0, 360);
        rotateAnimation.setRotateVector(1, 1, 1);
        rotateAnimation.setLoop(true);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        pokerOverlay.startAnimation(rotateAnimation);

        addChild(pokerOverlay);
    }

    private void addEntries() {

        float width = getWidth();
        float height = getHeight();

        normalEntry = new RadioButtonOverlay(
                R.drawable.normal_entry,
                R.drawable.normal_entry_press,
                0
        );

        loaderEntry = new RadioButtonOverlay(
                R.drawable.loader_entry,
                R.drawable.loader_entry_press,
                R.drawable.loader_entry_disable
        );

        superEntry = new RadioButtonOverlay(
                R.drawable.super_entry,
                R.drawable.super_entry_press,
                R.drawable.super_entry_disable
        );

        int margin = 150;

        this.normalEntry.setPos(
                width / 2 - margin - this.normalEntry.getWidth() / 2,
                height / 2
        );


        loaderEntry.setPos(width / 2, height / 2);

        superEntry.setPos(
                width / 2 + margin + this.normalEntry.getWidth() / 2,
                height / 2
        );

        radioGroup = new RadioGroup(this.normalEntry, loaderEntry, superEntry);

        addChildren(radioGroup);
    }


    public RadioButtonOverlay getLoaderEntry() {
        return loaderEntry;
    }

    public RadioButtonOverlay getNormalEntry() {
        return normalEntry;
    }

    public RadioButtonOverlay getSuperEntry() {
        return superEntry;
    }

    public ServiceDialog getServiceDialog() {
        return serviceDialog;
    }

    public RadioGroup getRadioGroup() {
        return radioGroup;
    }

    private ServiceDialog serviceDialog;

    protected RadioGroup radioGroup;
    protected RadioButtonOverlay normalEntry;
    protected RadioButtonOverlay loaderEntry;
    protected RadioButtonOverlay superEntry;
}
