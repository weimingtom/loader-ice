package com.ebensz.games.scene_providers;

import com.ebensz.games.logic.story.Game;
import com.ebensz.games.logic.story.LoaderGame;
import com.ebensz.games.logic.story.NormalGame;
import com.ebensz.games.logic.story.SuperGame;
import com.ebensz.games.scenes.GameScene;
import ice.engine.Scene;
import ice.engine.SceneProvider;

public class GameEntry extends SceneProvider {


    public enum Mode {
        Normal, Loader, Super
    }

    @Override
    public void onCreate() {

        super.onCreate();

        game = chooseMode();

        game.onCreate();

        scene = game.getScene();
    }

    @Override
    public void onResume() {

        super.onResume();

        game.onResume(null);
    }

    @Override
    public void onPause() {

        super.onPause();

        game.onPause();
    }

    @Override
    protected Scene getScene() {
        return scene;
    }

    private Game chooseMode() {
        Mode mode = null;

        mode = Mode.Normal;

        switch (mode) {

            case Normal:
                return new NormalGame();
            case Loader:
                return new LoaderGame();
            case Super:
                return new SuperGame();
        }

        return null;
    }

    private GameScene scene;
    private Game game;
}
