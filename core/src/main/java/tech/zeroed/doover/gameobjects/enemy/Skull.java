package tech.zeroed.doover.gameobjects.enemy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Rect;
import com.dongbat.jbump.Response;
import tech.zeroed.doover.Colliders;
import tech.zeroed.doover.GameWorld;
import tech.zeroed.doover.gameobjects.GameObject;
import tech.zeroed.doover.gameobjects.ai.Patrol;

import static tech.zeroed.doover.GameWorld.worldSprites;

public class Skull extends Enemy {

    public Animation<TextureAtlas.AtlasRegion> idle_animation;
    public Animation<TextureAtlas.AtlasRegion> run_animation;

    public Skull(){
        super();
        idle_animation = new Animation<>(1 / 10f, worldSprites.findRegions("skelet_idle_anim"), Animation.PlayMode.LOOP);
        run_animation = new Animation<>(1 / 10f, worldSprites.findRegions("skelet_run_anim_0"), Animation.PlayMode.LOOP);
        animation = idle_animation;

        this.boundingBoxWidth = 10;
        this.boundingBoxHeight = 12;
        this.boundingBoxY = 0;
        this.boundingBoxX = 3;
        this.gravityX = 0;
        this.gravityY = -100;

        this.moveSpeedX = 30;
        this.moveSpeedY = 0;

        ai = new Patrol(this);
    }
    
    @Override
    public void run(float delta) {
        super.run(delta);
        ai.update(delta);

        deltaY += gravityY * delta;

        x += deltaX * delta;
        y += deltaY * delta;

        // Check and handle collisions
        GameWorld.world.move(item, x + boundingBoxX, y + boundingBoxY, Colliders.GROUND_COLLISION_FILTER);

        // Update position based on collision results
        Rect rect = GameWorld.world.getRect(item);
        if(rect != null) {
            x = rect.x - boundingBoxX;
            y = rect.y - boundingBoxY;
        }
    }

    @Override
    public void debugDraw() {
        super.debugDraw();
        ai.debugDraw();
    }
}
