package com.tower.gameObjects;

import com.tower.Game;

public class Ladder extends gameObject {
    float x;
    float y;
    float width;
    float height;
    Game parent;

    public Ladder(Game parent, float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.parent = parent;
    }

    @Override
    public void onEnter() {
        parent.player.onLadder = true;

    }

    @Override
    public void onExit() {
        parent.player.onLadder = false;
    }

}
