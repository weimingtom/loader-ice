package com.ebensz.games.scenes;

import android.graphics.Bitmap;
import com.ebensz.games.R;
import com.ebensz.games.scenes.dialogs.ServiceDialog;
import ice.engine.EngineContext;
import ice.engine.Scene;
import ice.node.widget.RadioButton;
import ice.node.widget.RadioGroup;
import ice.node.widget.TextureGrid;
import ice.res.Res;

/**
 * User: Jason
 * Date: 12-2-4
 * Time: 下午3:10
 */
public class MainScene extends Scene {


    public MainScene() {
        int appWidth = EngineContext.getAppWidth();
        int appHeight = EngineContext.getAppHeight();

        TextureGrid background = new TextureGrid(appWidth, appHeight, R.drawable.bg);
        background.setPos(0, appHeight);
        addChild(background);
    }


    private void init() {

        TextureGrid background = new TextureGrid(R.drawable.bg);
        background.setPos(-20, -20);

        ControllerBar controllerBar = new ControllerBar();

        addChildren(background, controllerBar);

        addEntries();

        addChild(serviceDialog = new ServiceDialog());
    }

    private void addEntries() {

        int width = getWidth();
        int height = getHeight();

        Bitmap normal = Res.getBitmap(R.drawable.normal_entry);
        Bitmap pressed = Res.getBitmap(R.drawable.normal_entry_press);
        Bitmap hover = Res.getBitmap(R.drawable.normal_entry_hover);
        normalEntry = new RadioButton(normal, pressed, null);

        normal = Res.getBitmap(R.drawable.loader_entry);
        pressed = Res.getBitmap(R.drawable.loader_entry_press);
        hover = Res.getBitmap(R.drawable.loader_entry_hover);
        Bitmap disable = Res.getBitmap(R.drawable.loader_entry_disable);
        loaderEntry = new RadioButton(normal, pressed, disable);

        normal = Res.getBitmap(R.drawable.super_entry);
        pressed = Res.getBitmap(R.drawable.super_entry_press);
        hover = Res.getBitmap(R.drawable.super_entry_hover);
        disable = Res.getBitmap(R.drawable.super_entry_disable);
        superEntry = new RadioButton(normal, pressed, disable);


        int margin = 150;

        normalEntry.setPos(
                width / 2 - margin - normalEntry.getWidth(),
                (height - normalEntry.getHeight()) / 2
        );


        loaderEntry.setPos(
                (width - loaderEntry.getWidth()) / 2,
                (height - loaderEntry.getHeight()) / 2
        );

        superEntry.setPos(
                width / 2 + margin,
                (height - superEntry.getHeight()) / 2
        );

        radioGroup = new RadioGroup(normalEntry, loaderEntry, superEntry);
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

    private RadioGroup radioGroup;
    private RadioButton normalEntry;
    private RadioButton loaderEntry;
    private RadioButton superEntry;


}
