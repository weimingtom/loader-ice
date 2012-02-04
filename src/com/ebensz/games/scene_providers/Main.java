package com.ebensz.games.scene_providers;

import com.ebensz.games.scenes.MainScene;
import ice.engine.Scene;
import ice.engine.SceneProvider;

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
    protected boolean isEntry() {
        return true;
    }

    @Override
    protected Scene getScene() {
        return scene;
    }

    private Scene scene;
}
