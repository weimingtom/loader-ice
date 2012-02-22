package com.ebensz.games;

import com.ebensz.games.graphic.LoaderView;
import com.ebensz.games.scene_providers.Loading;
import ice.engine.Game;
import ice.engine.GameView;
import ice.engine.SceneProvider;

public class Loader extends Game {

    @Override
    protected Class<? extends SceneProvider> getEntry() {
        return Loading.class;
    }

    @Override
    protected GameView createRenderer() {
        return new LoaderView(this);
    }

}
