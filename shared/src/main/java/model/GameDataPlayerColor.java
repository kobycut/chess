package model;

import chess.ChessGame;

public class GameDataPlayerColor {
    private final GameData gameData;
    private String playerColor;

    public GameDataPlayerColor(GameData gameData, String playerColor) {
        this.gameData = gameData;
        this.playerColor = playerColor;
    }

    public GameData getGameData() {
        return gameData;
    }
    public void setPlayerColor(String color) {
        this.playerColor = color;
    }

    public String getPlayerColor() {
        return playerColor;
    }
}
