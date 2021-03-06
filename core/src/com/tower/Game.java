package com.tower;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
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
import com.tower.gameObjects.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;


public class Game implements Screen, InputProcessor {
    public final int WIDTH = 1366;
    public final int HEIGHT = 768;
    public final int MAX_SCORE = 900;
    public int deathCounter = 0;
    public TiledMap map;
    public final AssetManager manager = new AssetManager();
    public Player player;
    public final Stage stage = new Stage();
    public final HashMap<Integer, Integer> signals = new HashMap<>();
    public int level_number = 0;
    public final OrthographicCamera camera = new OrthographicCamera();
    final OrthogonalTiledMapRenderer renderer;
    final SpriteBatch batch = new SpriteBatch();
    final BitmapFont font = new BitmapFont();
    final Main parent;
    final StretchViewport viewport;
    final ShapeRenderer debugRenderer = new ShapeRenderer();
    Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override
        protected Rectangle newObject() {
            return new Rectangle();
        }
    };
    Boolean debug_mode;
    Boolean loadingScreens = true;
    final Array<String> level_set = new Array<>();
    String[] gameObjects = {"Ladder", "Death", "Exit", "Fertilizer", "CarPart", "Switch", "SpawnPoint", "Trigger", "ToggleDeath"};
    public Array<MovingPlatform> movingPlatforms = new Array<>();
    final ArrayList<String> loadingMessages = new ArrayList<>();
    final Array<gameObject> activeObjects = new Array<>();
    int MAP_HEIGHT;
    int MAP_WIDTH;
    final Console console = new GUIConsole();
    boolean hardcore;
    boolean timeTrial;
    float timeTrialTime = 0;
    public boolean noclip;

    public Game(final Main parent, String[] level_set, boolean hardcore, boolean timeTrial) {
        this.parent = parent;
        this.hardcore = hardcore;
        this.timeTrial = timeTrial;
        // Asset loading
        manager.setLoader(TiledMap.class, new TmxMapLoader());
        for (int i = 0; i < level_set.length; i++) {
            this.level_set.add("maps/" + level_set[i]);
            manager.load(this.level_set.get(i), TiledMap.class);
        }

        String[] art = {"maps/tiles/carPart.png", "Textures/Trees/Tree1.png", "Textures/laser.png",
                "Textures/Trees/Tree2.png", "Textures/Trees/Tree3.png", "Textures/Trees/Tree4.png", "Textures/Trees/Tree5.png", "Textures/Trees/Tree6.png",
                "Textures/ers.png", "maps/tiles/blankTile.png", "Textures/eKey.png", "maps/tiles/blankTile.png", "Textures/oil.png",
                "Textures/player/jump-5.png", "Textures/cGate.png", "Textures/eKey.png", "Textures/cGateOff.png"};
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
        for (int num = 0; num < 10; num++) {
            Iterator<String> iterator = map.getProperties().getKeys();
            while (iterator.hasNext()) {
                int mapNum;
                try {
                    mapNum = Integer.parseInt(iterator.next());
                } catch (Exception ignored) {
                    continue;
                }
                if (mapNum == num) {

                    signals.put(num, map.getProperties().get(String.valueOf(mapNum), Integer.class));
                }
            }
        }
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
                    try {
                        if (object.getProperties().get("MovingPlatform", Boolean.class)) {
                            activeObjects.add(new MovingPlatform(this, rect.x, rect.y, rect.width, rect.height
                                    , object.getProperties().get("ID", Integer.class)
                                    , object.getProperties().get("Texture", String.class), object.getProperties().get("Speed", float.class)
                                    , object.getProperties().get("numTextures", int.class), object.getProperties().get("collisionWidth", float.class)
                                    , object.getProperties().get("collisionHeight", float.class), object));
                        }
                    } catch (NullPointerException ignored) {
                    }
                    try {
                        if (object.getProperties().get("ToggleDeath", Boolean.class)) {
                            activeObjects.add(new ToggleDeath(this, rect.x, rect.y, rect.width, rect.height, object,
                                    object.getProperties().get("control", String.class)));
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


        // Tutorial Popup Window
        final Dialog d = new Dialog("Tutorial", new Skin(Gdx.files.internal("expeeSkin/expee-ui.json"))) {
            public void result(Object obj) {
                stage.clear();
                Gdx.input.setInputProcessor(parent.game);
                console.resetInputProcessing();
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

            }
        };
        Label text = new Label("Welcome to a prototype version of The Tower 2. \nTo move the player either use the left and right arrow keys or A and D." +
                "\nYou can jump and climb ladders with the spacebar or the up arrow.\nAs you progress through the game your tree will grow larger\n based on" +
                " how well you do in the game and how sustainable your actions are.\nMake sure to keep your eye out for special collectables.\nWhen you are" +
                " ready press the button below to start.", new Skin(Gdx.files.internal("expeeSkin/expee-ui.json")));
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
        update();
    }

    // Runs 100 times per second
    public void update() {
        player.update();
        camera.update();
        for (gameObject o : activeObjects) o.update();
        for (MovingPlatform p : movingPlatforms) p.update();
        timeTrialTime += 0.01f;

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
        for (MovingPlatform platform : movingPlatforms) {
            for (int i = 1; i <= platform.numTextures; i++) {
                if (!platform.texture.equals("NONE")) {
                    batch.draw(manager.get(platform.texture, Texture.class), platform.currentX - camera.position.x + WIDTH / 2f + (i - 1)
                                    * manager.get(platform.texture, Texture.class).getWidth(),
                            platform.currentY - camera.position.y + HEIGHT / 2f);
                }
            }

        }


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
                        + "\nLeft: " + player.left + " |Right: " + player.right
                        + "\nMovingPlatform: " + player.onMovingPlatform
                        + "\nGrounded" + player.grounded
                , 5, HEIGHT - 2);
        batch.end();
        debugRenderer.begin();
        debugRenderer.setColor(Color.WHITE);
        debugRenderer.rect(player.sprite.getX(), player.sprite.getY(), player.sprite.getWidth(), player.sprite.getHeight());
        debugRenderer.end();
        batch.begin();
    }

    public void renderHud(SpriteBatch batch) {
        font.draw(batch, "Tree Growth: " + (int) ((float) player.score / MAX_SCORE * 100) + "%", WIDTH / 2f - 40, HEIGHT - 10);
        font.draw(batch, "Deaths: " + deathCounter, 1270, 760);
        if (timeTrial) font.draw(batch, "Time: " + Math.round(timeTrialTime * 10) / 10f, 1270, 740);
        batch.end();
        stage.act();
        stage.draw();
        batch.begin();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        console.refresh();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            Timer.instance().stop();
            player.left = player.right = player.jumping = false;
            parent.change_screen(parent.menu);
            Gdx.input.setInputProcessor(parent.menu.play_stage);
            console.resetInputProcessing();
        } else if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
            player.left = true;
        } else if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
            player.right = true;
        } else if (keycode == Input.Keys.SPACE || keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            player.jumping = true;
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
            player.jumping = false;
        }
        return false;
    }

    public Array<Rectangle> getMapObjects(Rectangle rect, Array<Rectangle> tiles, String check) {
        tiles.clear();
        MapLayer layer = map.getLayers().get("Collision_Layer");
        MapObjects objects = layer.getObjects();
        for (MapObject object : objects) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            try {
                if (rect.overlaps(rectangle) && (check.equals("none") || object.getProperties().get(check, Boolean.class))) {
                    tiles.add(rectangle);
                }
            } catch (NullPointerException ignored) {
            }

        }
        return tiles;
    }

    public Array<MovingPlatform> getMovingPlatforms(Rectangle rect, Array<MovingPlatform> tiles) {
        tiles.clear();
        for (MovingPlatform p : movingPlatforms) {
            p.updateRect();
            if (rect.overlaps(p.rect) || p.rect.overlaps(rect)) {
                tiles.add(p);
            }
        }
        return tiles;
    }

    public void nextLevel() {
        level_number++;
        player.score += MathUtils.clamp((200 - player.currentLevelDeaths * 25), 50, 200);
        activeObjects.clear();
        movingPlatforms.clear();
        signals.clear();
        if (level_number < level_set.size) {
            map = manager.get(this.level_set.get(level_number), TiledMap.class);
            renderer.setMap(map);
            MAP_HEIGHT = map.getProperties().get("height", Integer.class) * 70;
            MAP_WIDTH = map.getProperties().get("width", Integer.class) * 70;
            player.findStart();
            player.spawn();

            // Add active map objects to array
            MapObjects objects = map.getLayers().get("Collision_Layer").getObjects();
            for (MapObject object : objects) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                try {
                    if ((Boolean) object.getProperties().get("Active")) {
                        try {
                            if (object.getProperties().get("MovingPlatform", Boolean.class)) {
                                activeObjects.add(new MovingPlatform(this, rect.x, rect.y, rect.width, rect.height
                                        , object.getProperties().get("ID", Integer.class)
                                        , object.getProperties().get("Texture", String.class), object.getProperties().get("Speed", float.class)
                                        , object.getProperties().get("numTextures", int.class), object.getProperties().get("collisionWidth", float.class)
                                        , object.getProperties().get("collisionHeight", float.class), object));
                            }
                        } catch (NullPointerException ignored) {
                        }
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
                        try {
                            if (object.getProperties().get("ToggleDeath", Boolean.class))
                                activeObjects.add(new ToggleDeath(this, rect.x, rect.y, rect.width, rect.height, object
                                        , object.getProperties().get("control", String.class)));
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
            if (loadingScreens) {
                Timer.instance().clear();
                parent.change_screen(new fakeLoadingScreen(this));
            }
        } else wonGame();
    }

    public void wonGame() {
        final Dialog d = new Dialog("Congratulations", new Skin(Gdx.files.internal("expeeSkin/expee-ui.json"))) {
            public void result(Object obj) {
                stage.clear();
                System.exit(0);

            }
        };
        final Dialog d1 = new Dialog("Summary", new Skin(Gdx.files.internal("expeeSkin/expee-ui.json"))) {
            public void result(Object obj) {
                d.setVisible(true);
            }
        };
        d1.toFront();
        Label text1;
        if (timeTrial) {
            text1 = new Label("You completed the game dying " + deathCounter + " times with a time of " +
                    Math.round(timeTrialTime * 10) / 10f + " seconds.", new Skin(Gdx.files.internal("expeeSkin/expee-ui.json")));
        } else {
            text1 = new Label("You completed the game dying " + deathCounter + " times.", new Skin(Gdx.files.internal("expeeSkin/expee-ui.json")));
        }

        d1.getContentTable().align(Align.center);
        d1.align(Align.topLeft);
        text1.setAlignment(Align.center);
        d1.text(text1);
        d1.button("Continue");
        d1.setY(550);
        d1.setX(450);
        d1.setWidth(466);
        d1.setHeight(168);

        Label text = new Label("Congratulations you have finished the prototype version of The Tower 2.\nYou had a total tree growth of " +
                (int) ((float) player.score / MAX_SCORE * 100) + "%.\nYou collected " + player.carPartsFound + " electric car parts.\nYou died " +
                deathCounter + " times.", new Skin(Gdx.files.internal("expeeSkin/expee-ui.json")));
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
        stage.addActor(d1);

        Gdx.input.setInputProcessor(stage);
        console.resetInputProcessing();
        player.y_velocity = player.x_velocity = 0;
        player.right = player.left = player.jumping = false;
        Timer.instance().clear();
    }


    // Unused Methods
    @Override
    public void dispose() {
        Timer.instance().clear();
        // Free resources
        manager.dispose();
        batch.dispose();
        debugRenderer.dispose();
        console.dispose();
        map.dispose();
        renderer.dispose();
        stage.dispose();
        font.dispose();
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