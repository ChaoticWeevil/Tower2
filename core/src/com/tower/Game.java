package com.tower;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.strongjoshua.console.Console;
import com.strongjoshua.console.GUIConsole;
import com.tower.gameObjects.AndGate;
import com.tower.gameObjects.Gate;
import com.tower.gameObjects.OrGate;
import com.tower.gameObjects.gameObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class Game implements Screen, InputProcessor {
    public TiledMap map;
    public AssetManager manager = new AssetManager();
    OrthographicCamera camera = new OrthographicCamera();
    OrthogonalTiledMapRenderer renderer;
    SpriteBatch batch = new SpriteBatch();
    public Player player;
    BitmapFont font = new BitmapFont();
    Main parent;
    StretchViewport viewport;
    ShapeRenderer debugRenderer = new ShapeRenderer();
    Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override
        protected Rectangle newObject() {
            return new Rectangle();
        }
    };
    public Stage stage = new Stage();

    Boolean debug_mode;
    Boolean test = false;
    Boolean loadingScreens = true;

    Array<String> level_set = new Array<>();
    String[] gameObjects = {"Ladder", "Death", "Exit", "Fertilizer", "CarPart", "Switch"};
    String[] activeGameObjects = {"Gate", "AndGate", "OrGate"};
    ArrayList<String> loadingMessages = new ArrayList<>();
    Array<gameObject> activeObjects = new Array<>();
    public HashMap<Integer, Integer> signals = new HashMap<>();

    public final int WIDTH = 1366;
    public final int HEIGHT = 768;
    public final int MAX_SCORE = 900;
    int MAP_HEIGHT;
    int MAP_WIDTH;

    Console console = new GUIConsole();


    public int level_number = 0;

    public Game(final Main parent, String[] level_set) {
        this.parent = parent;
        // Asset loading
        manager.setLoader(TiledMap.class, new TmxMapLoader());
        for (int i = 0; i < level_set.length; i++) {
            this.level_set.add("maps/" + level_set[i]);
            manager.load(this.level_set.get(i), TiledMap.class);
        }
        String[] art = {"heart.png", "half_heart.png", "p_right.png", "p_left.png", "carPart.png", "Trees/Tree1.png", "Trees/Tree2.png", "Trees/Tree3.png", "Trees/Tree4.png", "Trees/Tree5.png", "Trees/Tree6.png",
                "switchRight.png", "switchLeft.png", "ers.png", "blankTile.png", "eKey.png"};
        for (String a : art) {
            manager.load(a, Texture.class);
        }
        manager.finishLoading();

        map = manager.get(this.level_set.get(level_number), TiledMap.class);
        renderer = new OrthogonalTiledMapRenderer(map);

        debugRenderer.setAutoShapeType(true);
        camera.setToOrtho(false, WIDTH, HEIGHT);
        camera.update();
        viewport = new StretchViewport(WIDTH, HEIGHT,// camera);
                new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        viewport.apply();
        viewport.getCamera().position.x = WIDTH / 2f;
        viewport.getCamera().position.y = HEIGHT / 2f;
        stage.setViewport(viewport);

        debug_mode = false;

        MAP_HEIGHT = map.getProperties().get("height", Integer.class) * 70;
        MAP_WIDTH = map.getProperties().get("width", Integer.class) * 70;
        player = new Player(this);

        // Initialise loading screen messages
        Scanner s = new Scanner(Gdx.files.internal("loadingMessages.txt").read());
        while (s.hasNextLine()) {
            loadingMessages.add(s.nextLine());
        }
        s.close();

        // Add active map objects to array
        MapObjects objects = map.getLayers().get("Collision_Layer").getObjects();
        for (MapObject object : objects) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            try {
                if ((Boolean) object.getProperties().get("Active")) {
                    try {
                        if (object.getProperties().get("Gate", Boolean.class))
                            activeObjects.add(new Gate(this, rect.x, rect.y, rect.width, rect.height));
                    } catch (NullPointerException ignored) {
                    }
                    try {
                        if (object.getProperties().get("AndGate", Boolean.class)) {
                            activeObjects.add(new AndGate(this, rect.x, rect.y, rect.width, rect.height));
                        }
                    } catch (NullPointerException ignored) {
                    }
                    if (object.getProperties().get("OrGate", Boolean.class)) {
                        activeObjects.add(new OrGate(this, rect.x, rect.y, rect.width, rect.height));
                    }
                }
            } catch (NullPointerException ignored) {
            }
            try {
                if (object.getProperties().get("HasCustomTexture", Boolean.class)) {
                    ((TiledMapTileLayer) map.getLayers().get("Things")).getCell((int) rect.x / 70, (int) rect.y / 70).getTile()
                            .setTextureRegion(new TextureRegion(manager.get(object.getProperties().get("OffTexture", String.class), Texture.class)));
                }
            } catch (NullPointerException ignored) {
            }
        }

        console.setCommandExecutor(new consoleCommands(this));
        console.setDisplayKeyID(Input.Keys.GRAVE);



        // Starts the game by running update method
        Timer.schedule(new Timer.Task() {
                           @Override
                           public void run() {
                               update();
                           }
                       }
                , 0.1f
                , 0.01f
        );
        // Tutorial Popup Window
        final Dialog d = new Dialog("Tutorial", new Skin(Gdx.files.internal("expeeSkin/expee-ui.json"))) {
            public void result(Object obj) {
                stage.clear();
                Gdx.input.setInputProcessor(parent.game);
                console.resetInputProcessing();

            }
        };
        Label text = new Label("Welcome to a prototype version of The Tower 2. \nTo move the player either use the left and right arrow keys or A and D.\nYou can jump and climb ladders with the spacebar or the up arrow.\nAs you progress" +
                " through the game your tree will grow larger\n based on how well you do in the game and how sustainable your actions are.\nMake sure to keep your eye out for special collectables.\nWhen you are ready press" +
                " the button below to start.", new Skin(Gdx.files.internal("expeeSkin/expee-ui.json")));
        d.getContentTable().align(Align.center);
        d.align(Align.topLeft);
        text.setAlignment(Align.center);
        d.text(text);
        d.button("Begin");
        d.setY(550);
        d.setX(450);
        d.setWidth(466);
        d.setHeight(168);
        stage.addActor(d);
        Gdx.input.setInputProcessor(stage);
        console.resetInputProcessing();
    }

    // Runs 100 times per second
    public void update() {
        player.update();
        camera.update();
        for (gameObject o : activeObjects) o.update();

    }

    @Override // Runs automatically whenever libGDX decides
    public void render(float delta) {
        Gdx.graphics.setTitle("Tower | FPS: " + Gdx.graphics.getFramesPerSecond());
        batch.setProjectionMatrix(viewport.getCamera().combined);
        Gdx.gl.glClearColor(.5f, .7f, .9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.setView(camera);
        renderer.render();
        batch.begin();
        player.render(batch);
        renderHud(batch);
        if (debug_mode) renderDebug(batch);
        batch.end();
        console.draw();
    }

    public void renderDebug(SpriteBatch batch) {
        debugRenderer.setProjectionMatrix(viewport.getCamera().combined);
        font.draw(batch, "Camera: " + camera.position.x + ", " + camera.position.y
                        + "\nPlayer: " + player.sprite.getX() + ", " + player.sprite.getY()
                        + "\nVelocity: " + player.x_velocity + ", " + player.y_velocity
                        + "\nFertilizer: " + player.fertilizerFound
                        + "\nCar Parts: " + player.carPartsFound
                        + "\nScore: " + player.score
                        + "\nSignals: " + signals
                , 5, HEIGHT - 2);
        batch.end();
        debugRenderer.begin();
        debugRenderer.rect(player.sprite.getX(), player.sprite.getY(), player.sprite.getWidth(), player.sprite.getHeight());
        debugRenderer.end();
        batch.begin();
    }

    public void renderHud(SpriteBatch batch) {
        font.draw(batch, "Tree Growth: " + (int) ((float) player.score / MAX_SCORE * 100) + "%", WIDTH / 2f - 40, HEIGHT - 10);
        batch.end();
        stage.act();
        stage.draw();
        batch.begin();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            Timer.instance().stop();
            player.left = player.right = player.jump = false;
            parent.change_screen(parent.menu);
            Gdx.input.setInputProcessor(parent.menu.play_stage);
        } else if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
            player.left = true;
        } else if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
            player.right = true;
        } else if (keycode == Input.Keys.SPACE || keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            player.jump = true;
        } else if (keycode == Input.Keys.E) {
            for (gameObject o : player.overlappedObjects) {
                o.onActivate();
            }
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
            player.left = false;
        }
        if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
            player.right = false;
        }
        if (keycode == Input.Keys.SPACE || keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            player.jump = false;
        }
        return false;
    }

    public Array<Rectangle> getTiles(Rectangle rect, Array<Rectangle> tiles, String check) {
        tiles.clear();
        MapLayer layer = map.getLayers().get("Collision_Layer");
        MapObjects objects = layer.getObjects();
        for (MapObject object : objects) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            rectangle.y = MAP_HEIGHT - rectangle.y;
            try {
                if (rect.overlaps(rectangle) && (check.equals("none") || (Boolean) object.getProperties().get(check))) {
                    tiles.add(rectangle);
                }
            } catch (NullPointerException ignored) {
            }

        }
        return tiles;
    }

    public void nextLevel() {
        level_number++;
        player.score += MathUtils.clamp((200 - player.currentLevelDeaths * 25), 50, 200);
        activeObjects.clear();
        if (level_number < level_set.size) {
            map = manager.get(this.level_set.get(level_number), TiledMap.class);
            renderer.setMap(map);
            player.spawn();

            // Add active map objects to array
            MapObjects objects = map.getLayers().get("Collision_Layer").getObjects();
            for (MapObject object : objects) {
                try {
                    if ((Boolean) object.getProperties().get("Active")) {
                        Rectangle rect = ((RectangleMapObject) object).getRectangle();
                        for (String check : activeGameObjects) {
                            if ("Gate".equals(check)) {
                                activeObjects.add(new Gate(this, rect.x, rect.y, rect.width, rect.height));
                            }
                        }
                    }
                } catch (NullPointerException ignored) {
                }
            }
            if (loadingScreens) parent.change_screen(new fakeLoadingScreen(this));
        }
        else wonGame();
    }

    public void wonGame() {
        final Dialog d = new Dialog("Congratulations", new Skin(Gdx.files.internal("expeeSkin/expee-ui.json"))) {
            public void result(Object obj) {
                stage.clear();
                System.exit(0);

            }
        };
        Label text = new Label("Congratulations you have finished the prototype version of The Tower 2.\nYou had a total tree growth of " + (int) ((float) player.score / MAX_SCORE * 100) + "%"
                + ".\nYou collected "  + player.carPartsFound + " electric car parts.", new Skin(Gdx.files.internal("expeeSkin/expee-ui.json")));
        d.getContentTable().align(Align.center);
        d.align(Align.topLeft);
        text.setAlignment(Align.center);
        d.text(text);
        d.button("Exit Game");
        d.setY(550);
        d.setX(450);
        d.setWidth(466);
        d.setHeight(168);
        stage.addActor(d);
        Gdx.input.setInputProcessor(stage);
    }

    // Unused Methods
    @Override
    public void dispose() {
        // Free resources
        manager.dispose();
    }

    @Override
    public void show() {
        Timer.instance().start();
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
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}