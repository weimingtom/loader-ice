package com.ebensz.games.logic.story;

import com.ebensz.games.R;
import com.ebensz.games.model.Dir;

public class LoaderGame extends NormalGame {

    public LoaderGame(){

        super();
        getScene().getGameControllerBar().getVenue().setBitmap(R.drawable.loader_venue);
    }


    @Override
    protected Dir ensureLoaderDir() {
        return Dir.Outside;
    }


}
