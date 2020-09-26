package tech.zeroed.doover.gameobjects.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import tech.zeroed.doover.GameWorld;
import tech.zeroed.doover.Level;
import tech.zeroed.doover.gameobjects.enemy.Enemy;

public class BossAI extends AI {
    private STATE state;

    public enum STATE {
        Hover,
        Swoop,
        Null
    }

    // Can be changed depending on the boss's current state
    private AI subAI;

    public BossAI(Enemy target) {
        super(target);
        setState(STATE.Null);
    }

    public void setState(STATE state){
        this.state = state;
        AI newAi = null;
        switch (state){
            case Hover:
                HoverPatrol patrol = new HoverPatrol(target){
                    @Override
                    public void changeDirection() {
                        super.changeDirection();
                        if(MathUtils.randomBoolean(0.5f)){
                            setState(STATE.Swoop);
                        }
                    }
                };
                patrol.testXLeft = target.boundingBoxX-target.moveSpeedX;
                patrol.testXRight = target.boundingBoxX+target.boundingBoxWidth+target.moveSpeedX;
                patrol.testYBottom = target.boundingBoxY -0.5f;
                patrol.testSizeH = target.boundingBoxHeight+2;
                patrol.verticalVariance = 15;
                newAi = patrol;
                break;
            case Swoop:
                Swoop swoop = new Swoop(target){
                    @Override
                    public void swoopComplete() {
                        super.swoopComplete();
                        if(flip){
                            setState(STATE.Hover);
                        }
                    }
                };

                float arcTop = (Level.CURRENT_LEVEL_HEIGHT_TILES-3.5f) * Level.TILE_WIDTH;
                float arcBottom = -50;

                swoop.p1.set(Level.TILE_WIDTH*4f, arcTop);
                swoop.p2.set(0, arcBottom);
                swoop.p3.set((Level.CURRENT_LEVEL_WIDTH_TILES) * Level.TILE_WIDTH, arcBottom);
                swoop.p4.set((Level.CURRENT_LEVEL_WIDTH_TILES-4f) * Level.TILE_WIDTH, arcTop);

                if(target.x > Level.CURRENT_LEVEL_WIDTH_TILES*Level.TILE_WIDTH/2f){
                    swoop.alpha = 1;
                    swoop.flip = true;
                    swoop.p4.set(target.x+target.boundingBoxWidth/2, target.y+target.boundingBoxHeight);
                }else{
                    swoop.p1.set(target.x, target.y);
                }

                newAi = swoop;
                break;
            case Null:
                newAi = new NullAI(target);
                break;
        }
        subAI = newAi;
    }

    public STATE getState() {
        return state;
    }

    @Override
    public void update(float delta) {
        subAI.update(delta);

    }

    @Override
    public void debugDraw() {
        super.debugDraw();
        subAI.debugDraw();
    }
}
