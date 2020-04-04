package com.tower.gameObjects;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.tower.Game;

public class Collectable extends gameObject {
    Game parent;
    int x;
    int y;

    public Collectable(Game parent, int x , int y) {
        this.parent = parent;
        this.x = x;
        this.y = y;
    }

    @Override
    public void onEnter() {
        parent.player.collectablesFound += 1;
        parent.player.score += 100;
        x /= 70;
        y /= 70;
        ((TiledMapTileLayer)parent.map.getLayers().get("Things")).getCell(x, y).setTile(null);
        parent.map.getLayers().get("Collision_Layer").getObjects().remove(parent.map.getLayers().get("Collision_Layer").getObjects().get("Collectable"));
    }

}
