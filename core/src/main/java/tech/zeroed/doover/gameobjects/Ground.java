package tech.zeroed.doover.gameobjects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.dongbat.jbump.Item;
import tech.zeroed.doover.GameWorld;

public class Ground extends GameObject {
    public static Array<TextureRegion> sprites = new Array<>();

    public Ground(){
        sprite = sprites.get(2);
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
