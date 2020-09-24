package tech.zeroed.doover.gameobjects.ai;

import tech.zeroed.doover.gameobjects.GameObject;
import tech.zeroed.doover.gameobjects.enemy.Enemy;

public abstract class AI {
    public Enemy target;

    public AI(Enemy target){
        this.target = target;
    }

    public abstract void update(float delta);

    public void debugDraw() {

    }
}
