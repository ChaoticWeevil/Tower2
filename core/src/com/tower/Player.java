package com.tower;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.tower.gameObjects.*;

public class Player {
    // Physics values
    final float MAX_X_VELOCITY = 6.5f;
    final float MAX_Y_VELOCITY = 14f;
    final float MAX_LADDER_VELOCITY = 8f;
    final float ACCELERATION = 0.7f;
    final float DAMPING = 0.45f;
    final float AIR_DAMPING = 0.3f;
    final float GRAVITY = -0.2f;
    public final float JUMP_SPEED = 7.8f;

    private Game parent;
    Sprite sprite;
    boolean left;
    boolean right;
    boolean jump;
    Texture p_left;
    Texture p_right;
    public float y_velocity = 0;
    float x_velocity;
    boolean grounded = false;
    public boolean onLadder = false;
    private Array<Rectangle> objects = new Array<>();
    private Array<gameObject> overlappedObjects = new Array<>();
    private Array<gameObject> tempObjects = new Array<>();
    Rectangle spawn_location;

    public Player(Game parent) {
        this.parent = parent;
        p_left = parent.manager.get("p_left.png", Texture.class);
        p_right = parent.manager.get("p_right.png", Texture.class);
        sprite = new Sprite(p_right);
        sprite.setX(parent.WIDTH / 2f);
        sprite.setY(parent.HEIGHT / 2f);
        spawn();

    }

    public void render(Batch batch) {
        sprite.draw(batch);
    }

    public void update() {
        // Manage gameObjects
        Rectangle rect = parent.rectPool.obtain();
        rect.set(sprite.getX() + parent.camera.position.x - parent.WIDTH / 2f,
                sprite.getY() + parent.camera.position.y - parent.HEIGHT / 2f,
                sprite.getWidth(), sprite.getHeight());
        tempObjects.clear();
        for (String check : parent.gameObjects) {
            objects = parent.getTiles(rect, objects, check);
            objects = parent.getTiles(rect, objects, check);
            for (Rectangle r : objects) {
                switch (check) {
                    case "Ladder":
                        tempObjects.add(new Ladder(parent, r.x, r.y, r.width, r.height));
                        break;
                    case "Death":
                        tempObjects.add(new Death(parent));
                        break;
                    case "Exit":
                        tempObjects.add(new Exit(parent));
                        break;
                    case "Collectable":
                        tempObjects.add(new Collectable(parent, (int)r.x, (int)r.y));
                        break;
                }
            }
        }
        for (gameObject o : overlappedObjects) {
            if (!tempObjects.contains(o, false)) {
                o.onExit();
                overlappedObjects.removeValue(o, false);
            }
        }
        for (gameObject o : tempObjects) {
            if (!overlappedObjects.contains(o, false)) {
                overlappedObjects.add(o);
                o.onEnter();
            }
        }
        for (gameObject o : overlappedObjects) {
            o.update();
        }


        rect.set(sprite.getX() + parent.camera.position.x - parent.WIDTH / 2f,
                sprite.getY() + parent.camera.position.y - parent.HEIGHT / 2f + y_velocity - 1,
                sprite.getWidth(), sprite.getHeight());
        objects = parent.getTiles(rect, objects, "platform");
        objects = parent.getTiles(rect, objects, "platform");
        if (objects.isEmpty()) {
            grounded = false;
            parent.test = false;
        } else {
            if (y_velocity < 0) {
                parent.test = true;
                grounded = true;
            }
            y_velocity = 0;
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
            if (grounded || onLadder) y_velocity += JUMP_SPEED;
        }


        if (x_velocity > 0) {
            if (grounded) x_velocity -= DAMPING;
            else x_velocity -= AIR_DAMPING;
            if (x_velocity < 0) x_velocity = 0;
        } else if (x_velocity < 0) {
            if (grounded) x_velocity += DAMPING;
            else x_velocity += AIR_DAMPING;
            if (x_velocity > 0) x_velocity = 0;
        }

        if (!grounded) y_velocity += GRAVITY;


        x_velocity = MathUtils.clamp(x_velocity, -MAX_X_VELOCITY, MAX_X_VELOCITY);
        if (onLadder) y_velocity = MathUtils.clamp(y_velocity, -MAX_LADDER_VELOCITY, MAX_LADDER_VELOCITY);
        else y_velocity = MathUtils.clamp(y_velocity, -MAX_Y_VELOCITY, MAX_Y_VELOCITY);

        rect.set(sprite.getX() + parent.camera.position.x - parent.WIDTH / 2f + x_velocity,
                sprite.getY() + parent.camera.position.y - parent.HEIGHT / 2f,
                sprite.getWidth(), sprite.getHeight());
        objects = parent.getTiles(rect, objects, "platform");
        objects = parent.getTiles(rect, objects, "platform");
        if (!objects.isEmpty()) {
            x_velocity = 0;
        }

        rect.set(sprite.getX() + parent.camera.position.x - parent.WIDTH / 2f,
                sprite.getY() + parent.camera.position.y - parent.HEIGHT / 2f + y_velocity + 1,
                sprite.getWidth(), sprite.getHeight());
        objects = parent.getTiles(rect, objects, "platform");
        objects = parent.getTiles(rect, objects, "platform");
        if (!objects.isEmpty()) {
            if (y_velocity > 0) {
                y_velocity = 0;
            }
        }

        parent.camera.position.x += x_velocity;
        parent.camera.position.y += y_velocity;

        if (parent.camera.position.y < -100) {
            respawn();
        }
    }

    public void respawn() {
        parent.camera.position.x = spawn_location.x;
        parent.camera.position.y = spawn_location.y;
        x_velocity = y_velocity = 0;
    }

    public void spawn() {
        try {
            spawn_location = new Rectangle();
            spawn_location.set(0, 0, parent.MAP_WIDTH, parent.MAP_HEIGHT);
            objects = parent.getTiles(spawn_location, objects, "spawn");
            objects = parent.getTiles(spawn_location, objects, "spawn");
            spawn_location.set(objects.first());
            spawn_location.y += 1;
            parent.camera.position.x = spawn_location.x;
            parent.camera.position.y = spawn_location.y;
            objects.clear();
        } catch (Exception ignored) {
            parent.camera.position.x = parent.WIDTH / 2f;
            parent.camera.position.y = parent.HEIGHT / 2f;
        }
        y_velocity = x_velocity = 0;
        left = right = jump = false;
    }
}
