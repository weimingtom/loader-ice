package com.ebensz.games;

import android.opengl.GLU;
import com.ebensz.games.graphic.LoaderRenderer;
import com.ebensz.games.scene_providers.Loading;
import ice.engine.Game;
import ice.engine.GameView;
import ice.engine.SceneProvider;
import ice.graphic.GlRenderer;
import ice.graphic.projection.PerspectiveProjection;

public class Loader extends Game {

    @Override
    protected Class<? extends SceneProvider> getEntry() {
        return Loading.class;
    }

    @Override
    protected GameView buildGameView() {

        return new GameView(this) {
            @Override
            protected GlRenderer onCreateGlRenderer() {
                return new LoaderRenderer();
            }
        };
    }

}
