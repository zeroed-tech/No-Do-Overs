package tech.zeroed.doover.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.dongbat.jbump.*;
import tech.zeroed.doover.GameWorld;
import tech.zeroed.doover.gameobjects.traps.Traps;
import text.formic.Stringf;

import static tech.zeroed.doover.GameWorld.*;

public class Player extends GameObject{

    public static PlayerCollisionFilter PLAYER_COLLISION_FILTER = new PlayerCollisionFilter();

    public Animation<TextureAtlas.AtlasRegion> idle_animation;
    public Animation<TextureAtlas.AtlasRegion> run_animation;
    public Animation<TextureAtlas.AtlasRegion> jump_animation;


    // Player constants
    public static float ACCELERATION = 1800f;
    public static float RUN_SPEED = 300f;
    public static float JUMP_SPEED = 600;
    public static float GRAVITY = 2000f;

    public static float SHOTGUN_KNOCK_BACK = 700f;
    public static float SHOTGUN_RELOAD_RATE = 0.6f;

    public static float TERMINAL_VELOCITY = 900;

    private boolean jumping = false;
    private boolean inAir = false;
    private boolean flipped = false;
    private Collisions projectedCollisions = new Collisions();

    private float gunReloadDelay = 0;

    public Player(){
        idle_animation = new Animation<>(1 / 10f, worldSprites.findRegions("knight_m_idle_anim"), Animation.PlayMode.LOOP);
        run_animation = new Animation<>(1 / 10f, worldSprites.findRegions("knight_m_run_anim"), Animation.PlayMode.LOOP);
        jump_animation = new Animation<>(1 / 10f, worldSprites.findRegions("knight_m_hit_anim"), Animation.PlayMode.LOOP);
        animation = idle_animation;
        sprite = worldSprites.findRegion("Gun");
        animationTime = 1;
        this.boundingBoxWidth = 9;
        this.boundingBoxHeight = 14;
        this.boundingBoxX = 4;
        this.boundingBoxY = 0;
        this.gravityX = 0;
        this.gravityY = -GRAVITY;

        item = new Item<>(this);
    }

    public void kill(){
        if(dead) return;
        super.kill();

        level.spawnPlayer();
    }

    @Override
    public void run(float delta) {
        super.run(delta);
        if(dead) return;

        gunReloadDelay -= delta;
        if(gunReloadDelay < 0)
            gunReloadDelay = 0;

        boolean left  = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean up    = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean down  = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean jump  = Gdx.input.isKeyPressed(Input.Keys.Z);
        boolean shoot = Gdx.input.isKeyPressed(Input.Keys.SPACE);

        boolean killSwitch = Gdx.input.isKeyJustPressed(Input.Keys.GRAVE);

        if(killSwitch) {
            kill();
        }

        if(right || left){
            // An arrow key is being pressed

            // Check if the current direction is the same as on the last update
            flipped = flipX != left;
            flipX = left;

            if(flipped){
                // Halve the characters current movement to make them change directions faster
                deltaX = deltaX/2;
            }

            float moveAmount = ACCELERATION * delta * (flipX ? -1 : 1);
            deltaX = MathUtils.clamp(deltaX + moveAmount, -RUN_SPEED, RUN_SPEED);

            animation = run_animation;
        }else{
            // Decelerate
            if(deltaX > 0){
                deltaX = MathUtils.clamp(deltaX - (ACCELERATION * (jumping ? 0.5f : 1)) * delta, 0, RUN_SPEED);
            }else if(deltaX < 0){
                deltaX = MathUtils.clamp(deltaX + (ACCELERATION * (jumping ? 0.5f : 1)) * delta, -RUN_SPEED, 0);
            }else{
                animation = idle_animation;
            }
        }

        // Check if the player is on the ground
        GameWorld.world.project(item, x + boundingBoxX, y + boundingBoxY, boundingBoxWidth, boundingBoxY, x + boundingBoxX, y + boundingBoxY - 0.1f, PLAYER_COLLISION_FILTER, projectedCollisions);
        // If we aren't colliding with anything then we must be in the air
        inAir = projectedCollisions.size() == 0;

        // The player is currently on the ground and has just pressed the jump key
        if(!jumping && !inAir && jump) {
            deltaY = JUMP_SPEED;
            jumping = true;
        }

        if(shoot && gunReloadDelay <= 0){
            // Shooting
            for(int i = 0; i < 9; i++){
                Bullet bullet = new Bullet(MathUtils.random(0.25f, 0.4f));
                if(down || up){
                    bullet.deltaX = MathUtils.random(-30, 30);
                    bullet.deltaY = MathUtils.random(300, 350) * (down ? -1 : 1);
                    bullet.x = x + boundingBoxWidth/2;
                    bullet.y = y + boundingBoxHeight/2;
                }else{
                    bullet.deltaX = MathUtils.random(400, 450) * (flipX ? -1 : 1);
                    bullet.deltaY = MathUtils.random(-30, 30);
                    if(flipX) {
                        bullet.x = x - boundingBoxWidth;
                    }else{
                        bullet.x = x + boundingBoxWidth * 2;
                    }
                    bullet.y = y + boundingBoxHeight/2-3;
                }
                GameWorld.addGameObjectToWorld(bullet);
            }

            // Knock back
            if(down || up){
                deltaY = SHOTGUN_KNOCK_BACK * (up ? -1 : 1);
            }else {
                deltaX -= (SHOTGUN_KNOCK_BACK * (flipX ? -1 : 1)) * (inAir ? 1 : 0) ;
            }

            gunReloadDelay = SHOTGUN_RELOAD_RATE;
        }

        // Update physics for this object
        deltaX += gravityX * delta;
        if(deltaY > -TERMINAL_VELOCITY)
            deltaY += gravityY * delta;

        // Prevent the player from falling faster and faster
        if(deltaY < 0)
            deltaY = MathUtils.clamp(deltaY, -TERMINAL_VELOCITY*2, 0);

        x += deltaX * delta;
        y += deltaY * delta;

        // Check and handle collisions
        Response.Result results = GameWorld.world.move(item, x + boundingBoxX, y + boundingBoxY, PLAYER_COLLISION_FILTER);
        for(int i = 0; i < results.projectedCollisions.size(); i++){
            Collision collision = results.projectedCollisions.get(i);
            GameObject other = (GameObject) collision.other.userData;
            if(other instanceof Ground || other instanceof Crate){
                if(collision.normal.x != 0){
                    // Hit a wall

                }

                if (collision.normal.y != 0) {
                    // Hit the ceiling or floor
                    deltaY = 0;

                    if(collision.normal.y == 1){
                        // Hit the floor
                        jumping = false;
                        inAir = false;
                    }
                }
            }else if(other instanceof Traps){
                kill();
            }
        }

        // Update position based on collision results
        Rect rect = GameWorld.world.getRect(item);
        if(rect != null) {
            x = rect.x - boundingBoxX;
            y = rect.y - boundingBoxY;
        }

        if(y < -100)
            y = 1000;


        GameWorld.lookAt(x, y);
    }

    @Override
    public void draw(SpriteBatch batch) {
        TextureAtlas.AtlasRegion region = animation.getKeyFrame(animationTime);
        batch.draw(region, x, y, region.getRegionWidth() / 2f, region.getRegionHeight() / 2f, region.getRegionWidth(), region.getRegionHeight(), flipX ? -1 : 1, flipY ? -1 : 1, rotation);
        batch.draw(sprite, x+(region.getRegionWidth()/2f * (flipX ? -1 : 1)), y+2, sprite.getRegionWidth() / 2f, sprite.getRegionHeight() / 2f, sprite.getRegionWidth(), sprite.getRegionHeight(), flipX ? -1 : 1, flipY ? -1 : 1, MathUtils.clamp(360*(gunReloadDelay/SHOTGUN_RELOAD_RATE), 0, 360));
    }

    @Override
    public void debugDraw() {
        super.debugDraw();
//        font.draw(GameWorld.spriteBatch, Stringf.format(
//                         "Jumping:   %s\n" +
//                         "In Air:    %s\n" +
//                         "X:                %.2f\n" +
//                         "Y:                %.2f\n" +
//                         "deltaX:       %.2f\n" +
//                         "deltaY:       %.2f\n" +
//                         "Gun Reload:   %.2f\n"
//                , jumping, inAir, x, y, deltaX, deltaY, gunReloadDelay),
//                -hudCamera.viewportWidth/2+10,  hudCamera.viewportHeight/2-10 - font.getLineHeight());
    }

    public static class PlayerCollisionFilter implements CollisionFilter{
        @Override
        public Response filter(Item item, Item other) {
            if(other.userData instanceof Crate || other.userData instanceof Ground){
                return Response.slide;
            }
            if(other.userData instanceof Traps){
                return Response.cross;
            }
            return null;
        }
    }
}
