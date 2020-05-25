package com.tower.gameObjects;

import com.badlogic.gdx.math.Rectangle;
import com.tower.Game;


public class SpawnPoint extends gameObject {
    Rectangle rect;
    Game parent;

    public SpawnPoint(Game parent, float x, float y, float width, float height) {
        this.parent = parent;
        rect = new Rectangle((int)x,(int)y+1,(int)width,(int)height);
    }

    @Override
    public void onEnter() {
        parent.player.spawnLocation = rect;
    }

    @Override
    public void onExit() {
    }

}
