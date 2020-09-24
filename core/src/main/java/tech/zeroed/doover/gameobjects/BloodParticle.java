package tech.zeroed.doover.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.dongbat.jbump.*;
import tech.zeroed.doover.GameWorld;

public class BloodParticle extends GameObject{
    public static Array<TextureRegion> sprites = new Array<>();
    public static BloodCollisionFilter BLOOD_COLLISION_FILTER = new BloodCollisionFilter();
    private Collisions projectedCollisions = new Collisions();

    public BloodParticle(){
        this.sprite = sprites.random();
        this.boundingBoxWidth = MathUtils.random(1,3);
        this.boundingBoxHeight = boundingBoxWidth;
        this.boundingBoxX = 0;
        this.boundingBoxY = 0;
        this.gravityX = 0;
        this.gravityY = -500;

        item = new Item<>(this);
    }

    @Override
    public void run(float delta) {
        deltaX += delta * gravityX;
        deltaY += delta * gravityY;

        GameWorld.world.project(item, x + boundingBoxX, y + boundingBoxY, boundingBoxWidth, boundingBoxY, x + boundingBoxX, y + boundingBoxY - 0.1f, BLOOD_COLLISION_FILTER, projectedCollisions);
        // If we aren't colliding with anything then we must be in the air
        boolean applyFriction = projectedCollisions.size() != 0;

        if(applyFriction){
            if(deltaX > 0){
                deltaX = MathUtils.clamp(deltaX-0.40f, 0, deltaX);
            }else if(deltaX < 0){
                deltaX = MathUtils.clamp(deltaX+0.40f, deltaX, 0);
            }
            if(deltaY < 0)
                deltaY = 0;
        }

        if(deltaX == 0 && deltaY == 0){
            GameWorld.removeGameObjectFromWorld(this);
        }

        x += deltaX * delta;
        y += deltaY * delta;


        GameWorld.world.move(item, x + boundingBoxX, y + boundingBoxY, BLOOD_COLLISION_FILTER);

        // Update position based on collision results
        Rect rect = GameWorld.world.getRect(item);
        if (rect != null) {
            x = rect.x - boundingBoxX;
            y = rect.y - boundingBoxY;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(sprite, x, y, boundingBoxWidth / 2f, boundingBoxHeight / 2f, boundingBoxWidth, boundingBoxHeight, 1, 1, rotation);
    }

    public static class BloodCollisionFilter implements CollisionFilter {
        @Override
        public Response filter(Item item, Item other) {
            if(other.userData instanceof Ground){
                return Response.slide;
            }
            return null;
        }
    }
}
