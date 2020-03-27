package com.tower;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Timer;

public class fakeLoadingScreen implements Screen {
    private Stage stage = new Stage();
    private Game parent;
    private BitmapFont font = new BitmapFont();
    private Skin skin = new Skin(Gdx.files.internal("skin/star-soldier-ui.json"));
    private SpriteBatch batch = new SpriteBatch();
    private String loadingDots = ".";
    private float loadingTime = 0;

    fakeLoadingScreen(final Game parent) {
        this.parent = parent;
        stage.setViewport(parent.viewport);
        font.setColor(Color.WHITE);
        font.getData().setScale(2);
        final TextButton btnContinue = new TextButton("Next Level", skin);
        btnContinue.setSize(parent.WIDTH / 5f, parent.HEIGHT / 8f);
        btnContinue.setPosition(parent.WIDTH - 300, 25);
        btnContinue.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Timer.instance().clear();
                Timer.schedule(new Timer.Task() {
                                   @Override
                                   public void run() {
                                       parent.update();
                                   }
                               }
                        , 0.1f
                        , 0.01f
                );
                parent.parent.change_screen(parent.parent.game);
            }
        });
        btnContinue.setVisible(false);
        stage.addActor(btnContinue);
        Timer.schedule(new Timer.Task() {
                           @Override
                           public void run() {
                               loadingTime += 0.5f;
                               loadingDots += ".";
                               if (loadingDots.length() > 3) loadingDots = ".";
                               if (loadingTime > 4) btnContinue.setVisible(true);
                           }
                       }
                , 1
                , 0.5f
        );
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
        batch.begin();
        font.draw(batch, "Loading" + loadingDots, 10, 50);
        batch.end();
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
