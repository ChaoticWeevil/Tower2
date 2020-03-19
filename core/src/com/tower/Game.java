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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;


public class Game implements Screen, InputProcessor {
    TiledMap map;
    AssetManager manager;
    OrthographicCamera camera;
    OrthogonalTiledMapRenderer renderer;
    SpriteBatch batch;
    public Player player;
    BitmapFont font;
    Main parent;
    StretchViewport viewport;
    ShapeRenderer debugRenderer;
    Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override
        protected Rectangle newObject() {
            return new Rectangle();
        }
    };

    Boolean debug_mode;
    Boolean test = false;

    String[] level_set;
    String[] gameObjects = {"Ladder", "Death", "Exit"};

    final int WIDTH = 1366;
    final int HEIGHT = 768;
    int MAP_HEIGHT;
    int MAP_WIDTH;
    public int level_number = 0;

    public Game(Main parent, String[] level_set) {
        this.parent = parent;
        this.level_set = level_set;
        // Asset loading
        manager = new AssetManager();
        manager.setLoader(TiledMap.class, new TmxMapLoader());

        for (int i = 0; i < level_set.length; i++) {
            level_set[i] = "maps/" + level_set[i];
            manager.load(level_set[i], TiledMap.class);
        }
        String[] art = {"heart.png", "half_heart.png", "up.png", "left.png", "right.png", "p_right.png", "p_left.png"};
        for (String a : art) {
            manager.load(a, Texture.class);
        }
        manager.finishLoading();

        map = manager.get(level_set[level_number], TiledMap.class);
        renderer = new OrthogonalTiledMapRenderer(map);
        batch = new SpriteBatch();

        debugRenderer = new ShapeRenderer();
        debugRenderer.setAutoShapeType(true);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);
        camera.update();
        font = new BitmapFont();
        viewport = new StretchViewport(WIDTH, HEIGHT,// camera);
                new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        viewport.apply();
        viewport.getCamera().position.x = WIDTH / 2f;
        viewport.getCamera().position.y = HEIGHT / 2f;

        debug_mode = false;

        MAP_HEIGHT = map.getProperties().get("height", Integer.class) * 70;
        MAP_WIDTH = map.getProperties().get("width", Integer.class) * 70;
        player = new Player(this);

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

    // Runs 100 times per second
    public void update() {
        player.update();
        camera.update();
    }

    @Override // Runs automatically whenever libgdx decides
    public void render(float delta) {
        Gdx.graphics.setTitle("Tower | FPS: " + Gdx.graphics.getFramesPerSecond());
        batch.setProjectionMatrix(viewport.getCamera().combined);
        Gdx.gl.glClearColor(.5f, .7f, .9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.setView(camera);
        renderer.render();
        batch.begin();
        player.render(batch);
        if (debug_mode) renderDebug(batch);
        batch.end();
    }

    public void renderDebug(SpriteBatch batch) {
        debugRenderer.setProjectionMatrix(viewport.getCamera().combined);
        font.draw(batch, "Camera: " + camera.position.x + ", " + camera.position.y
                        + "\nPlayer: " + player.sprite.getX() + ", " + player.sprite.getY()
                        + "\nVelocity: " + player.x_velocity + ", " + player.y_velocity
                        + "\nTest: " + player.onLadder
                , 5, HEIGHT - 2);
        batch.end();
        debugRenderer.begin();
        debugRenderer.rect(player.sprite.getX(), player.sprite.getY(), player.sprite.getWidth(), player.sprite.getHeight());
        debugRenderer.end();
        batch.begin();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            parent.change_screen(parent.menu);
            Gdx.input.setInputProcessor(parent.menu.play_stage);
        }
        if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
            player.left = true;
        }
        if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
            player.right = true;
        }
        if (keycode == Input.Keys.SPACE) {
            player.jump = true;
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
        if (keycode == Input.Keys.SPACE) {
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
        map = manager.get(level_set[level_number], TiledMap.class);
        renderer.setMap(map);
        player.spawn();
    }


    // Unused Methods
    @Override
    public void dispose() {
        // Free resources
        manager.dispose();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
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
        if (character == "`".charAt(0)) debug_mode = !debug_mode;
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