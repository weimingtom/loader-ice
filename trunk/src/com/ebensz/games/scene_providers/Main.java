package com.ebensz.games.scene_providers;

import com.ebensz.games.R;
import com.ebensz.games.scenes.MainScene;
import com.ebensz.games.scenes.dialogs.ServiceDialog;
import ice.engine.Scene;
import ice.engine.SceneProvider;
import ice.res.Res;

/**
 * User: Jason
 * Date: 12-2-4
 * Time: 下午3:09
 */
public class Main extends SceneProvider {
    public Main() {
        scene = new MainScene();
    }


    @Override
    protected void onResume() {
        super.onResume();

        MainScene mainScene = (MainScene) scene;

//        mainScene.updateLockStates(states);
//
//        bindButtonAction(states);

        ServiceDialog serviceDialog = mainScene.getServiceDialog();
        serviceDialog.startEntryAnimation(Res.getText(R.string.welcome));

    }


    @Override
    protected boolean isEntry() {
        return true;
    }

    @Override
    protected Scene getScene() {
        return scene;
    }

    private Scene scene;
}
