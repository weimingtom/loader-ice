package com.ebensz.games.logic.story;

import com.ebensz.games.model.Dir;

public class LoaderGame extends NormalGame {


    @Override
    protected Dir ensureLoaderDir() {
        return Dir.Outside;
    }


}
