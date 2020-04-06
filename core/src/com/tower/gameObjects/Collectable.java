package com.tower.gameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.tower.Game;

public class Collectable extends gameObject {
    Game parent;
    int x;
    int y;
    final int carPart = 1;
    final int fertilizer = 2;
    int type;


    public Collectable(Game parent, int x, int y, int type) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.type = type;
    }

    @Override
    public void onEnter() {
        final Dialog d;
        if ((type == carPart && parent.player.carPartsFound == 0) || (type == fertilizer && parent.player.fertilizerFound == 0)) {
            if (type == carPart) {
                d = new Dialog("You found an electric car part", new Skin(Gdx.files.internal("expeeSkin/expee-ui.json")));
                d.text("There is one hidden in each level see if you can find them all");
            }
            else {
                d = new Dialog("You found a bag of fertilizer", new Skin(Gdx.files.internal("expeeSkin/expee-ui.json")));
                d.text("This wil help your tree grow.\nThere is one hidden in each level see if you can find them all");
                d.align(Align.center);
            }
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
        if (type == carPart) {
            parent.player.carPartsFound ++;
        }
        else {
            parent.player.fertilizerFound ++;
            parent.player.score += 100;
        }


        x /= 70;
        y /= 70;
        ((TiledMapTileLayer) parent.map.getLayers().get("Things")).getCell(x, y).setTile(null);
        if (type == carPart) parent.map.getLayers().get("Collision_Layer").getObjects().remove(parent.map.getLayers().get("Collision_Layer").getObjects().get("CarPart"));
        else parent.map.getLayers().get("Collision_Layer").getObjects().remove(parent.map.getLayers().get("Collision_Layer").getObjects().get("Fertilizer"));
    }

}
