package com.ebensz.games.logic;

import com.ebensz.games.logic.player.Player;
import com.ebensz.games.model.Dir;
import com.ebensz.games.model.GameState;
import com.ebensz.games.model.RoomType;
import com.ebensz.games.model.poker.PokersInfo;

import java.io.Serializable;
import java.util.Map;

public class SavedGame implements Serializable {

    private static final long serialVersionUID = 4209360273818925922L;

    public static SavedGame generate() {

        return null;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public PokersInfo getPokersInfo() {
        return pokersInfo;
    }

    public void setPokersInfo(PokersInfo pokersInfo) {
        this.pokersInfo = pokersInfo;
    }

    public Map<Dir, Player> getPlayerMap() {
        return playerMap;
    }

    public void setPlayerMap(Map<Dir, Player> playerMap) {
        this.playerMap = playerMap;
    }

    public Dir getFocusDir() {
        return focusDir;
    }

    public void setFocusDir(Dir focusDir) {
        this.focusDir = focusDir;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    private GameState gameState = GameState.NotShow;

    private RoomType roomType = RoomType.NORMAL_ROOM;
    private Dir focusDir;
    Map<Dir, Player> playerMap;
    private PokersInfo pokersInfo;
}
