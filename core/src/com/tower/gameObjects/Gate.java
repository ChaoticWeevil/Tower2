package com.tower.gameObjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.tower.Game;

public class Gate extends gameObject {
    Game parent;

    public Gate(Game parent, float x, float y, float width, float height) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void update() {
        String offTexture;
        MapLayer layer = parent.map.getLayers().get("Collision_Layer");
        MapObjects objects = layer.getObjects();
        for (MapObject object : objects) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            int ID;
            if (rectangle.x == x && rectangle.y == y) {
                try {
                    ID = parent.signals.get(object.getProperties().get("ID", Integer.class));
                } catch (Exception ignored) {
                    ID = 0;
                }
                try {
                    offTexture = object.getProperties().get("offTexture", String.class);
                } catch (Exception ignored) {
                    offTexture = "maps/tiles/blankTile.png";
                }
                if (offTexture == null) {
                    offTexture = "maps/tiles/blankTile.png";
                }

                if (ID == 1) {
                    object.getProperties().put("platform", false);
                    ((TiledMapTileLayer) parent.map.getLayers().get("Things")).getCell((int)x/70, (int)y/70).getTile()
                            .setTextureRegion(new TextureRegion(parent.manager.get(offTexture, Texture.class)));
                }
                else {
                    object.getProperties().put("platform", true);
                    ((TiledMapTileLayer) parent.map.getLayers().get("Things")).getCell((int)x/70, (int)y/70).getTile()
                            .setTextureRegion(new TextureRegion(parent.manager.get((String) object.getProperties().get("Texture"), Texture.class)));
                }
            }
        }
    }
}

