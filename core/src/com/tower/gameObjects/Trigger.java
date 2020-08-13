package com.tower.gameObjects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.tower.Game;

// ID
// Value
public class Trigger extends gameObject {
    Game parent;

    public Trigger(Game parent, float x, float y, float width, float height) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void onEnter() {
        for (MapObject object : parent.map.getLayers().get("Collision_Layer").getObjects()) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            if (rectangle.x == x && rectangle.y == y) {
                int value = object.getProperties().get("Value", Integer.class);
                int id = object.getProperties().get("ID", Integer.class);
                try {
                    parent.map.getLayers().get("ID " + id).setVisible(value == 1);
                }
                catch (Exception ignored) {
                }

                parent.signals.put(id, value);
            }
        }
    }
}