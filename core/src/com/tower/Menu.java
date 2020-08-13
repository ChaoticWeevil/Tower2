package com.tower;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Menu implements Screen {
    FitViewport viewport;
    Stage main_stage;
    Stage play_stage;
    Stage death_stage;
    Stage stage;
    private final Main parent;
    TextButton play;
    TextButton exit;
    TextButton new_game;
    TextButton continue_game;
    TextButton back;
    TextButton back2;

    public Menu(Main tower) {
        viewport = new FitViewport(1366f, 768f,
                new OrthographicCamera(1366f, 768f));
        final int SCREEN_HEIGHT = 768;
        final int SCREEN_WIDTH = 1366;
        parent = tower;

        main_stage = new Stage(viewport);
        play_stage = new Stage(viewport);
        death_stage = new Stage(viewport);
        stage = main_stage;
        Gdx.input.setInputProcessor(main_stage);
        Skin skin = new Skin(Gdx.files.internal("skin/star-soldier-ui.json"));

        // Main Stage
        Label title = new Label("The Tower", skin);
        title.setFontScale(3);
        title.setPosition(SCREEN_WIDTH / 2f - title.getWidth() - 75, SCREEN_HEIGHT * 3 / 4f);
        main_stage.addActor(title);

        play = new TextButton("Play", skin);
        play.setSize(SCREEN_WIDTH / 5f, SCREEN_HEIGHT / 8f);
        play.setPosition(SCREEN_WIDTH / 2f - play.getWidth() / 2, SCREEN_HEIGHT / 2f);


        exit = new TextButton("Exit", skin);
        exit.setSize(SCREEN_WIDTH / 5f, SCREEN_HEIGHT / 8f);
        exit.setPosition(SCREEN_WIDTH / 2f - exit.getWidth() / 2, SCREEN_HEIGHT / 2f - 100);


        main_stage.addActor(play);
        main_stage.addActor(exit);

        // Play Stage
        final CheckBox timeCheckbox = new CheckBox(" Time Trial", skin);
        Label title2 = new Label("The Tower", skin);
        new_game = new TextButton("New Game", skin);
        continue_game = new TextButton("Continue Game", skin);
        continue_game.setVisible(false);
        back = new TextButton("Back", skin);

        title2.setFontScale(3);
        continue_game.setSize(SCREEN_WIDTH / 5f, SCREEN_HEIGHT / 8f);
        new_game.setSize(SCREEN_WIDTH / 5f, SCREEN_HEIGHT / 8f);
        back.setSize(SCREEN_WIDTH / 5f, SCREEN_HEIGHT / 8f);

        title2.setPosition(SCREEN_WIDTH / 2f - title.getWidth() - 75, SCREEN_HEIGHT * 3 / 4f);
        continue_game.setPosition(SCREEN_WIDTH / 2f - play.getWidth() / 2, SCREEN_HEIGHT / 2f);
        new_game.setPosition(SCREEN_WIDTH / 2f - exit.getWidth() / 2, SCREEN_HEIGHT / 2f - 100);
        back.setPosition(SCREEN_WIDTH / 2f - exit.getWidth() / 2, SCREEN_HEIGHT / 2f - 300);

        play_stage.addActor(title2);
        play_stage.addActor(new_game);
        play_stage.addActor(continue_game);
        play_stage.addActor(back);

        // Game Over Stage
        Label game_over = new Label("Game Over", skin);
        back2 = new TextButton("Back", skin);

        game_over.setFontScale(3);
        back2.setSize(SCREEN_WIDTH / 5f, SCREEN_HEIGHT / 8f);

        game_over.setPosition(SCREEN_WIDTH / 2f - title.getWidth() - 75, SCREEN_HEIGHT * 3 / 4f);
        back2.setPosition(SCREEN_WIDTH / 2f - exit.getWidth() / 2, SCREEN_HEIGHT / 2f);


        death_stage.addActor(game_over);
        death_stage.addActor(back2);

        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        play.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stage = play_stage;
                Gdx.input.setInputProcessor(play_stage);
            }
        });
        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stage = main_stage;
                Gdx.input.setInputProcessor(main_stage);
            }
        });
        continue_game.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(parent.game);
                parent.change_screen(parent.game);
                Gdx.input.setInputProcessor(parent.game);
                parent.game.console.resetInputProcessing();
            }
        });
        new_game.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    parent.game.dispose();

                } catch (NullPointerException ignored) {}
                parent.game = new Game(parent, parent.level_set_1, false, timeCheckbox.isChecked());
                parent.change_screen(parent.game);
                continue_game.setVisible(true);

            }
        });
        back2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stage = main_stage;
                Gdx.input.setInputProcessor(main_stage);
            }
        });

        TextButton hardcoreButton = new TextButton("Hardcore Game", skin);
        hardcoreButton.setSize(SCREEN_WIDTH / 5f, SCREEN_HEIGHT / 8f);
        hardcoreButton.setPosition(SCREEN_WIDTH / 2f - exit.getWidth() / 2, SCREEN_HEIGHT / 2f - 200);
        hardcoreButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    parent.game.dispose();

                } catch (NullPointerException ignored) {}
                parent.game = new Game(parent, parent.level_set_1, true, timeCheckbox.isChecked());
                parent.change_screen(parent.game);
                continue_game.setVisible(true);
            }
        });
        play_stage.addActor(hardcoreButton);


        timeCheckbox.setPosition(SCREEN_WIDTH / 2f - exit.getWidth() / 2 + 270, SCREEN_HEIGHT / 2f - 160);
        play_stage.addActor(timeCheckbox);
        
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.5f, .7f, .9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}