package tech.zeroed.doover;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import tech.zeroed.doover.gameobjects.*;
import tech.zeroed.doover.gameobjects.traps.Spikes;

public class Level {

    public static int TILE_HEIGHT;
    public static int TILE_WIDTH;

    public Vector2 player_spawn_point = new Vector2();

    public void Load(String mapName) {
        TiledMap tilemap = new TmxMapLoader().load("tilemap/"+mapName+".tmx");

        for(TiledMapTileSet tiledMapTiles : tilemap.getTileSets()){
            for(TiledMapTile tile : tiledMapTiles){
               switch (tile.getProperties().get("Type", "Unknown", String.class)){
                   case "Ground":
                       Ground.sprites.add(tile.getTextureRegion());
                       break;
                   case "Crate":
                       Crate.sprites.add(tile.getTextureRegion());
                       break;
                   case "Spikes":
                       Spikes.sprites.add(tile.getTextureRegion());
                       break;
                   default:
                       break;
               }
            }
        }

        // Initialise extra objects that generate their sprites
        // Create blood
        Pixmap pixmap = new Pixmap(10, 1, Pixmap.Format.RGBA8888);
        for(int i = 0; i < 10; i++){
            pixmap.setColor(new Color(MathUtils.random(0.5f, 1), 0, 0, MathUtils.random(0.5f, 1)));
            pixmap.drawPixel(i, 0);
        }
        Texture texture = new Texture(pixmap); //remember to dispose of later

        for(int i = 0; i < 10; i++) {
            BloodParticle.sprites.add(new TextureRegion(texture, i, 0, 1, 1));
        }

        // Create Bullets
        for(int i = 0; i < 10; i++){
            pixmap.setColor(new Color(0.8f, 0.8f, 0.8f, MathUtils.random(0.5f, 1)));
            pixmap.drawPixel(i, 0);
        }
        texture = new Texture(pixmap); //remember to dispose of later
        pixmap.dispose();

        for(int i = 0; i < 10; i++) {
            Bullet.sprites.add(new TextureRegion(texture, i, 0, 1, 1));
        }

        for(MapLayer layer : tilemap.getLayers()){
            TiledMapTileLayer tl = (TiledMapTileLayer)layer;
            for(int x = 0; x < tl.getWidth(); x++){
                for(int y = 0; y < tl.getHeight(); y++) {
                    TiledMapTileLayer.Cell cell = tl.getCell(x,y);

                    if(cell == null)continue;

                    TiledMapTile tile = cell.getTile();
                    GameObject toSpawn = null;
                    switch (tile.getProperties().get("Type", "Unknown", String.class)){
                        case "Ground":
                            toSpawn = new Ground();
                            break;
                        case "Crate":
                            toSpawn = new Crate();
                            break;
                        case "Spawn":
                            player_spawn_point.set(x * GameWorld.TILE_SIZE, y * GameWorld.TILE_SIZE);
                            break;
                        case "Spikes":
                            toSpawn = new Spikes();
                            break;
                        default:
                            break;
                    }
                    if(toSpawn != null) {
                        toSpawn.x = x * GameWorld.TILE_SIZE;
                        toSpawn.y = y * GameWorld.TILE_SIZE;

                        GameWorld.addGameObjectToWorld(toSpawn);
                    }
                }
            }
        }

        TILE_HEIGHT = ((TiledMapTileLayer)tilemap.getLayers().get(0)).getHeight();
        TILE_WIDTH = ((TiledMapTileLayer)tilemap.getLayers().get(0)).getWidth();
        spawnPlayer();
    }

    public void spawnPlayer(){
        Player player = new Player();
        player.x = player_spawn_point.x;
        player.y = player_spawn_point.y;
        GameWorld.addGameObjectToWorld(player);
    }
}
