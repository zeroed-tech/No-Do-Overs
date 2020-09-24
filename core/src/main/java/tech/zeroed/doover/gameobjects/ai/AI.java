package tech.zeroed.doover.gameobjects.ai;

import tech.zeroed.doover.gameobjects.GameObject;

public abstract class AI {
    public GameObject target;

    public AI(GameObject target){
        this.target = target;
    }

    abstract void update(float delta);
}
