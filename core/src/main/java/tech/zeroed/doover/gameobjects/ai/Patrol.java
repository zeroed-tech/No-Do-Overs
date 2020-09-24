package tech.zeroed.doover.gameobjects.ai;

import com.badlogic.gdx.graphics.Color;
import com.dongbat.jbump.Collisions;
import com.dongbat.jbump.Rect;
import tech.zeroed.doover.GameWorld;
import tech.zeroed.doover.gameobjects.GameObject;
import tech.zeroed.doover.gameobjects.enemy.Enemy;

import static tech.zeroed.doover.Colliders.GROUND_COLLISION_FILTER;

public class Patrol extends AI{

    private Collisions projectedCollisions = new Collisions();

    private float testXRight;
    private float testXLeft;
    private float testYBottom;
    private final float testSize = 0.5f;

    public Patrol(Enemy target) {
        super(target);
        testXLeft = target.boundingBoxX-target.moveSpeedX;
        testXRight = target.boundingBoxX+target.boundingBoxWidth+target.moveSpeedX;
        testYBottom = target.boundingBoxY-0.5f;
    }

    // get direction faced
    // Check if there is ground .1 in front

    @Override
    public void update(float delta) {
        GameWorld.world.project(null,
                target.x + target.boundingBoxX + (target.flipX ? 0 : target.boundingBoxWidth),
                target.y + testYBottom,
                testSize,
                testSize,
                target.x + (target.flipX ? testXLeft : testXRight),
                target.y + testYBottom,
                GROUND_COLLISION_FILTER,
                projectedCollisions);
        if (projectedCollisions.others.size() == 0) {
            target.flipX = !target.flipX;
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
                testSize, testSize);
        GameWorld.shapeDrawer.rectangle(rect.x, rect.y, rect.w, rect.h);
        GameWorld.shapeDrawer.setColor(oldColor);
    }
}
