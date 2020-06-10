package com.tower;

import com.strongjoshua.console.CommandExecutor;

public class consoleCommands extends CommandExecutor {
    Game game;
    public consoleCommands (Game game) {
        this.game = game;
    }
    public void debug (Boolean value) {
        game.debug_mode = value;
    }
    public void d (Boolean value) {
        game.debug_mode = value;
    }
    public void nextLevel () {
        game.nextLevel();
    }
    public void nl () {
        game.nextLevel();
    }
    public void setGrowth (int value) {
        game.player.score = value;
    }
    public void loadingScreens (Boolean value) {
        game.loadingScreens = value;
    }
    public void ls (Boolean value) {
        game.loadingScreens = value;
    }
    public void kill () {game.player.respawn();}

}
