package com.ebensz.games.logic.story;

import com.ebensz.games.R;
import com.ebensz.games.logic.RuntimeData;
import com.ebensz.games.logic.player.Player;
import com.ebensz.games.logic.settle.SettleTool;
import com.ebensz.games.logic.settle.SuperSettleTool;
import com.ebensz.games.model.Dir;
import com.ebensz.games.model.History;

import java.util.Map;

public class SuperGame extends NormalGame {

    public SuperGame(){

        super();
        getScene().getGameControllerBar().getVenue().setBitmap(R.drawable.super_venue);
    }

    @Override
    protected RuntimeData prepareRuntime(RuntimeData oldData, History history) {

        RuntimeData runtimeData = super.prepareRuntime(oldData, history);

        SettleTool settleTool = runtimeData.getSettleTool();

        if (!(settleTool instanceof SuperSettleTool)) {

            Map<Dir, Player> playerMap = runtimeData.getPlayerMap();

            runtimeData.setSettleTool(new SuperSettleTool(playerMap));
        }

        return runtimeData;
    }
}
