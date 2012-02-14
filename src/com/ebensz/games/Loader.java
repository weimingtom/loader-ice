package com.ebensz.games;

import android.os.Bundle;
import android.view.Display;
import com.ebensz.games.graphic.LoaderView;
import com.ebensz.games.scene_providers.Main;
import ice.engine.Game;
import ice.engine.GameView;
import ice.engine.SceneProvider;

public class Loader extends Game {

    @Override
    protected Class<? extends SceneProvider> getEntry() {
        return Main.class;
    }

}
