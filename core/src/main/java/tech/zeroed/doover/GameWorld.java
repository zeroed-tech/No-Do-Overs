package tech.zeroed.doover;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Rect;
import com.dongbat.jbump.World;
import space.earlygrey.shapedrawer.ShapeDrawer;
import tech.zeroed.doover.gameobjects.GameObject;

public class GameWorld {

    public static int TILE_SIZE = 16;

    public static TextureAtlas worldSprites;
    public static SnapshotArray<GameObject> gameObjects;
    public static World<GameObject> world;
    public static SpriteBatch spriteBatch;
    public static OrthographicCamera camera;
    public static OrthographicCamera hudCamera;
    public static ExtendViewport viewport;
    public static ShapeDrawer shapeDrawer;
    public static BitmapFont font;

    public GameWorld() {
        worldSprites = new TextureAtlas(Gdx.files.internal("sprites/Sprites.atlas"));
        world = new World<>(TILE_SIZE);
        gameObjects = new SnapshotArray<>();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.drawPixel(0, 0);
        Texture texture = new Texture(pixmap); //remember to dispose of later
        pixmap.dispose();

        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        hudCamera = new OrthographicCamera(1280, 720);
        hudCamera.position.z = 1;
        hudCamera.update();

        camera.position.set(0, 0, 0);
        viewport = new ExtendViewport(1280, 720, camera);
        shapeDrawer = new ShapeDrawer(spriteBatch, new TextureRegion(texture, 0, 0, 1, 1));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/OpenSans-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 25;
        font = generator.generateFont(parameter);
        generator.dispose();

        Level.Load("Test");

    }

    public void update(float delta){
        checkInput();
        for (GameObject entity : gameObjects) {
            entity.run(delta);
        }
    }

    public void draw(float delta){
        viewport.apply();

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        for (GameObject entity : gameObjects) {
            Item<GameObject> item = entity.item;
            item.userData.draw(spriteBatch);
        }

        for (GameObject entity : gameObjects) {
            Item<GameObject> item = entity.item;
            if (item != null) {
                shapeDrawer.setColor(Color.RED);
                shapeDrawer.setDefaultLineWidth(1.0f);
                Rect rect = world.getRect(item);
                shapeDrawer.rectangle(rect.x, rect.y, rect.w, rect.h);
            }
        }

        spriteBatch.setProjectionMatrix(hudCamera.combined);
        font.draw(spriteBatch, "Camera: X: "+(int)camera.position.x+" Y: "+(int)camera.position.y+" Zoom: "+camera.zoom+"", -hudCamera.viewportWidth/2+10, hudCamera.viewportHeight/2-10);

        for (GameObject entity : gameObjects) {
            Item<GameObject> item = entity.item;
            if (item != null) {
                entity.debugDraw(spriteBatch, font);
            }
        }

        spriteBatch.end();
    }

    public static void addGameObjectToWorld(GameObject gameObject){
        gameObjects.add(gameObject);
        GameWorld.world.add(gameObject.item,
                gameObject.x + gameObject.boundingBoxX,
                gameObject.y + gameObject.boundingBoxY,
                gameObject.boundingBoxWidth,
                gameObject.boundingBoxHeight);
    }

    public static void removeGameObjectFromWorld(GameObject gameObject) {
        gameObjects.removeValue(gameObject, true);
        world.remove(gameObject.item);
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    private void checkInput(){
//        if(Gdx.input.isKeyPressed(Input.Keys.S)){
//            camera.position.y -= 1f/camera.zoom;
//        }
//        if(Gdx.input.isKeyPressed(Input.Keys.W)){
//            camera.position.y += 1f/camera.zoom;
//        }
//        if(Gdx.input.isKeyPressed(Input.Keys.A)){
//            camera.position.x -= 1f/camera.zoom;
//        }
//        if(Gdx.input.isKeyPressed(Input.Keys.D)){
//            camera.position.x += 1f/camera.zoom;
//        }
        if(Gdx.input.isKeyPressed(Input.Keys.Q)){
            camera.zoom += 0.01f;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.E)){
            camera.zoom -= 0.01f;
        }

        clampCamera();
        camera.update();
    }

    public static void clampCamera(){
        // Camera clamping code from https://stackoverflow.com/questions/47644078/clamp-camera-to-map-zoom-issue
        camera.zoom = MathUtils.clamp(camera.zoom, 0.3f, 1);

        float zoomedHalfWorldWidth = camera.zoom * camera.viewportWidth / 2;
        float zoomedHalfWorldHeight = camera.zoom * camera.viewportHeight / 2;

        //min and max values for camera's x coordinate
        float minX = zoomedHalfWorldWidth;
        float maxX = Level.TILE_WIDTH*TILE_SIZE - zoomedHalfWorldWidth;

        //min and max values for camera's y coordinate
        float minY = zoomedHalfWorldHeight;
        float maxY = Level.TILE_HEIGHT*TILE_SIZE - zoomedHalfWorldHeight;

        camera.position.x = MathUtils.clamp(camera.position.x, minX, maxX);
        if(minY < maxY)
            camera.position.y = MathUtils.clamp(camera.position.y, minY, maxY);
        else
            camera.position.y = minY;
    }
}
