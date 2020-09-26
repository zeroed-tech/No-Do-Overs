package tech.zeroed.doover;

import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Response;
import tech.zeroed.doover.gameobjects.Bullet;
import tech.zeroed.doover.gameobjects.Crate;
import tech.zeroed.doover.gameobjects.Ground;
import tech.zeroed.doover.gameobjects.Player;
import tech.zeroed.doover.gameobjects.traps.Traps;

public class Colliders {

    public static GroundCollisionFilter GROUND_COLLISION_FILTER = new GroundCollisionFilter();
    public static EnemyCollisionFilter Enemy_COLLISION_FILTER = new EnemyCollisionFilter();
    public static BulletCollisionFilter BULLET_COLLISION_FILTER = new BulletCollisionFilter();


    private static class BulletCollisionFilter implements CollisionFilter {
        @Override
        public Response filter(Item item, Item other) {
            if(other.userData instanceof Bullet){
                return Response.cross;
            }
            return null;
        }
    }

    private static class EnemyCollisionFilter implements CollisionFilter {
        @Override
        public Response filter(Item item, Item other) {
            if(other.userData instanceof Ground) return Response.slide;
            if(other.userData instanceof Player) return Response.cross;
            return null;
        }
    }

    private static class GroundCollisionFilter implements CollisionFilter {
        @Override
        public Response filter(Item item, Item other) {
            if(other.userData instanceof Crate || other.userData instanceof Ground){
                return Response.slide;
            }
            return null;
        }
    }
}
