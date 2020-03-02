package com.tower;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import org.w3c.dom.css.Rect;

public class Player {
    // Physics values
    final float MAX_X_VELOCITY = 7.5f;
    final float MAX_Y_VELOCITY = 10f;
    final float ACCELERATION = 0.5f;
    final float DAMPING = 0.25f;
    final float AIR_DAMPING = 0.125f;
    final float GRAVITY = -0.2f;
    final float JUMP_SPEED = 9;

    private Game parent;
    Sprite sprite;
    boolean left;
    boolean right;
    boolean jump;
    Texture p_left;
    Texture p_right;
    float y_velocity;
    float x_velocity;
    boolean grounded = false;
    private Array<Rectangle> tiles = new Array<Rectangle>();
    public Player(Game parent) {
        this.parent = parent;
        p_left = parent.manager.get("p_left.png", Texture.class);
        p_right = parent.manager.get("p_right.png", Texture.class);
        sprite = new Sprite(p_right);
        sprite.setX(parent.WIDTH/2f);
        sprite.setY(parent.HEIGHT/2f);
        y_velocity = 0;
    }

    public void render(Batch batch) {
        sprite.draw(batch);
    }

    public void update() {
        Rectangle rect = parent.rectPool.obtain();
        rect.set(sprite.getX() + parent.camera.position.x - parent.WIDTH/2f,
                sprite.getY() + parent.camera.position.y - parent.HEIGHT/2f + y_velocity - 1,
                sprite.getWidth(), sprite.getHeight());
        tiles = parent.getTiles(rect, tiles, "platform");
        tiles = parent.getTiles(rect, tiles, "platform");
        if (tiles.isEmpty()) {
            grounded = false;
            parent.test = false;
        }
        else {
            grounded = true;
            y_velocity = 0;
            parent.test = true;
        }

        if (left) {
            if (sprite.getTexture() != p_left) sprite.setTexture(p_left);
            x_velocity -= ACCELERATION;
        }
        if (right) {
            if (sprite.getTexture() != p_right) sprite.setTexture(p_right);
            x_velocity += ACCELERATION;
        }
        if (jump) {
            jump = false;
            if (grounded) y_velocity += JUMP_SPEED;
        }


        if (x_velocity > 0) {
            if (grounded) x_velocity -= DAMPING;
            else x_velocity -= AIR_DAMPING;
            if (x_velocity < 0) x_velocity = 0;
        }
        else if (x_velocity < 0) {
            if (grounded) x_velocity += DAMPING;
            else x_velocity += AIR_DAMPING;
            if (x_velocity > 0) x_velocity = 0;
        }

        if (!grounded) y_velocity += GRAVITY;


        x_velocity = MathUtils.clamp(x_velocity, -MAX_X_VELOCITY, MAX_X_VELOCITY);
        y_velocity = MathUtils.clamp(y_velocity, -MAX_Y_VELOCITY, MAX_Y_VELOCITY);

        rect.set(sprite.getX() + parent.camera.position.x - parent.WIDTH/2f + x_velocity,
                sprite.getY() + parent.camera.position.y - parent.HEIGHT/2f,
                sprite.getWidth(), sprite.getHeight());
        tiles = parent.getTiles(rect, tiles, "platform");
        tiles = parent.getTiles(rect, tiles, "platform");
        if (! tiles.isEmpty()) {
            x_velocity = 0;
        }


        parent.camera.position.x += x_velocity;
        parent.camera.position.y += y_velocity;
    }

}
