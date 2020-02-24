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
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;


public class Game implements Screen, InputProcessor {
    TiledMap map;
    AssetManager manager;
    OrthographicCamera camera;
    OrthogonalTiledMapRenderer renderer;
    SpriteBatch batch;
    Player player;
    BitmapFont font;
    Main parent;
    StretchViewport viewport;
    ShapeRenderer debugRenderer;

    Boolean debug_mode;
    final int WIDTH = 1366;
    final int HEIGHT = 768;

    public Game(Main parent) {
        this.parent = parent;
        // Asset loading
        manager = new AssetManager();
        manager.setLoader(TiledMap.class, new TmxMapLoader());

        manager.load("maps/level_1.tmx", TiledMap.class);
        manager.load("heart.png", Texture.class);
        manager.load("half_heart.png", Texture.class);
        manager.load("up.png", Texture.class);
        manager.load("left.png", Texture.class);
        manager.load("right.png", Texture.class);
        manager.load("p_right.png", Texture.class);
        manager.load("p_left.png", Texture.class);
        manager.finishLoading();

        map = manager.get("maps/level_1.tmx", TiledMap.class);
        renderer = new OrthogonalTiledMapRenderer(map);
        batch = new SpriteBatch();
        player = new Player(this);
        debugRenderer = new ShapeRenderer();
        debugRenderer.setAutoShapeType(true);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);
        camera.update();
        font = new BitmapFont();
        viewport = new StretchViewport(WIDTH, HEIGHT,// camera);
                new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        viewport.apply();
        viewport.getCamera().position.x = WIDTH/2f;
        viewport.getCamera().position.y = HEIGHT/2f;

        debug_mode = false;

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
        Gdx.graphics.setTitle("Tower |FPS: " + Gdx.graphics.getFramesPerSecond());
        batch.setProjectionMatrix(viewport.getCamera().combined);
        Gdx.gl.glClearColor(0, 0, 0, 1);
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

    public Boolean collisionCheck(Rectangle rect, String check) {
        MapLayer layer = map.getLayers().get("Collision_Layer");
        MapObjects objects = layer.getObjects();
        for (MapObject object : objects) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            Boolean platform = (Boolean) object.getProperties().get(check);
            try {
                if (rectangle.overlaps(rect) && platform) {
                    return true;
                }
            } catch (Exception e) {
                System.out.println("ERROR: collisionCheck error - " + e);
            }
        }
        return false;
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