package tech.zeroed.doover;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.*;
import tech.zeroed.doover.gameobjects.Crate;
import tech.zeroed.doover.gameobjects.GameObject;
import tech.zeroed.doover.gameobjects.Ground;
import tech.zeroed.doover.gameobjects.Player;

public class Level {

    public static int TILE_HEIGHT;
    public static int TILE_WIDTH;

    public static void Load(String mapName) {
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
                   default:
                       break;
               }
            }
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
                            toSpawn = new Player();
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
    }
}
