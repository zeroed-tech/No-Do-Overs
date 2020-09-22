package tech.zeroed.doover.gameobjects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.dongbat.jbump.*;
import tech.zeroed.doover.GameWorld;

public class Crate extends GameObject {
    public static Array<TextureRegion> sprites = new Array<>();

    public static final CrateCollisionFilter CRATE_COLLISION_FILTER = new CrateCollisionFilter();

    public Crate(){
        sprite = sprites.get(0);
        this.boundingBoxWidth = GameWorld.TILE_SIZE-2;
        this.boundingBoxHeight = GameWorld.TILE_SIZE-2;
        this.boundingBoxX = 1;
        this.boundingBoxY = 1;
        this.gravityX = 0;
        this.gravityY = -25;

        item = new Item<>(this);
    }

    @Override
    public void run(float delta) {
        // Update physics for this object
        deltaX += delta * gravityX;
        deltaY += delta * gravityY;

        x += delta * deltaX;
        y += delta * deltaY;

        // Check and handle collisions
        Response.Result results = GameWorld.world.move(item, x + boundingBoxX, y + boundingBoxY, CRATE_COLLISION_FILTER);
        for(int i = 0; i < results.projectedCollisions.size(); i++){
            Collision collision = results.projectedCollisions.get(i);
            GameObject other = (GameObject) collision.other.userData;
            if(other instanceof Crate){
                if (collision.normal.y != 0) {
                    // Hit the floor
                    deltaY = 0;
                }

            }
        }

        // Update position based on collision results
        Rect rect = GameWorld.world.getRect(item);
        x = rect.x - boundingBoxX;
        y = rect.y - boundingBoxY;
    }

    public static class CrateCollisionFilter implements CollisionFilter {
        @Override
        public Response filter(Item item, Item other) {
            if(other.userData instanceof Ground | other.userData instanceof Crate){
                return Response.slide;
            }
            return null;
        }
    }
}
