package tech.zeroed.doover.gameobjects.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.dongbat.jbump.Collisions;
import com.dongbat.jbump.Rect;
import tech.zeroed.doover.GameWorld;
import tech.zeroed.doover.gameobjects.GameObject;
import tech.zeroed.doover.gameobjects.enemy.Enemy;

import static tech.zeroed.doover.Colliders.GROUND_COLLISION_FILTER;

public class Patrol extends AI{

    private Collisions projectedCollisions = new Collisions();

    protected float testXRight;
    protected float testXLeft;
    protected float testYBottom;
    private boolean changeOnCollision;
    protected float testSizeW = 0.5f;
    protected float testSizeH = 0.5f;

    /**
     *
     * @param target Unit to affect
     * @param changeOnCollision If true, the unit will change directions when they collide with a wall (hovering)
     *                          If false, the unit will change directions when they stop colliding with a wall (walking)
     */
    public Patrol(Enemy target, boolean changeOnCollision) {
        super(target);
        testXLeft = target.boundingBoxX-target.moveSpeedX;
        testXRight = target.boundingBoxX+target.boundingBoxWidth+target.moveSpeedX;
        testYBottom = target.boundingBoxY-0.5f;
        this.changeOnCollision = changeOnCollision;
    }

    public void changeDirection(){
        Gdx.app.log("Hit a wall", "Changing directions");
    }

    @Override
    public void update(float delta) {
        // get direction faced
        // Check if there is ground .1 in front
        GameWorld.world.project(null,
                target.x + target.boundingBoxX + (target.flipX ? 0 : target.boundingBoxWidth),
                target.y + testYBottom,
                testSizeW,
                testSizeH,
                target.x + (target.flipX ? testXLeft : testXRight)*delta,
                target.y + testYBottom,
                GROUND_COLLISION_FILTER,
                projectedCollisions);
        if (changeOnCollision && projectedCollisions.others.size() != 0) {
            target.flipX = !target.flipX;
            changeDirection();
        }
        target.deltaX = target.flipX ? -target.moveSpeedX : target.moveSpeedX;
    }

    @Override
    public void debugDraw() {
        float oldColor = GameWorld.shapeDrawer.getPackedColor();
        GameWorld.shapeDrawer.setColor(Color.GREEN);
        GameWorld.shapeDrawer.setDefaultLineWidth(1f);
        Rect rect = new Rect(
                target.x + target.boundingBoxX + (target.flipX ? 0 : target.boundingBoxWidth),
                target.y + testYBottom,
                testSizeW, testSizeH);
        GameWorld.shapeDrawer.rectangle(rect.x, rect.y, rect.w, rect.h);
        GameWorld.shapeDrawer.setColor(oldColor);
    }
}
