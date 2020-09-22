package tech.zeroed.doover.gameobjects;

import com.badlogic.gdx.graphics.g2d.*;
import com.dongbat.jbump.Item;

public abstract class GameObject {

    public static void test(){

    }

    public TextureRegion sprite;
    public Animation<TextureAtlas.AtlasRegion> animation;
    public float animationTime;

    public float x;
    public float y;
    public float deltaX;
    public float deltaY;
    public float boundingBoxX;
    public float boundingBoxY;
    public float boundingBoxWidth;
    public float boundingBoxHeight;
    public float rotation;

    public boolean flipX;
    public boolean flipY;

    public float gravityX;
    public float gravityY;

    public Item<GameObject> item;

    /**
     * Called in the update loop. Should be overridden by subclasses to implement any
     * update logic needed
     * @param delta time in seconds since the last update cycle
     */
    public abstract void run(float delta);

    /**
     * Draws this object's sprite if it exists to the passed in batch
     * @param batch SpriteBatch to draw to
     */
    public void draw(SpriteBatch batch){
        TextureRegion toDraw = null;

        if(sprite != null){
            toDraw = sprite;
        }else if(animation != null){
            toDraw = animation.getKeyFrame(animationTime);
        }

        if(toDraw != null){
            batch.draw(toDraw, x, y, toDraw.getRegionWidth() / 2f, toDraw.getRegionHeight() / 2f, toDraw.getRegionWidth(), toDraw.getRegionHeight(), flipX ? -1 : 1, flipY ? -1 : 1, rotation);
        }
    }

    public void debugDraw(SpriteBatch batch, BitmapFont font){

    }
}
