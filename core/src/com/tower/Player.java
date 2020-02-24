package com.tower;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

public class Player {
    final float max_x_velocity = 7.5f;
    final float max_y_velocity = 1.5f;
    final float acceleration = 0.5f;
    final float damping = 0.25f;
    final float air_damping = 0.125f;
    private Game parent;
    Sprite sprite;
    boolean left;
    boolean right;
    boolean jump;
    Texture p_left;
    Texture p_right;
    float y_velocity;
    float x_velocity;
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
        boolean grounded = isGrounded();
        if (left) {
            if (sprite.getTexture() != p_left) sprite.setTexture(p_left);
//            parent.camera.position.x -= 5;
            x_velocity -= acceleration;

//            if (sprite.getX() < parent.WIDTH / 2f) {
//                sprite.translateX(-1 * speed);
//            } else {
//                parent.viewport.getCamera().translate(-1 * speed, 0, 0);
//                if (parent.viewport.getCamera().position.x < parent.SCREEN_WIDTH / 2f) {
//                    parent.viewport.getCamera().translate(speed, 0, 0);
//                    sprite.translateX(-1 * speed);
//                    if (sprite.getX() < 0) {
//                        sprite.translateX(speed);
//                    }
//                }
//            }
        }
        if (right) {
            if (sprite.getTexture() != p_right) sprite.setTexture(p_right);
//            parent.camera.position.x += 5;
            x_velocity += acceleration;
//            parent.viewport.getCamera().position.x += 5;

//            if (sprite.getX() < parent.SCREEN_WIDTH / 2f) {
//                sprite.translateX(speed);
//            } else {
//                parent.viewport.getCamera().translate(speed, 0, 0);
//                if (parent.viewport.getCamera().position.x > parent.map.getProperties().get
//                        ("width", Integer.class) * 70 - parent.SCREEN_WIDTH / 2f) {
//                    parent.viewport.getCamera().translate(speed * -1, 0, 0);
//                    sprite.translateX(speed);
//                    if (sprite.getX() + sprite.getTexture().getWidth() > parent.SCREEN_WIDTH) {
//                        sprite.translateX(speed);
//                    }
//                }
//            }
        }
        if (jump) {
//            Rectangle player_rect = new Rectangle();
//            player_rect.set(sprite.getX() + parent.viewport.getCamera().position.x - parent.SCREEN_WIDTH / 2f,
//                    sprite.getY() - 1 + parent.viewport.getCamera().position.y - parent.SCREEN_HEIGHT / 2f,
//                    sprite.getTexture().getWidth(), sprite.getTexture().getHeight());
//
//            // Check if on ground
//            if (parent.collisionCheck(player_rect, "platform") || parent.collisionCheck(player_rect, "ladder")) {
//                y_velocity = 20;
//            }
        }
        if (x_velocity > 0) {
            if (grounded) x_velocity -= damping;
            else x_velocity -= air_damping;
            if (x_velocity < 0) x_velocity = 0;
        }
        else if (x_velocity < 0) {
            if (grounded) x_velocity += damping;
            else x_velocity += air_damping;
            if (x_velocity > 0) x_velocity = 0;
        }

        x_velocity = MathUtils.clamp(x_velocity, -max_x_velocity, max_x_velocity);
        y_velocity = MathUtils.clamp(y_velocity, -max_y_velocity, max_y_velocity);

        parent.camera.position.x += x_velocity;
//        sprite.setPosition(sprite.getX() + x_velocity, sprite.getY() + y_velocity);
//        parent.camera.position.x = sprite.getX();
    }

    public boolean isGrounded() { // TODO
        return true;
    }
}
