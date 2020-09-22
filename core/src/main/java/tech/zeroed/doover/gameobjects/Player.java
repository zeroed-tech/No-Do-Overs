package tech.zeroed.doover.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.dongbat.jbump.*;
import tech.zeroed.doover.GameWorld;
import text.formic.Stringf;

import static tech.zeroed.doover.GameWorld.hudCamera;
import static tech.zeroed.doover.GameWorld.worldSprites;

public class Player extends GameObject{

    public static PlayerCollisionFilter PLAYER_COLLISION_FILTER = new PlayerCollisionFilter();



    // Player constants
    public static float ACCELERATION = 1800f;
    public static float RUN_SPEED = 500f;
    public static float JUMP_SPEED = 950;
    public static float GRAVITY = 3000f;

    public static float SHOTGUN_KNOCK_BACK = 1000f;
    public static float SHOTGUN_RELOAD_RATE = 0.6f;

    public static float TERMINAL_VELOCITY = 900;

    private boolean jumping = false;
    private boolean inAir = false;
    private boolean flipped = false;
    private Collisions projectedCollisions = new Collisions();

    private float gunReloadDelay = 0;


    public Player(){
        animation = new Animation<>(1 / 30f, worldSprites.findRegions("Player"), Animation.PlayMode.LOOP);
        sprite = worldSprites.findRegion("Gun");
        animationTime = 1;
        this.boundingBoxWidth = 15;
        this.boundingBoxHeight = 30;
        this.boundingBoxX = 0;
        this.boundingBoxY = 0;
        this.gravityX = 0;
        this.gravityY = -GRAVITY;

        item = new Item<>(this);
    }

    @Override
    public void run(float delta) {

        animationTime += delta;
        gunReloadDelay -= delta;

        boolean left  = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean up    = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean down  = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean jump  = Gdx.input.isKeyPressed(Input.Keys.Z);
        boolean shoot = Gdx.input.isKeyJustPressed(Input.Keys.SPACE);

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
        }else{
            // Decelerate
            if(deltaX > 0){
                deltaX = MathUtils.clamp(deltaX - (ACCELERATION * (jumping ? 0.5f : 1)) * delta, 0, RUN_SPEED);
            }else if(deltaX < 0){
                deltaX = MathUtils.clamp(deltaX + (ACCELERATION * (jumping ? 0.5f : 1)) * delta, -RUN_SPEED, 0);
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
                    bullet.y = y + boundingBoxHeight/2+5;
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

        GameWorld.camera.position.set(x, y, 0);
    }

    @Override
    public void draw(SpriteBatch batch) {
        TextureAtlas.AtlasRegion region = animation.getKeyFrame(animationTime);
        batch.draw(region, x, y, region.getRegionWidth() / 2f, region.getRegionHeight() / 2f, region.getRegionWidth(), region.getRegionHeight(), flipX ? -1 : 1, flipY ? -1 : 1, rotation);
        batch.draw(sprite, x+(region.getRegionHeight()/2f * (flipX ? -1 : 1)), y+boundingBoxHeight/2, sprite.getRegionWidth() / 2f, sprite.getRegionHeight() / 2f, sprite.getRegionWidth(), sprite.getRegionHeight(), flipX ? -1 : 1, flipY ? -1 : 1, MathUtils.clamp(360*(gunReloadDelay/SHOTGUN_RELOAD_RATE), 0, 360));
    }

    @Override
    public void debugDraw(SpriteBatch batch, BitmapFont font) {
        super.debugDraw(batch, font);
        font.draw(batch, Stringf.format(
                         "Jumping:   %s\n" +
                         "In Air:    %s\n" +
                         "X:                %.2f\n" +
                         "Y:                %.2f\n" +
                         "deltaX:       %.2f\n" +
                         "deltaY:       %.2f\n" +
                         "Gun Reload:   %.2f\n"
                , jumping, inAir, x, y, deltaX, deltaY, gunReloadDelay),
                -hudCamera.viewportWidth/2+10,  hudCamera.viewportHeight/2-10 - font.getLineHeight());
    }

    public static class PlayerCollisionFilter implements CollisionFilter{
        @Override
        public Response filter(Item item, Item other) {
            if(other.userData instanceof Crate || other.userData instanceof Ground){
                return Response.slide;
            }
            return null;
        }
    }
}
