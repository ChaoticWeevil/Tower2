package com.tower.gameObjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.tower.Game;

public class ToggleDeath extends gameObject {
    Game parent;
    MapObject object;
    String control;
    long lastTime = 0;
    int period;
    boolean on = false;

    public ToggleDeath(Game parent, float x, float y, float width, float height, MapObject object, String control) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.object = object;
        this.control = control;
        try {
            period = object.getProperties().get("period", int.class);
        } catch (Exception ignored) {
            period = 1;
        }
        System.out.println(period);
    }

    @Override
    public void update() {
        if (control.equals("SIGNALS")) {
            int ID;
            try {
                ID = parent.signals.get(object.getProperties().get("ID", int.class));
            } catch (Exception ignored) {
                ID = 0;
            }
            if (ID == 1) {
                object.getProperties().put("Death", false);
                ((TiledMapTileLayer) parent.map.getLayers().get("Things")).getCell((int) x / 70, (int) y / 70).getTile()
                        .setTextureRegion(new TextureRegion(parent.manager.get("maps/Tiles/blankTile.png", Texture.class)));
            } else {
                object.getProperties().put("Death", true);
                ((TiledMapTileLayer) parent.map.getLayers().get("Things")).getCell((int) x / 70, (int) y / 70).getTile()
                        .setTextureRegion(new TextureRegion(parent.manager.get((String) object.getProperties().get("texture"), Texture.class)));
            }

        }
        else if (control.equals("TIMER")) {
            if (System.currentTimeMillis() > lastTime + (1000 * period)) {
                on = !on;
                lastTime = System.currentTimeMillis();
                if (on) {
                    object.getProperties().put("Death", true);
                    ((TiledMapTileLayer) parent.map.getLayers().get("Things")).getCell((int) x / 70, (int) y / 70).getTile()
                            .setTextureRegion(new TextureRegion(parent.manager.get((String) object.getProperties().get("texture"), Texture.class)));
                } else {
                    object.getProperties().put("Death", false);
                    ((TiledMapTileLayer) parent.map.getLayers().get("Things")).getCell((int) x / 70, (int) y / 70).getTile()
                            .setTextureRegion(new TextureRegion(parent.manager.get("maps/Tiles/blankTile.png", Texture.class)));
                }
            }
        }
    }
}
