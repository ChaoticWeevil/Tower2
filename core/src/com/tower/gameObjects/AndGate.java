package com.tower.gameObjects;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.tower.Game;


public class AndGate extends gameObject {
    Game parent;

    public AndGate(Game parent, float x, float y, float width, float height) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void update() {
        MapLayer layer = parent.map.getLayers().get("Collision_Layer");
        MapObjects objects = layer.getObjects();
        for (MapObject object : objects) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            int Input1;
            int Input2;
            int OutputID = 0;
            if (rectangle.x == x && rectangle.y == y) {
                try {
                    Input1 = parent.signals.get(object.getProperties().get("InputID1", Integer.class));
                    Input2 = parent.signals.get(object.getProperties().get("InputID2", Integer.class));
                    OutputID = object.getProperties().get("OutputID", Integer.class);
                } catch (Exception ignored) {
                    Input1 = 0;
                    Input2 = 0;
                }
                if (Input1 == 1 && Input2 == 1) parent.signals.put(OutputID, 1);
                else parent.signals.put(OutputID, 0);

            }
        }
    }
}

