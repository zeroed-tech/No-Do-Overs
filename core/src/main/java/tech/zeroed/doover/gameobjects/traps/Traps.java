package tech.zeroed.doover.gameobjects.traps;

import com.dongbat.jbump.Item;
import tech.zeroed.doover.GameWorld;
import tech.zeroed.doover.gameobjects.GameObject;

public abstract class Traps extends GameObject {

    public Traps(){
        this.boundingBoxWidth = GameWorld.TILE_SIZE;
        this.boundingBoxHeight = GameWorld.TILE_SIZE;
        this.gravityX = 0;
        this.gravityY = 0;

        item = new Item<>(this);
    }

    @Override
    public void run(float delta) {

    }
}
