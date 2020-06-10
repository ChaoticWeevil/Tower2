package com.tower.gameObjects;

import com.badlogic.gdx.math.Rectangle;
import com.tower.Game;


public class MovingPlatform extends gameObject {
    Game parent;
    int ID;
    public float currentX;
    public float currentY;
    public float collisionWidth = 70;
    public float collisionHeight = 70;
    public String axis;
    public String texture;
    public int numTextures;
    public Rectangle rect;
    public String currentDirection = "RIGHT";

    public MovingPlatform(Game parent, float x, float y, float width, float height, int ID, String axis, String texture) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.ID = ID;
        this.axis = axis;
        this.texture = texture;

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
        if (currentDirection.equals("RIGHT")) {
            if ((currentX + collisionWidth) < width + x) {
                currentX++;
            }
            else {
                currentDirection = "LEFT";
            }
        }
        if (currentDirection.equals("LEFT")) {
            if (currentX > x) {
                currentX--;
            }
            else {
                currentDirection = "RIGHT";
            }
        }
        updateRect();
    }
}
