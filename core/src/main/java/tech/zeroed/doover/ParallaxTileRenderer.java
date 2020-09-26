package tech.zeroed.doover;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class ParallaxTileRenderer extends OrthogonalTiledMapRenderer {
    public int[] layersRendered;
    public ParallaxTileRenderer(TiledMap map, int[] layersRendered) {
        super(map);
        this.layersRendered = layersRendered;
    }

    @Override
    public void render() {
        super.render(layersRendered);
    }

    @Override
    public void renderTileLayer(TiledMapTileLayer layer) {
        float parallaxX = layer.getProperties().get("ParallaxX", 0f, Float.class);
        layer.setOffsetX(parallaxX * GameWorld.camera.position.x);
        super.renderTileLayer(layer);
    }
}
