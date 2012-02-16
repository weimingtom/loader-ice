package com.ebensz.games.scenes;

import android.graphics.Bitmap;
import com.ebensz.games.R;
import com.ebensz.games.scenes.dialogs.ServiceDialog;
import ice.engine.Scene;
import ice.node.widget.BitmapOverlay;
import ice.node.widget.RadioButtonOverlay;
import ice.node.widget.RadioGroup;
import ice.res.Res;

public class MainSceneBase extends Scene {


    public MainSceneBase() {
        setupComponents();
    }

    private void setupComponents() {

        BitmapOverlay background = new BitmapOverlay(getWidth(), getHeight(), R.drawable.bg);

        ControllerBar controllerBar = new ControllerBar();

        addChildren(background, controllerBar);

        addEntries();

        addChild(serviceDialog = new ServiceDialog());
    }

    private void addEntries() {

        float width = getWidth();
        float height = getHeight();

        Bitmap bitmap = Res.getBitmap(R.drawable.normal_entry);

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
                width / 2 - margin - this.normalEntry.getWidth(),
                (height - this.normalEntry.getHeight()) / 2
        );


        loaderEntry.setPos(
                (width - loaderEntry.getWidth()) / 2,
                (height - loaderEntry.getHeight()) / 2
        );

        superEntry.setPos(
                width / 2 + margin,
                (height - superEntry.getHeight()) / 2
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
