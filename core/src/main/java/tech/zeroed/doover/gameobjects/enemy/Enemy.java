package tech.zeroed.doover.gameobjects.enemy;

import com.dongbat.jbump.Item;
import com.dongbat.jbump.Rect;
import com.dongbat.jbump.Response;
import tech.zeroed.doover.Colliders;
import tech.zeroed.doover.GameWorld;
import tech.zeroed.doover.gameobjects.GameObject;
import tech.zeroed.doover.gameobjects.Player;
import tech.zeroed.doover.gameobjects.ai.AI;

public abstract class Enemy extends GameObject {

    public AI ai;
    public float moveSpeedX;
    public float moveSpeedY;

    public Enemy(){
        this.boundingBoxWidth = GameWorld.TILE_SIZE;
        this.boundingBoxHeight = GameWorld.TILE_SIZE;
        this.gravityX = 0;
        this.gravityY = 0;

        item = new Item<>(this);
    }

    @Override
    public void run(float delta) {
        super.run(delta);
        ai.update(delta);

        deltaY += gravityY * delta;

        x += deltaX * delta;
        y += deltaY * delta;

        // Check and handle collisions
        Response.Result result = GameWorld.world.move(item, x + boundingBoxX, y + boundingBoxY, Colliders.Enemy_COLLISION_FILTER);
        if(result.projectedCollisions.others.size() > 0){
            for(Item item : result.projectedCollisions.others){
                if(item.userData instanceof Player){
                    ((Player) item.userData).kill();
                }
            }
        }

        // Update position based on collision results
        Rect rect = GameWorld.world.getRect(item);
        if(rect != null) {
            x = rect.x - boundingBoxX;
            y = rect.y - boundingBoxY;
        }
    }
}
