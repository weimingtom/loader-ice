package com.ebensz.games.scenes;

import android.graphics.Bitmap;
import com.ebensz.games.R;
import com.ebensz.games.scenes.dialogs.ServiceDialog;
import ice.engine.Scene;
import ice.node.widget.RadioButton;
import ice.node.widget.RadioGroup;
import ice.node.widget.TextureGrid;
import ice.res.Res;

public class MainSceneBase extends Scene {


    public MainSceneBase() {
        setupComponents();
    }

    private void setupComponents() {

        TextureGrid background = new TextureGrid(getWidth(), getHeight(), R.drawable.bg);
        background.setPos(0, getHeight());

        ControllerBar controllerBar = new ControllerBar();

        addChildren(background, controllerBar);

        addEntries();

        addChild(serviceDialog = new ServiceDialog());
    }

    private void addEntries() {

        float width = getWidth();
        float height = getHeight();

        Bitmap bitmap = Res.getBitmap(R.drawable.normal_entry);

        normalEntry = new RadioButton(
                R.drawable.normal_entry,
                R.drawable.normal_entry_press,
                0
        );

        loaderEntry = new RadioButton(
                R.drawable.loader_entry,
                R.drawable.loader_entry_press,
                R.drawable.loader_entry_disable
        );

        superEntry = new RadioButton(
                R.drawable.super_entry,
                R.drawable.super_entry_press,
                R.drawable.super_entry_disable
        );

        int margin = 150;

        this.normalEntry.setPos(
                width / 2 - margin - this.normalEntry.getWidth(),
                (height + this.normalEntry.getHeight()) / 2
        );


        loaderEntry.setPos(
                (width - loaderEntry.getWidth()) / 2,
                (height + loaderEntry.getHeight()) / 2
        );

        superEntry.setPos(
                width / 2 + margin,
                (height + superEntry.getHeight()) / 2
        );

        radioGroup = new RadioGroup(this.normalEntry, loaderEntry, superEntry);

        addChildren(radioGroup);
    }


    public RadioButton getLoaderEntry() {
        return loaderEntry;
    }

    public RadioButton getNormalEntry() {
        return normalEntry;
    }

    public RadioButton getSuperEntry() {
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
    protected RadioButton normalEntry;
    protected RadioButton loaderEntry;
    protected RadioButton superEntry;
}
