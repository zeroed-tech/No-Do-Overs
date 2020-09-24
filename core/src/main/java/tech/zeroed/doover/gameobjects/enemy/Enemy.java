package tech.zeroed.doover.gameobjects.enemy;

import com.dongbat.jbump.Item;
import tech.zeroed.doover.GameWorld;
import tech.zeroed.doover.gameobjects.GameObject;
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
}
