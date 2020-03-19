package com.tower.gameObjects;

import com.tower.Game;

public class Exit extends gameObject {
    Game parent;

    public Exit(Game parent) {
        this.parent = parent;
    }

    @Override
    public void onEnter() {
        parent.nextLevel();
    }

}
