package tech.zeroed.doover.gameobjects.traps;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import tech.zeroed.doover.GameWorld;

public class Spikes extends Traps {
    public static Array<TextureRegion> sprites = new Array<>();

    public Spikes() {
        super();
        sprite = GameWorld.worldSprites.findRegion("Spikes");
    }
}
