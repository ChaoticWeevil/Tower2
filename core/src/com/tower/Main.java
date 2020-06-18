package com.tower;

import com.badlogic.gdx.Screen;


public class Main extends com.badlogic.gdx.Game {
    Menu menu;
    Game game;

    final String[] level_set_1 = {"level_1-1.tmx", "level_1-2.tmx", "level_1-3.tmx"};

    @Override
    public void create() {
        menu = new Menu(this);
        change_screen(menu);
    }

    public void change_screen(Screen newScreen) {
        setScreen(newScreen);
    }
}