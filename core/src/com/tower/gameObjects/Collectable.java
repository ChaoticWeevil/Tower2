package com.tower.gameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
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
        if (parent.player.collectablesFound == 0) {
            final Dialog d = new Dialog("You found an electric car part", new Skin(Gdx.files.internal("expeeSkin/expee-ui.json")));
            d.text("There is one hidden in each level see if you can find them all");
            d.setY(parent.HEIGHT - 85);
            d.setX(parent.WIDTH - 365);
            d.setWidth(350);
            d.setHeight(75);
            parent.stage.addActor(d);
            final Timer temp_timer = new Timer();
            temp_timer.schedule(new Timer.Task() {
                                    @Override
                                    public void run() {
                                        d.setVisible(false);
                                        temp_timer.clear();
                                    }
                                }
                    , 5
                    , 1
            );
        }


        parent.player.collectablesFound += 1;
        parent.player.score += 100;
        x /= 70;
        y /= 70;
        ((TiledMapTileLayer)parent.map.getLayers().get("Things")).getCell(x, y).setTile(null);
        parent.map.getLayers().get("Collision_Layer").getObjects().remove(parent.map.getLayers().get("Collision_Layer").getObjects().get("Collectable"));
    }

}
