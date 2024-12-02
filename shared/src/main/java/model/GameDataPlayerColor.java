package model;

public class GameDataPlayerColor {
    private final GameData gameData;
    private final String playerColor;

    public GameDataPlayerColor(GameData gameData, String playerColor) {
        this.gameData = gameData;
        this.playerColor = playerColor;
    }

    public GameData getGameData() {
        return gameData;
    }

    public String getPlayerColor() {
        return playerColor;
    }
}
