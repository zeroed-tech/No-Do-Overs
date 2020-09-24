package tech.zeroed.doover.gameobjects;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.dongbat.jbump.Item;
import tech.zeroed.doover.GameWorld;

import static tech.zeroed.doover.GameWorld.addGameObjectToWorld;
import static tech.zeroed.doover.GameWorld.level;

public abstract class GameObject {
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

    public boolean dead = false;

    public Item<GameObject> item;

    public void kill(){
        if(dead) return;
        dead = true;
        GameWorld.removeGameObjectFromWorld(this);

        for(int i = 0; i < 100; i++){
            BloodParticle particle = new BloodParticle();
            particle.x = MathUtils.random(x, x+boundingBoxWidth);
            particle.y = MathUtils.random(y, y+boundingBoxHeight);;
            particle.deltaX = MathUtils.random(-20, 20);
            particle.deltaY = MathUtils.random(100, 250);
            addGameObjectToWorld(particle);
        }
    }

    /**
     * Called in the update loop. Should be overridden by subclasses to implement any
     * update logic needed
     * @param delta time in seconds since the last update cycle
     */
    public void run(float delta){
        animationTime += delta;
    }

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

    public void debugDraw(){

    }
}
