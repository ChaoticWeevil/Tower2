package com.tower.gameObjects;

public class gameObject {
    public float x, y, width, height;
    public void overlappedUpdate() {
    } // Runs every update method if overlapped by player

    public void update() {
    } // Runs every update method if in activeObjects array

    public void onEnter() {
    } // Runs when player enters object

    public void onExit() {
    } // Runs when player exits object
}
