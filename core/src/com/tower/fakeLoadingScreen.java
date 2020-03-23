package com.tower;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class fakeLoadingScreen implements Screen {
    Stage stage = new Stage();
    Game parent;

    public fakeLoadingScreen(final Game parent) {
        Skin skin = new Skin(Gdx.files.internal("skin/star-soldier-ui.json"));
        this.parent = parent;
        TextButton btnContinue = new TextButton("Next Level", skin);
        btnContinue.setSize(parent.WIDTH / 5f, parent.HEIGHT / 8f);
        btnContinue.setPosition(parent.WIDTH - 300, 25);
        btnContinue.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.parent.change_screen(parent.parent.game);
            }
        });
        stage.addActor(btnContinue);
        Label lbl = new Label("Loading...", skin);
        lbl.setSize(parent.WIDTH / 5f, parent.HEIGHT / 8f);
        lbl.setPosition(50, 50);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
