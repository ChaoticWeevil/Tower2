package com.tower;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class fakeLoadingScreen implements Screen {
    Stage stage;
    Game parent;

    public fakeLoadingScreen(final Game parent) {
        Skin skin = new Skin(Gdx.files.internal("skin/star-soldier-ui.json"));
        this.parent = parent;
        stage = new Stage();
        TextButton btnContinue = new TextButton("Continue Game", skin);
        btnContinue.setSize(parent.WIDTH / 5f, parent.HEIGHT / 8f);
        btnContinue.setPosition(parent.WIDTH / 2f - btnContinue.getWidth() / 2, parent.HEIGHT / 2f);
        btnContinue.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.parent.change_screen(parent.parent.game);
            }
        });

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
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
