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

public class Switch extends gameObject {
    Game parent;
    float x;
    float y;
    public Switch(Game parent, float x, float y, float width, float height) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        hasActivateMethod = true;
    }

    @Override
    public void onActivate() {
        MapLayer layer = parent.map.getLayers().get("Collision_Layer");
        MapObjects objects = layer.getObjects();
        for (MapObject object : objects) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            if (rectangle.x == x && rectangle.y == y) {
                int value;
                Texture t;
                try {
                    value = parent.signals.get(object.getProperties().get("ID"));
                    if (value == 1) {
                        value = 0;
                        t = parent.manager.get(object.getProperties().get("OffTexture", String.class), Texture.class);
                    }
                    else {
                        value = 1;
                        t = parent.manager.get(object.getProperties().get("OnTexture", String.class), Texture.class);
                    }
                } catch (Exception ignored) {
                    value = 1;
                    t = parent.manager.get(object.getProperties().get("OnTexture", String.class), Texture.class);
                }
                parent.signals.put((Integer) object.getProperties().get("ID"), value);
                ((TiledMapTileLayer) parent.map.getLayers().get("Things")).getCell((int)x/70, (int)y/70).getTile()
                        .setTextureRegion(new TextureRegion(t));
            }
        }
    }
}

