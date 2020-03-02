package com.tower.gameObjects;

import com.tower.Game;

public class Death extends gameObject {
    Game parent;
    public Death(Game parent) {
        this.parent = parent;
    }
    @Override
    public void onEnter () {
        parent.player.respawn();

    }

}
