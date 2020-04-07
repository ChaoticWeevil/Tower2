package com.tower.gameObjects;

import com.tower.Game;

public class Exit extends gameObject {
    Game parent;

    public Exit(Game parent, float x, float y, float width, float height) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void onEnter() {
        parent.nextLevel();
    }

}
