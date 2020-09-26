package tech.zeroed.doover.gameobjects.ai;

import tech.zeroed.doover.gameobjects.enemy.Enemy;

public class NullAI extends AI {
    public NullAI(Enemy target) {
        super(target);
    }

    @Override
    public void update(float delta) {

    }
}
