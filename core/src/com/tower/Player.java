package com.tower;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.tower.gameObjects.*;

public class Player {
    // Physics values
    final float MAX_X_VELOCITY = 6.5f;
    final float MAX_Y_VELOCITY = 14f;
    final float MAX_LADDER_VELOCITY = 5f;
    final float ACCELERATION = 0.7f;
    final float DAMPING = 0.45f;
    final float AIR_DAMPING = 0.3f;
    final float GRAVITY = -0.2f;
    public final float JUMP_SPEED = 7.8f;

    final int WALKING = 1;
    final int JUMPING = 2;
    final int STANDING = 3;
    final int LEFT = 1;
    final int RIGHT = 2;
    int facing = RIGHT;
    int state = WALKING;
    float stateTime = 0f;


    private final Game parent;
    Sprite sprite;
    boolean left;
    boolean right;
    boolean jumping;
    Texture p_left;
    Texture p_right;
    public float y_velocity = 0;
    float x_velocity;
    boolean grounded = false;
    public boolean onLadder = false;
    public boolean onMovingPlatform = false;
    public MovingPlatform movingPlatform;
    private Array<Rectangle> objects = new Array<>();
    private Array<MovingPlatform> platforms = new Array<>();
    Array<gameObject> overlappedObjects = new Array<>();
    private final Array<gameObject> tempObjects = new Array<>();
    public Rectangle spawnLocation;
    Animation<TextureRegion> walk;
    Animation<TextureRegion> jump;
    Animation<TextureRegion> idle;

    public int fertilizerFound = 0;
    public int carPartsFound = 0;
    public int score = 0;
    public int currentLevelDeaths = 0;

    public Player(Game parent) {
        this.parent = parent;
        p_left = parent.manager.get("textures/player/p_left.png", Texture.class);
        p_right = parent.manager.get("textures/player/p_right.png", Texture.class);
        sprite = new Sprite(p_right);
        sprite.setX(parent.WIDTH / 2f);
        sprite.setY(parent.HEIGHT / 2f);

        TextureAtlas atlas = new TextureAtlas("Textures/player/walkAnimation/walk.atlas");

        walk = new Animation<TextureRegion>(0.1f, atlas.getRegions());


        atlas = new TextureAtlas("Textures/player/jumpAnimation/jump.atlas");
        jump = new Animation<TextureRegion>(0.05f, atlas.getRegions().toArray());

        atlas = new TextureAtlas("Textures/player/idleAnimation/idle.atlas");
        idle = new Animation<TextureRegion>(0.3f, atlas.getRegions());

        findStart();
        spawn();

    }

    public void render(Batch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame;
        if (state == WALKING) {
            currentFrame = walk.getKeyFrame(stateTime, true);
            if (left && !currentFrame.isFlipX()) currentFrame.flip(true, false);
            else if (right && currentFrame.isFlipX()) currentFrame.flip(true, false);
        } else if (state == JUMPING) {
            currentFrame = new TextureRegion(parent.manager.get("Textures/player/jump-5.png", Texture.class));
            if (facing == LEFT && !currentFrame.isFlipX()) currentFrame.flip(true, false);
            else if (facing == RIGHT && currentFrame.isFlipX()) currentFrame.flip(true, false);
        } else {
            currentFrame = idle.getKeyFrame(stateTime, true);
            if (facing == LEFT && !currentFrame.isFlipX()) currentFrame.flip(true, false);
            else if (facing == RIGHT && currentFrame.isFlipX()) currentFrame.flip(true, false);
        }

        batch.draw(currentFrame, sprite.getX(), sprite.getY());
        for (gameObject o : overlappedObjects) {
            if (o.hasActivateMethod)
                batch.draw(parent.manager.get("eKey.png", Texture.class), sprite.getX() + (sprite.getWidth() / 2f - 17), sprite.getY() + sprite.getHeight() + 10);
        }
    }

    public boolean notContainsObject(Array<gameObject> array, gameObject a) {
        boolean contains = false;
        for (gameObject b : array) {
            if ((a.x == b.x) && (a.y == b.y) && (a.width == b.width) && (a.height == b.height)) {
                contains = true;
                break;
            }
        }
        return !contains;
    }

    public void update() {
        // Manage gameObjects
        Rectangle rect = parent.rectPool.obtain();
        rect.set(sprite.getX() + parent.camera.position.x - parent.WIDTH / 2f,
                sprite.getY() + parent.camera.position.y - parent.HEIGHT / 2f,
                sprite.getWidth(), sprite.getHeight());
        tempObjects.clear();
        for (String check : parent.gameObjects) {
            objects = parent.getMapObjects(rect, objects, check);
            for (Rectangle r : objects) {
                switch (check) {
                    case "Ladder":
                        tempObjects.add(new Ladder(parent, r.x, r.y, r.width, r.height));
                        break;
                    case "Death":
                        tempObjects.add(new Death(parent, r.x, r.y, r.width, r.height));
                        break;
                    case "Exit":
                        tempObjects.add(new Exit(parent, r.x, r.y, r.width, r.height));
                        break;
                    case "CarPart":
                        tempObjects.add(new Collectable(parent, r.x, r.y, r.width, r.height, 1));
                        break;
                    case "Fertilizer":
                        tempObjects.add(new Collectable(parent, r.x, r.y, r.width, r.height, 2));
                        break;
                    case "Switch":
                        tempObjects.add(new Switch(parent, r.x, r.y, r.width, r.height));
                        break;
                    case "SpawnPoint":
                        tempObjects.add(new SpawnPoint(parent, r.x, r.y, r.width, r.height));
                        break;
                    case "Trigger":
                        tempObjects.add(new Trigger(parent, r.x, r.y, r.width, r.height));
                }
            }
        }
        for (gameObject overlappedObject : overlappedObjects) {
            if (notContainsObject(tempObjects, overlappedObject)) {
                overlappedObject.onExit();
                overlappedObjects.removeValue(overlappedObject, false);
            }

        }
        for (gameObject o : tempObjects) {
            if (notContainsObject(overlappedObjects, o)) {
                overlappedObjects.add(o);
                o.onEnter();
            }
        }
        for (gameObject o : overlappedObjects) {
            o.overlappedUpdate();
        }


        rect.set(sprite.getX() + parent.camera.position.x - parent.WIDTH / 2f + x_velocity,
                sprite.getY() + parent.camera.position.y - parent.HEIGHT / 2f + y_velocity - 2,
                sprite.getWidth(), sprite.getHeight());

        platforms = parent.getMovingPlatforms(rect, platforms);
        if (!platforms.isEmpty()) {
            onMovingPlatform = true;
            movingPlatform = platforms.first();
        } else {
            onMovingPlatform = false;
        }

        rect.set(sprite.getX() + parent.camera.position.x - parent.WIDTH / 2f,
                sprite.getY() + parent.camera.position.y - parent.HEIGHT / 2f + y_velocity - 2,
                sprite.getWidth(), sprite.getHeight());
        objects = parent.getMapObjects(rect, objects, "platform");
        if (!objects.isEmpty()) {
            grounded = true;
            y_velocity = 0;
        }
        else {
            grounded = false;
        }
        if (onMovingPlatform) {
            if (sprite.getY() + parent.camera.position.y - parent.HEIGHT / 2f >= movingPlatform.y + movingPlatform.height - 1) {
                grounded = true;
                y_velocity = 0;
            }
        }


        if (left) {
            facing = LEFT;
            x_velocity -= ACCELERATION;
            state = WALKING;
        } else if (right) {
            facing = RIGHT;
            x_velocity += ACCELERATION;
            state = WALKING;
        } else {
            state = STANDING;
        }
        if (jumping) {
            if (grounded || onLadder) {
                y_velocity += JUMP_SPEED;
            }
        }
        if (!grounded) state = JUMPING;


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
        objects = parent.getMapObjects(rect, objects, "platform");
        rect.set(sprite.getX() + parent.camera.position.x - parent.WIDTH / 2f + x_velocity - 5,
                sprite.getY() + parent.camera.position.y - parent.HEIGHT / 2f,
                sprite.getWidth() + 5, sprite.getHeight());
        platforms = parent.getMovingPlatforms(rect, platforms);

        if (!objects.isEmpty() || !platforms.isEmpty()) {
            x_velocity = 0;
        }

        rect.set(sprite.getX() + parent.camera.position.x - parent.WIDTH / 2f,
                sprite.getY() + parent.camera.position.y - parent.HEIGHT / 2f + y_velocity + 1,
                sprite.getWidth(), sprite.getHeight());
        objects = parent.getMapObjects(rect, objects, "platform");
        platforms = parent.getMovingPlatforms(rect, platforms);
        if (!objects.isEmpty()) {
            if (y_velocity > 0) {
                y_velocity = 0;
            }
        }

        parent.camera.position.x += x_velocity;
        parent.camera.position.y += y_velocity;

        if (onMovingPlatform) {
            if (movingPlatform.currentDirection.equals("RIGHT")) {
                parent.camera.position.x += 2;
                rect.set(sprite.getX() + parent.camera.position.x - parent.WIDTH / 2f + x_velocity,
                        sprite.getY() + parent.camera.position.y - parent.HEIGHT / 2f,
                        sprite.getWidth(), sprite.getHeight());
                objects = parent.getMapObjects(rect, objects, "platform");
                if (!objects.isEmpty()) parent.camera.position.x -= 2;
            } else {
                parent.camera.position.x -= 2;
                rect.set(sprite.getX() + parent.camera.position.x - parent.WIDTH / 2f + x_velocity,
                        sprite.getY() + parent.camera.position.y - parent.HEIGHT / 2f,
                        sprite.getWidth(), sprite.getHeight());
                objects = parent.getMapObjects(rect, objects, "platform");
                if (!objects.isEmpty()) parent.camera.position.x += 2;
            }
        }


        if (parent.camera.position.y < -100) {
            respawn();
        }
    }


    public void respawn() {
        currentLevelDeaths++;
        parent.camera.position.x = spawnLocation.x;
        parent.camera.position.y = spawnLocation.y;
        x_velocity = y_velocity = 0;
    }

    public void spawn() {
        parent.camera.position.x = spawnLocation.x;
        parent.camera.position.y = spawnLocation.y;
        y_velocity = x_velocity = 0;
        left = right = jumping = false;
    }

    public void findStart() {
        spawnLocation = new Rectangle();
        spawnLocation.set(0, 0, parent.MAP_WIDTH, parent.MAP_HEIGHT);
        objects = parent.getMapObjects(spawnLocation, objects, "spawn");
        spawnLocation.set(objects.first()); // 0
        spawnLocation.y += 1;
    }
}
