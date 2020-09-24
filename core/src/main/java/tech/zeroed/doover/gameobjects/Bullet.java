package tech.zeroed.doover.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.dongbat.jbump.*;
import tech.zeroed.doover.Colliders;
import tech.zeroed.doover.GameWorld;
import tech.zeroed.doover.gameobjects.enemy.Enemy;

public class Bullet extends GameObject {
    public static Array<TextureRegion> sprites = new Array<>();

    private static BulletCollisionFilter BULLET_COLLISION_FILTER = new BulletCollisionFilter();

    private float ttl;

    public Bullet(float ttl){
        this.ttl = ttl;

        this.boundingBoxX = 0;
        this.boundingBoxY = 0;
        this.gravityX = 0;
        this.gravityY = 0;

        this.sprite = sprites.random();
        this.boundingBoxWidth = MathUtils.random(3,5);
        this.boundingBoxHeight = boundingBoxWidth;

        item = new Item<>(this);
    }

    @Override
    public void run(float delta) {
        if(dead)
            return;
        ttl -= delta;

        if(ttl > 0) {
            x += deltaX * delta;
            y += deltaY * delta;

            Response.Result result = GameWorld.world.move(item, x + boundingBoxX, y + boundingBoxY, BULLET_COLLISION_FILTER);
            if(result.projectedCollisions.size() > 0){
                Item item = result.projectedCollisions.others.get(0);
                GameWorld.removeGameObjectFromWorld(this);
                dead = true;
                if(item.userData instanceof Enemy){
                    ((Enemy) item.userData).kill();;
                }
                // No point updating the bullet if we just destroyed it
                return;
            }

            // Update position based on collision results
            Rect rect = GameWorld.world.getRect(item);
            if (rect != null) {
                x = rect.x - boundingBoxX;
                y = rect.y - boundingBoxY;
            }
        }else{
            GameWorld.removeGameObjectFromWorld(this);
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(sprite, x, y, boundingBoxWidth / 2f, boundingBoxHeight / 2f, boundingBoxWidth, boundingBoxHeight, 1, 1, rotation);
    }

    public static class BulletCollisionFilter implements CollisionFilter{
        @Override
        public Response filter(Item item, Item other) {
            if(other.userData instanceof Ground || other.userData instanceof Enemy){
                return Response.cross;
            }
            return null;
        }
    }
}
