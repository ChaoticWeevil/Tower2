package com.tower.gameObjects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;
import com.tower.Game;


public class MovingPlatform extends gameObject {
    Game parent;
    int ID;
    public float currentX;
    public float currentY;
    public float collisionWidth;
    public float collisionHeight;
    public String texture;
    public int numTextures;
    public float speed;
    public Rectangle rect;
    public String currentDirection = "RIGHT";
    MapObject object;
    public float playerSpeed;


    public MovingPlatform(Game parent, float x, float y, float width, float height, int ID, String texture, float speed, int numTextures, float collisionWidth,
                          float collisionHeight, MapObject object) {
        this.parent = parent;
        this.numTextures = numTextures;
        this.object = object;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.ID = ID;
        this.texture = texture;
        this.speed = speed;
        this.collisionWidth = collisionWidth;
        this.collisionHeight = collisionHeight;
        try {
            playerSpeed = object.getProperties().get("playerSpeed", float.class);
            currentDirection = object.getProperties().get("direction", String.class);
        }
        catch (NullPointerException e) {
            playerSpeed = speed * 2;

        }

        currentX = x;
        currentY = y;

        rect = new com.badlogic.gdx.math.Rectangle(currentX, currentY, collisionWidth, collisionHeight);
        parent.movingPlatforms.add(this);
    }

    public void updateRect() {
        rect.set(currentX, currentY, collisionWidth, collisionHeight);
    }

    @Override
    public void update() {
        if (speed == 0) {

        }
        else {
            if (currentDirection.equals("RIGHT")) {
                if ((currentX + collisionWidth) < width + x) {
                    currentX += speed;
                } else {
                    currentDirection = "LEFT";
                }
            }
            if (currentDirection.equals("LEFT")) {
                if (currentX > x) {
                    currentX -= speed;
                } else {
                    currentDirection = "RIGHT";
                }
            }
        }

        updateRect();
    }
}
