package tech.zeroed.doover.gameobjects.ai;

import com.badlogic.gdx.math.MathUtils;
import tech.zeroed.doover.gameobjects.enemy.Boss;
import tech.zeroed.doover.gameobjects.enemy.Enemy;

public class HoverPatrol extends Patrol {

    public float verticalVariance;

    public HoverPatrol(Enemy target) {
        super(target, true);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if(target instanceof Boss) {
            target.y = ((Boss) target).hoverHeight + MathUtils.sin(target.x * 0.1f) * verticalVariance;
        }
    }
}
