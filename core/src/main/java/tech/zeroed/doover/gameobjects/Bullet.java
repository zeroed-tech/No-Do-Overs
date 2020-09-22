package tech.zeroed.doover.gameobjects;

import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Rect;
import com.dongbat.jbump.Response;
import tech.zeroed.doover.GameWorld;

public class Bullet extends GameObject {

    private static BulletCollisionFilter BULLET_COLLISION_FILTER = new BulletCollisionFilter();

    private float ttl;

    public Bullet(float ttl){
        this.ttl = ttl;
        this.boundingBoxWidth = 5;
        this.boundingBoxHeight = 5;
        this.boundingBoxX = 0;
        this.boundingBoxY = 0;
        this.gravityX = 0;
        this.gravityY = 0;

        item = new Item<>(this);
    }

    @Override
    public void run(float delta) {
        ttl -= delta;

        if(ttl > 0) {
            x += deltaX * delta;
            y += deltaY * delta;

            GameWorld.world.move(item, x + boundingBoxX, y + boundingBoxY, BULLET_COLLISION_FILTER);

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

    public static class BulletCollisionFilter implements CollisionFilter{
        @Override
        public Response filter(Item item, Item other) {
            return null;
        }
    }
}
